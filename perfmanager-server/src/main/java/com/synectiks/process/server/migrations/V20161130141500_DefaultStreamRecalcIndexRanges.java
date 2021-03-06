/*
 * */
package com.synectiks.process.server.migrations;

import org.mongojack.DBQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.indexer.MongoIndexSet;
import com.synectiks.process.server.indexer.cluster.Cluster;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indexset.IndexSetService;
import com.synectiks.process.server.indexer.indices.TooManyAliasesException;
import com.synectiks.process.server.indexer.ranges.CreateNewSingleIndexRangeJob;
import com.synectiks.process.server.indexer.ranges.IndexRange;
import com.synectiks.process.server.indexer.ranges.IndexRangeService;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class V20161130141500_DefaultStreamRecalcIndexRanges extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(V20161130141500_DefaultStreamRecalcIndexRanges.class);

    private final IndexSetRegistry indexSetRegistry;
    private final IndexSetService indexSetService;
    private final MongoIndexSet.Factory indexSetFactory;
    private final IndexRangeService indexRangeService;
    private final CreateNewSingleIndexRangeJob.Factory rebuildIndexRangeJobFactory;
    private final Cluster cluster;

    @Inject
    public V20161130141500_DefaultStreamRecalcIndexRanges(final IndexSetRegistry indexSetRegistry,
                                                          final IndexSetService indexSetService,
                                                          final MongoIndexSet.Factory indexSetFactory,
                                                          final IndexRangeService indexRangeService,
                                                          final CreateNewSingleIndexRangeJob.Factory rebuildIndexRangeJobFactory,
                                                          final Cluster cluster) {
        this.indexSetRegistry = indexSetRegistry;
        this.indexSetService = indexSetService;
        this.indexSetFactory = indexSetFactory;
        this.indexRangeService = indexRangeService;
        this.rebuildIndexRangeJobFactory = rebuildIndexRangeJobFactory;
        this.cluster = cluster;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2016-11-30T14:15:00Z");
    }

    @Override
    public void upgrade() {
        IndexSet indexSet;
        try {
            indexSet = indexSetRegistry.getDefault();
        } catch (IllegalStateException e) {
            // Try to find the default index set manually if the index set registry cannot find it.
            // This is needed if you run this migration with a 2.2.0-beta.2 state before commit 460cac6af.
            final IndexSetConfig indexSetConfig = indexSetService.findOne(DBQuery.is("default", true))
                    .orElseThrow(() -> new IllegalStateException("No default index set configured! This is a bug!"));
            indexSet = indexSetFactory.create(indexSetConfig);
        }

        final IndexSet defaultIndexSet = indexSet;

        if (!cluster.isConnected()) {
            LOG.info("Cluster not connected yet, delaying migration until it is reachable.");
            while (true) {
                try {
                    cluster.waitForConnectedAndDeflectorHealthy();
                    break;
                } catch (InterruptedException | TimeoutException e) {
                    LOG.warn("Interrupted or timed out waiting for Elasticsearch cluster, checking again.");
                }
            }
        }
        final Set<String> indexRangesWithoutStreams = indexRangeService.findAll().stream()
                .filter(indexRange -> defaultIndexSet.isManagedIndex(indexRange.indexName()))
                .filter(indexRange -> indexRange.streamIds() == null)
                .map(IndexRange::indexName)
                .collect(Collectors.toSet());

        if (indexRangesWithoutStreams.size() == 0) {
            // all ranges have a stream list, even if it is empty, nothing more to do
            return;
        }

        final String currentWriteTarget;
        try {
            currentWriteTarget = defaultIndexSet.getActiveWriteIndex();
        } catch (TooManyAliasesException e) {
            LOG.error("Multiple write targets found for write alias. Cannot continue to assign streams to older indices", e);
            return;
        }
        for (String indexName : defaultIndexSet.getManagedIndices()) {
            if (indexName.equals(currentWriteTarget)) {
                // do not recalculate for current write target
                continue;
            }
            if (!indexRangesWithoutStreams.contains(indexName)) {
                // already computed streams for this index
                continue;
            }
            LOG.info("Recalculating streams in index {}", indexName);
            final CreateNewSingleIndexRangeJob createNewSingleIndexRangeJob = rebuildIndexRangeJobFactory.create(indexSetRegistry.getAll(), indexName);
            createNewSingleIndexRangeJob.execute();
        }

    }
}
