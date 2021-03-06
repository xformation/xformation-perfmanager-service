/*
 * */
package com.synectiks.process.server.periodical;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.synectiks.process.server.cluster.UserPermissionMigrationState;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.shared.security.Permissions;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.users.RoleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * This task migrates users' permission sets from pre-1.2 style to the built-in roles: admin and reader.
 *
 * It does so by preserving the existing stream and dashboard permissions, which can still be assigned to individual users.
 * It is recommend to migrate to using roles exclusively, but for now both styles will work.
 */
public class UserPermissionMigrationPeriodical extends Periodical {
    private static final Logger log = LoggerFactory.getLogger(UserPermissionMigrationPeriodical.class);

    private final UserService userService;
    private final RoleService roleService;
    private final Permissions permissions;
    private final ClusterConfigService clusterConfigService;

    @Inject
    public UserPermissionMigrationPeriodical(final UserService userService,
                                             final RoleService roleService,
                                             final Permissions permissions,
                                             final ClusterConfigService clusterConfigService) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissions = permissions;
        this.clusterConfigService = clusterConfigService;
    }

    @Override
    public void doRun() {
        final List<User> users = userService.loadAll();
        final String adminRoleId = roleService.getAdminRoleObjectId();
        final String readerRoleId = roleService.getReaderRoleObjectId();

        for (User user : users) {
            if (user.isLocalAdmin()) {
                log.debug("Skipping local admin user.");
                continue;
            }

            final Set<String> fixedPermissions = Sets.newHashSet();
            final Set<String> fixedRoleIds = Sets.newHashSet(user.getRoleIds());

            final Set<String> permissionSet = Sets.newHashSet(user.getPermissions());

            boolean hasWildcardPermission = permissionSet.contains("*");

            if (hasWildcardPermission && !user.getRoleIds().contains(adminRoleId)) {
                // need to add the admin role to this user
                fixedRoleIds.add(adminRoleId);
            }

            final Set<String> basePermissions = permissions.readerPermissions(user.getName());
            final boolean hasCompleteReaderSet = permissionSet.containsAll(basePermissions);

            // only migrate the user if it looks like a pre-1.2 user:
            //   - it has no roles
            //   - it has the base reader permission set
            //   - it has the wildcard permissions
            if (!user.getRoleIds().isEmpty() && hasCompleteReaderSet && hasWildcardPermission) {
                log.debug("Not migrating user {}, it has already been migrated.", user.getName());
                continue;
            }
            if (hasCompleteReaderSet && !user.getRoleIds().contains(readerRoleId)) {
                // need to add the reader role to this user
                fixedRoleIds.add(readerRoleId);
            }
            // filter out the individual permissions to dashboards and streams
            final List<String> dashboardStreamPermissions = Lists.newArrayList(
                    Sets.filter(permissionSet, permission -> !basePermissions.contains(permission) && !"*".equals(permission)));
            // add the minimal permission set back to the user
            fixedPermissions.addAll(permissions.userSelfEditPermissions(user.getName()));
            fixedPermissions.addAll(dashboardStreamPermissions);

            log.info("Migrating permissions to roles for user {} from permissions {} and roles {} to new permissions {} and roles {}",
                     user.getName(),
                     permissionSet,
                     user.getRoleIds(),
                     fixedPermissions,
                     fixedRoleIds);

            user.setRoleIds(fixedRoleIds);
            user.setPermissions(Lists.newArrayList(fixedPermissions));
            try {
                userService.save(user);
            } catch (ValidationException e) {
                log.error("Unable to migrate user permissions for user " + user.getName(), e);
            }
        }

        log.info("Marking user permission migration as done.");
        clusterConfigService.write(UserPermissionMigrationState.create(true));

    }

    @Override
    public boolean runsForever() {
        return true;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return false;
    }

    @Override
    public boolean masterOnly() {
        return true;
    }

    @Override
    public boolean startOnThisNode() {
        final UserPermissionMigrationState migrationState =
                clusterConfigService.getOrDefault(UserPermissionMigrationState.class,
                                                  UserPermissionMigrationState.create(false));
        // don't run again if the cluster config says we've already migrated the users
        return !migrationState.migrationDone();
    }

    @Override
    public boolean isDaemon() {
        return false;
    }

    @Override
    public int getInitialDelaySeconds() {
        return 0;
    }

    @Override
    public int getPeriodSeconds() {
        return 0;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
