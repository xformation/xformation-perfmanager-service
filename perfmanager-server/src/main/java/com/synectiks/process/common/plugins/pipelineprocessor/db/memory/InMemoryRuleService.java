/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.db.memory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import com.synectiks.process.common.plugins.pipelineprocessor.db.RuleDao;
import com.synectiks.process.common.plugins.pipelineprocessor.db.RuleService;
import com.synectiks.process.common.plugins.pipelineprocessor.events.RulesChangedEvent;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.events.ClusterEventBus;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * A RuleService that does not persist any data, but simply keeps it in memory.
 */
public class InMemoryRuleService implements RuleService {
    // poor man's id generator
    private final AtomicLong idGen = new AtomicLong(0);

    private final Map<String, RuleDao> store = new ConcurrentHashMap<>();
    private final Map<String, String> titleToId = new ConcurrentHashMap<>();

    private final ClusterEventBus clusterBus;

    @Inject
    public InMemoryRuleService(ClusterEventBus clusterBus) {
        this.clusterBus = clusterBus;
    }

    @Override
    public RuleDao save(RuleDao rule) {
        RuleDao toSave = rule.id() != null
                ? rule
                : rule.toBuilder().id(createId()).build();
        // enforce the title unique constraint
        if (titleToId.containsKey(toSave.title())) {
            // if this is an update and the title belongs to the passed rule, then it's fine
            if (!titleToId.get(toSave.title()).equals(toSave.id())) {
                throw new IllegalArgumentException("Duplicate rule titles are not allowed: " + toSave.title());
            }
        }
        titleToId.put(toSave.title(), toSave.id());
        store.put(toSave.id(), toSave);

        clusterBus.post(RulesChangedEvent.updatedRuleId(toSave.id()));

        return toSave;
    }

    @Override
    public RuleDao load(String id) throws NotFoundException {
        final RuleDao rule = store.get(id);
        if (rule == null) {
            throw new NotFoundException("No such rule with id " + id);
        }
        return rule;
    }

    @Override
    public RuleDao loadByName(String name) throws NotFoundException {
        final String id = titleToId.get(name);
        if (id == null) {
            throw new NotFoundException("No rule with name " + name);
        }
        return load(id);
    }

    @Override
    public Collection<RuleDao> loadAll() {
        return ImmutableSet.copyOf(store.values());
    }

    @Override
    public void delete(String id) {
        if (id == null) {
            return;
        }
        final RuleDao removed = store.remove(id);
        // clean up title index if the rule existed
        if (removed != null) {
            titleToId.remove(removed.title());
        }
        clusterBus.post(RulesChangedEvent.deletedRuleId(id));
    }

    @Override
    public Collection<RuleDao> loadNamed(Collection<String> ruleNames) {
        final Set<String> needles = Sets.newHashSet(ruleNames);
        return store.values().stream()
                .filter(ruleDao -> needles.contains(ruleDao.title()))
                .collect(Collectors.toList());
    }

    private String createId() {
        return String.valueOf(idGen.incrementAndGet());
    }
}
