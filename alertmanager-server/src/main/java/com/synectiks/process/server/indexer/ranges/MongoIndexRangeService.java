/*
 * */
package com.synectiks.process.server.indexer.ranges;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.primitives.Ints;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.synectiks.process.server.audit.AuditActor;
import com.synectiks.process.server.audit.AuditEventSender;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.indexer.indices.events.IndicesClosedEvent;
import com.synectiks.process.server.indexer.indices.events.IndicesDeletedEvent;
import com.synectiks.process.server.indexer.indices.events.IndicesReopenedEvent;
import com.synectiks.process.server.indexer.searches.IndexRangeStats;
import com.synectiks.process.server.plugin.system.NodeId;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static com.synectiks.process.server.audit.AuditEventTypes.ES_INDEX_RANGE_CREATE;
import static com.synectiks.process.server.audit.AuditEventTypes.ES_INDEX_RANGE_DELETE;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

public class MongoIndexRangeService implements IndexRangeService {
    private static final Logger LOG = LoggerFactory.getLogger(MongoIndexRangeService.class);
    private static final String COLLECTION_NAME = "index_ranges";

    private final Indices indices;
    private final IndexSetRegistry indexSetRegistry;
    private final AuditEventSender auditEventSender;
    private final NodeId nodeId;
    private final JacksonDBCollection<MongoIndexRange, ObjectId> collection;

    @Inject
    public MongoIndexRangeService(MongoConnection mongoConnection,
                                  MongoJackObjectMapperProvider objectMapperProvider,
                                  Indices indices,
                                  IndexSetRegistry indexSetRegistry,
                                  AuditEventSender auditEventSender,
                                  NodeId nodeId,
                                  EventBus eventBus) {
        this.indices = indices;
        this.indexSetRegistry = indexSetRegistry;
        this.auditEventSender = auditEventSender;
        this.nodeId = nodeId;
        this.collection = JacksonDBCollection.wrap(
            mongoConnection.getDatabase().getCollection(COLLECTION_NAME),
            MongoIndexRange.class,
            ObjectId.class,
            objectMapperProvider.get());

        eventBus.register(this);

        collection.createIndex(new BasicDBObject(MongoIndexRange.FIELD_INDEX_NAME, 1));
        collection.createIndex(BasicDBObjectBuilder.start()
            .add(MongoIndexRange.FIELD_BEGIN, 1)
            .add(MongoIndexRange.FIELD_END, 1)
            .get());
    }

    @Override
    public IndexRange get(String index) throws NotFoundException {
        final DBQuery.Query query = DBQuery.and(
            DBQuery.notExists("start"),
            DBQuery.is(IndexRange.FIELD_INDEX_NAME, index));
        final MongoIndexRange indexRange = collection.findOne(query);
        if (indexRange == null) {
            throw new NotFoundException("Index range for index <" + index + "> not found.");
        }

        return indexRange;
    }

    @Override
    public SortedSet<IndexRange> find(DateTime begin, DateTime end) {
        final DBQuery.Query query = DBQuery.or(
                DBQuery.and(
                        DBQuery.notExists("start"),  // "start" has been used by the old index ranges in MongoDB
                        DBQuery.lessThanEquals(IndexRange.FIELD_BEGIN, end.getMillis()),
                        DBQuery.greaterThanEquals(IndexRange.FIELD_END, begin.getMillis())
                ),
                DBQuery.and(
                        DBQuery.notExists("start"),  // "start" has been used by the old index ranges in MongoDB
                        DBQuery.lessThanEquals(IndexRange.FIELD_BEGIN, 0L),
                        DBQuery.greaterThanEquals(IndexRange.FIELD_END, 0L)
                )
        );

        try (DBCursor<MongoIndexRange> indexRanges = collection.find(query)) {
            return ImmutableSortedSet.copyOf(IndexRange.COMPARATOR, (Iterator<? extends IndexRange>) indexRanges);
        }
    }

