/*
 * */
package com.synectiks.process.server.periodical;

import com.github.joschi.jadconfig.util.Duration;
import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.plugin.system.NodeId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

public class GarbageCollectionWarningThread extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(GarbageCollectionWarningThread.class);

    private final List<GarbageCollectorMXBean> garbageCollectors;
    private final Duration gcWarningThreshold;
    private final NodeId nodeId;
    private final NotificationService notificationService;

    @Inject
    public GarbageCollectionWarningThread(final Configuration configuration,
                                          final NodeId nodeId,
                                          final NotificationService notificationService) {
        this(configuration.getGcWarningThreshold(), nodeId, notificationService);
    }

    GarbageCollectionWarningThread(final Duration gcWarningThreshold,
                                   final NodeId nodeId,
                                   final NotificationService notificationService) {
        this(ManagementFactory.getGarbageCollectorMXBeans(), gcWarningThreshold, nodeId, notificationService);
    }

    GarbageCollectionWarningThread(final List<GarbageCollectorMXBean> garbageCollectors,
                                   final Duration gcWarningThreshold,
                                   final NodeId nodeId,
                                   final NotificationService notificationService) {
        this.garbageCollectors = garbageCollectors;
        this.gcWarningThreshold = gcWarningThreshold;
        this.nodeId = nodeId;
        this.notificationService = notificationService;
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
        return false;
    }

    @Override
    public boolean startOnThisNode() {
        return true;
    }

    @Override
    public boolean isDaemon() {
        return true;
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
        return LOG;
    }

    @Override
    public void doRun() {
        for (final GarbageCollectorMXBean gc : garbageCollectors) {
            switch (gc.getName()) {
                case "ParNew":
                case "ConcurrentMarkSweep":
                    LOG.debug("Skipping GC warning listener for concurrent collector {}.", gc.getName());
                    continue;
            }
            LOG.debug("Installing GC warning listener for collector {}, total runtime threshold is {}.", gc.getName(), gcWarningThreshold);

            final NotificationEmitter emitter = (NotificationEmitter) gc;
            final NotificationListener listener = new NotificationListener() {
                @Override
                public void handleNotification(javax.management.Notification notification, Object handback) {
                    if (GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION.equals(notification.getType())) {
                        final GcInfo gcInfo = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData()).getGcInfo();
                        final Duration duration = Duration.milliseconds(gcInfo.getDuration());

                        if (duration.compareTo(gcWarningThreshold) > 0) {
                            LOG.warn("Last GC run with {} took longer than {} (last duration={})",
                                    gc.getName(), gcWarningThreshold, duration);

                            final Notification systemNotification = notificationService.buildNow()
                                    .addNode(nodeId.toString())
                                    .addTimestamp(Tools.nowUTC())
                                    .addSeverity(Notification.Severity.URGENT)
                                    .addType(Notification.Type.GC_TOO_LONG)
                                    .addDetail("gc_name", gc.getName())
                                    .addDetail("gc_duration_ms", duration.toMilliseconds())
                                    .addDetail("gc_threshold_ms", gcWarningThreshold.toMilliseconds())
                                    .addDetail("gc_collection_count", gc.getCollectionCount())
                                    .addDetail("gc_collection_time", gc.getCollectionTime());

                            if (!notificationService.publishIfFirst(systemNotification)) {
                                LOG.debug("Couldn't publish notification: {}", notification);
                            }
                        }
                    }
                }
            };
            emitter.addNotificationListener(listener, null, null);
        }
    }
}