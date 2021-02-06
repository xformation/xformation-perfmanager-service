/*
 * */
package com.synectiks.process.server.indexer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.audit.AuditEventSender;
import com.synectiks.process.server.indexer.MongoIndexSet;
import com.synectiks.process.server.indexer.NoTargetIndexException;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indices.HealthStatus;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.indexer.indices.jobs.SetIndexReadOnlyAndCalculateRangeJob;
import com.synectiks.process.server.indexer.ranges.IndexRangeService;
import com.synectiks.process.server.indexer.retention.strategies.NoopRetentionStrategy;
import com.synectiks.process.server.indexer.retention.strategies.NoopRetentionStrategyConfig;
import com.synectiks.process.server.indexer.rotation.strategies.MessageCountRotationStrategy;
import com.synectiks.process.server.indexer.rotation.strategies.MessageCountRotationStrategyConfig;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;
import com.synectiks.process.server.system.jobs.SystemJobConcurrencyException;
import com.synectiks.process.server.system.jobs.SystemJobManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MongoIndexSetTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Indices indices;
    @Mock
    private AuditEventSender auditEventSender;
    @Mock
    private NodeId nodeId;
    @Mock
    private IndexRangeService indexRangeService;
    @Mock
    private SystemJobManager systemJobManager;
    @Mock
    private SetIndexReadOnlyAndCalculateRangeJob.Factory jobFactory;
    @Mock
    private ActivityWriter activityWriter;

    private final IndexSetConfig config = IndexSetConfig.create(
            "Test",
            "Test",
            true,
            "graylog",
            1,
            0,
            MessageCountRotationStrategy.class.getCanonicalName(),
            MessageCountRotationStrategyConfig.createDefault(),
            NoopRetentionStrategy.class.getCanonicalName(),
            NoopRetentionStrategyConfig.createDefault(),
            ZonedDateTime.of(2016, 11, 8, 0, 0, 0, 0, ZoneOffset.UTC),
            "standard",
            "index-template",
            IndexSetConfig.TemplateType.MESSAGES,
            1,
            false
    );

    private MongoIndexSet mongoIndexSet;

    @Before
    public void setUp() {
        mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
    }

    @Test
    public void testExtractIndexNumber() {
        assertThat(mongoIndexSet.extractIndexNumber("alertmanager_0")).contains(0);
        assertThat(mongoIndexSet.extractIndexNumber("alertmanager_4")).contains(4);
        assertThat(mongoIndexSet.extractIndexNumber("alertmanager_52")).contains(52);
    }

    @Test
    public void testExtractIndexNumberWithMalformedFormatReturnsEmptyOptional() {
        assertThat(mongoIndexSet.extractIndexNumber("graylog2_hunderttausend")).isEmpty();
    }

    @Test
    public void testBuildIndexName() {
        assertEquals("alertmanager_0", mongoIndexSet.buildIndexName(0));
        assertEquals("alertmanager_1", mongoIndexSet.buildIndexName(1));
        assertEquals("alertmanager_9001", mongoIndexSet.buildIndexName(9001));
    }

    @Test
    public void nullIndexerDoesNotThrow() {
        final Map<String, Set<String>> deflectorIndices = mongoIndexSet.getAllIndexAliases();
        assertThat(deflectorIndices).isEmpty();
    }

    @Test
    public void nullIndexerDoesNotThrowOnIndexName() {
        final String[] indicesNames = mongoIndexSet.getManagedIndices();
        assertThat(indicesNames).isEmpty();
    }

    @Test
    public void testIsDeflectorAlias() {
        assertTrue(mongoIndexSet.isWriteIndexAlias("alertmanager_deflector"));
        assertFalse(mongoIndexSet.isWriteIndexAlias("alertmanager_foobar"));
        assertFalse(mongoIndexSet.isWriteIndexAlias("alertmanager_123"));
        assertFalse(mongoIndexSet.isWriteIndexAlias("HAHA"));
    }

    @Test
    public void testIsGraylogIndex() {
        assertTrue(mongoIndexSet.isGraylogDeflectorIndex("alertmanager_1"));
        assertTrue(mongoIndexSet.isManagedIndex("alertmanager_1"));

        assertTrue(mongoIndexSet.isGraylogDeflectorIndex("alertmanager_42"));
        assertTrue(mongoIndexSet.isManagedIndex("alertmanager_42"));

        assertTrue(mongoIndexSet.isGraylogDeflectorIndex("alertmanager_100000000"));
        assertTrue(mongoIndexSet.isManagedIndex("alertmanager_100000000"));

        // The restored archive indices should NOT be taken into account when getting the new deflector number.
        assertFalse(mongoIndexSet.isGraylogDeflectorIndex("alertmanager_42_restored_archive"));
        assertTrue(mongoIndexSet.isManagedIndex("alertmanager_42_restored_archive"));

        assertFalse(mongoIndexSet.isGraylogDeflectorIndex("alertmanager_42_restored_archive123"));
        assertFalse(mongoIndexSet.isManagedIndex("alertmanager_42_restored_archive123"));

        assertFalse(mongoIndexSet.isGraylogDeflectorIndex("alertmanager_42_restored_archive_123"));
        assertFalse(mongoIndexSet.isManagedIndex("alertmanager_42_restored_archive_123"));

        assertFalse(mongoIndexSet.isGraylogDeflectorIndex(null));
        assertFalse(mongoIndexSet.isManagedIndex(null));

        assertFalse(mongoIndexSet.isGraylogDeflectorIndex(""));
        assertFalse(mongoIndexSet.isManagedIndex(""));

        assertFalse(mongoIndexSet.isGraylogDeflectorIndex("alertmanager_deflector"));
        assertFalse(mongoIndexSet.isManagedIndex("alertmanager_deflector"));

        assertFalse(mongoIndexSet.isGraylogDeflectorIndex("graylog2beta_1"));
        assertFalse(mongoIndexSet.isManagedIndex("graylog2beta_1"));

        assertFalse(mongoIndexSet.isGraylogDeflectorIndex("alertmanager_1_suffix"));
        assertFalse(mongoIndexSet.isManagedIndex("alertmanager_1_suffix"));

        assertFalse(mongoIndexSet.isGraylogDeflectorIndex("HAHA"));
        assertFalse(mongoIndexSet.isManagedIndex("HAHA"));
    }

    @Test
    public void getNewestTargetNumber() throws NoTargetIndexException {
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of(
                "alertmanager_1", Collections.emptySet(),
                "alertmanager_2", Collections.emptySet(),
                "alertmanager_3", Collections.singleton("alertmanager_deflector"),
                "alertmanager_4_restored_archive", Collections.emptySet());

        when(indices.getIndexNamesAndAliases(anyString())).thenReturn(indexNameAliases);
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);

        final int number = mongoIndexSet.getNewestIndexNumber();
        assertEquals(3, number);
    }

    @Test
    public void getAllGraylogIndexNames() {
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of(
                "alertmanager_1", Collections.emptySet(),
                "alertmanager_2", Collections.emptySet(),
                "alertmanager_3", Collections.emptySet(),
                "alertmanager_4_restored_archive", Collections.emptySet(),
                "alertmanager_5", Collections.singleton("alertmanager_deflector"));

        when(indices.getIndexNamesAndAliases(anyString())).thenReturn(indexNameAliases);
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);


        final String[] allGraylogIndexNames = mongoIndexSet.getManagedIndices();
        assertThat(allGraylogIndexNames).containsExactlyElementsOf(indexNameAliases.keySet());
    }

    @Test
    public void getAllGraylogDeflectorIndices() {
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of(
                "alertmanager_1", Collections.emptySet(),
                "alertmanager_2", Collections.emptySet(),
                "alertmanager_3", Collections.emptySet(),
                "alertmanager_4_restored_archive", Collections.emptySet(),
                "alertmanager_5", Collections.singleton("alertmanager_deflector"));

        when(indices.getIndexNamesAndAliases(anyString())).thenReturn(indexNameAliases);

        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        final Map<String, Set<String>> deflectorIndices = mongoIndexSet.getAllIndexAliases();

        assertThat(deflectorIndices).containsOnlyKeys("alertmanager_1", "alertmanager_2", "alertmanager_3", "alertmanager_5");
    }

    @Test
    public void testCleanupAliases() throws Exception {
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cleanupAliases(ImmutableSet.of("alertmanager_2", "alertmanager_3", "foobar"));
        verify(indices).removeAliases("alertmanager_deflector", ImmutableSet.of("alertmanager_2", "foobar"));
    }

    @Test
    public void cycleThrowsRuntimeExceptionIfIndexCreationFailed() {
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of();

        when(indices.getIndexNamesAndAliases(anyString())).thenReturn(indexNameAliases);
        when(indices.create("alertmanager_0", mongoIndexSet)).thenReturn(false);

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Could not create new target index <alertmanager_0>.");

        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cycle();
    }

    @Test
    public void cycleAddsUnknownDeflectorRange() {
        final String newIndexName = "alertmanager_1";
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of(
                "alertmanager_0", Collections.singleton("alertmanager_deflector"));

        when(indices.getIndexNamesAndAliases(anyString())).thenReturn(indexNameAliases);
        when(indices.create(newIndexName, mongoIndexSet)).thenReturn(true);
        when(indices.waitForRecovery(newIndexName)).thenReturn(HealthStatus.Green);

        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cycle();

        verify(indexRangeService, times(1)).createUnknownRange(newIndexName);
    }

    @Test
    public void cycleSetsOldIndexToReadOnly() throws SystemJobConcurrencyException {
        final String newIndexName = "alertmanager_1";
        final String oldIndexName = "alertmanager_0";
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of(
                oldIndexName, Collections.singleton("alertmanager_deflector"));

        when(indices.getIndexNamesAndAliases(anyString())).thenReturn(indexNameAliases);
        when(indices.create(newIndexName, mongoIndexSet)).thenReturn(true);
        when(indices.waitForRecovery(newIndexName)).thenReturn(HealthStatus.Green);

        final SetIndexReadOnlyAndCalculateRangeJob rangeJob = mock(SetIndexReadOnlyAndCalculateRangeJob.class);
        when(jobFactory.create(oldIndexName)).thenReturn(rangeJob);

        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cycle();

        verify(jobFactory, times(1)).create(oldIndexName);
        verify(systemJobManager, times(1)).submitWithDelay(rangeJob, 30L, TimeUnit.SECONDS);
    }

    @Test
    public void cycleSwitchesIndexAliasToNewTarget() {
        final String oldIndexName = config.indexPrefix() + "_0";
        final String newIndexName = config.indexPrefix() + "_1";
        final String deflector = "alertmanager_deflector";
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of(
                oldIndexName, Collections.singleton(deflector));

        when(indices.getIndexNamesAndAliases(anyString())).thenReturn(indexNameAliases);
        when(indices.create(newIndexName, mongoIndexSet)).thenReturn(true);
        when(indices.waitForRecovery(newIndexName)).thenReturn(HealthStatus.Green);

        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cycle();

        verify(indices, times(1)).cycleAlias(deflector, newIndexName, oldIndexName);
    }

    @Test
    public void cyclePointsIndexAliasToInitialTarget() {
        final String indexName = config.indexPrefix() + "_0";
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of();

        when(indices.getIndexNamesAndAliases(anyString())).thenReturn(indexNameAliases);
        when(indices.create(indexName, mongoIndexSet)).thenReturn(true);
        when(indices.waitForRecovery(indexName)).thenReturn(HealthStatus.Green);

        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cycle();

        verify(indices, times(1)).cycleAlias("alertmanager_deflector", indexName);
    }
}