    @Override
    public SortedSet<IndexRange> findAll() {
        try (DBCursor<MongoIndexRange> cursor = collection.find(DBQuery.notExists("start"))) {
            return ImmutableSortedSet.copyOf(IndexRange.COMPARATOR, (Iterator<? extends IndexRange>) cursor);
        }
    }

    @Override
    public IndexRange calculateRange(String index) {
        indices.waitForRecovery(index);
        final DateTime now = DateTime.now(DateTimeZone.UTC);
        final Stopwatch sw = Stopwatch.createStarted();
        final IndexRangeStats stats = indices.indexRangeStatsOfIndex(index);
        final int duration = Ints.saturatedCast(sw.stop().elapsed(TimeUnit.MILLISECONDS));

        LOG.info("Calculated range of [{}] in [{}ms].", index, duration);
        return MongoIndexRange.create(index, stats.min(), stats.max(), now, duration, stats.streamIds());
    }

    @Override
    public IndexRange createUnknownRange(String index) {
        final DateTime begin = new DateTime(0L, DateTimeZone.UTC);
        final DateTime end = new DateTime(0L, DateTimeZone.UTC);
        final DateTime now = DateTime.now(DateTimeZone.UTC);
        return MongoIndexRange.create(index, begin, end, now, 0);
    }

    @Override
    public WriteResult<MongoIndexRange, ObjectId> save(IndexRange indexRange) {
        remove(indexRange.indexName());
        final WriteResult<MongoIndexRange, ObjectId> save = collection.save(MongoIndexRange.create(indexRange));
        return save;
    }

    @Override
    public boolean remove(String index) {
        final WriteResult<MongoIndexRange, ObjectId> remove = collection.remove(DBQuery.in(IndexRange.FIELD_INDEX_NAME, index));
        return remove.getN() > 0;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleIndexDeletion(IndicesDeletedEvent event) {
        for (String index : event.indices()) {
            if (!indexSetRegistry.isManagedIndex(index)) {
                LOG.debug("Not handling deleted index <{}> because it's not managed by any index set.", index);
                continue;
            }
            LOG.debug("Index \"{}\" has been deleted. Removing index range.");
            if (remove(index)) {
                auditEventSender.success(AuditActor.system(nodeId), ES_INDEX_RANGE_DELETE, ImmutableMap.of("index_name", index));
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleIndexClosing(IndicesClosedEvent event) {
        for (String index : event.indices()) {
            if (!indexSetRegistry.isManagedIndex(index)) {
                LOG.debug("Not handling closed index <{}> because it's not managed by any index set.", index);
                continue;
            }
            LOG.debug("Index \"{}\" has been closed. Removing index range.");
            if (remove(index)) {
                auditEventSender.success(AuditActor.system(nodeId), ES_INDEX_RANGE_DELETE, ImmutableMap.of("index_name", index));
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleIndexReopening(IndicesReopenedEvent event) {
        for (final String index : event.indices()) {
            if (!indexSetRegistry.isManagedIndex(index)) {
                LOG.debug("Not handling reopened index <{}> because it's not managed by any index set.", index);
                continue;
            }
            LOG.debug("Index \"{}\" has been reopened. Calculating index range.", index);

            indices.waitForRecovery(index);

            final IndexRange indexRange;
            try {
                indexRange = calculateRange(index);
                auditEventSender.success(AuditActor.system(nodeId), ES_INDEX_RANGE_CREATE, ImmutableMap.of("index_name", index));
            } catch (Exception e) {
                final String message = "Couldn't calculate index range for index \"" + index + "\"";
                LOG.error(message, e);
                auditEventSender.failure(AuditActor.system(nodeId), ES_INDEX_RANGE_CREATE, ImmutableMap.of("index_name", index));
                throw new RuntimeException(message, e);
            }

            save(indexRange);
        }
    }
}