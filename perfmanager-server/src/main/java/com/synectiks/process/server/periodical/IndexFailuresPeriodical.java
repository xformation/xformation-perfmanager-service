/*
 * */
package com.synectiks.process.server.periodical;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.synectiks.process.server.indexer.IndexFailureService;
import com.synectiks.process.server.indexer.messages.Messages;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.lifecycles.Lifecycle;
import com.synectiks.process.server.plugin.periodical.Periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class IndexFailuresPeriodical extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(IndexFailuresPeriodical.class);

    private final IndexFailureService indexFailureService;
    private final Messages messages;
    private final ServerStatus serverStatus;
    private final MetricRegistry metricRegistry;

    @Inject
    public IndexFailuresPeriodical(IndexFailureService indexFailureService,
                                   Messages messages,
                                   ServerStatus serverStatus,
                                   MetricRegistry metricRegistry) {
        this.indexFailureService = indexFailureService;
        this.messages = messages;
        this.serverStatus = serverStatus;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public void initialize() {
        metricRegistry.register(MetricRegistry.name(IndexFailuresPeriodical.class, "queueSize"), new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return messages.getIndexFailureQueue().size();
            }
        });
    }

    @Override
    public void doRun() {
        while (serverStatus.getLifecycle() != Lifecycle.HALTING) {
            try {
                messages.getIndexFailureQueue()
                        .take()
                        .forEach(indexFailureService::saveWithoutValidation);
            } catch (Exception e) {
                LOG.error("Could not persist index failure.", e);
            }
        }
    }

    @Override
    protected Logger getLogger() {
        return LOG;
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
}
