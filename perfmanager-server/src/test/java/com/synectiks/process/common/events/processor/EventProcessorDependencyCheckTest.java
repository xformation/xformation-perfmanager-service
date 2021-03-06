/*
 * */
package com.synectiks.process.common.events.processor;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.events.processor.DBEventProcessorStateService;
import com.synectiks.process.common.events.processor.EventProcessorDependencyCheck;
import com.synectiks.process.common.events.processor.EventProcessorStateDto;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;
import com.synectiks.process.server.system.processing.DBProcessingStatusService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class EventProcessorDependencyCheckTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private DBProcessingStatusService dbProcessingStatusService;

    private MongoJackObjectMapperProvider objectMapperProvider = new MongoJackObjectMapperProvider(new ObjectMapperProvider().get());
    private DBEventProcessorStateService stateService;
    private EventProcessorDependencyCheck dependencyCheck;

    @Before
    public void setUp() throws Exception {
        stateService = new DBEventProcessorStateService(mongodb.mongoConnection(), objectMapperProvider);
        dependencyCheck = new EventProcessorDependencyCheck(stateService, dbProcessingStatusService);
    }

    @Test
    public void canProcessTimerange() {
        final DateTime now = DateTime.now(DateTimeZone.UTC);

        final EventProcessorStateDto stateDto1 = EventProcessorStateDto.builder()
                .eventDefinitionId("a")
                .minProcessedTimestamp(now.minusDays(1))
                .maxProcessedTimestamp(now)
                .build();
        final EventProcessorStateDto stateDto2 = EventProcessorStateDto.builder()
                .eventDefinitionId("b")
                .minProcessedTimestamp(now.minusDays(1))
                .maxProcessedTimestamp(now.minusHours(1))
                .build();
        final EventProcessorStateDto stateDto3 = EventProcessorStateDto.builder()
                .eventDefinitionId("c")
                .minProcessedTimestamp(now.minusDays(1))
                .maxProcessedTimestamp(now.minusHours(2))
                .build();

        // No state objects yet
        assertThat(dependencyCheck.canProcessTimerange(now, ImmutableSet.of("a"))).isFalse();

        stateService.setState(stateDto1);
        stateService.setState(stateDto2);
        stateService.setState(stateDto3);

        // No state object has processedTimerageEnd >= now + 1h
        assertThat(dependencyCheck.canProcessTimerange(now.plusHours(1), ImmutableSet.of("a"))).isFalse();

        // Only processor "a" has been processed at "now"
        assertThat(dependencyCheck.canProcessTimerange(now, ImmutableSet.of("a"))).isTrue();
        assertThat(dependencyCheck.canProcessTimerange(now, ImmutableSet.of("a", "b"))).isFalse();
        assertThat(dependencyCheck.canProcessTimerange(now, ImmutableSet.of("a", "c"))).isFalse();
        assertThat(dependencyCheck.canProcessTimerange(now, ImmutableSet.of("a", "b", "c"))).isFalse();
        assertThat(dependencyCheck.canProcessTimerange(now, ImmutableSet.of("b"))).isFalse();
        assertThat(dependencyCheck.canProcessTimerange(now, ImmutableSet.of("c"))).isFalse();

        // Only processors "a" and "b" have been processed at now - 1h
        assertThat(dependencyCheck.canProcessTimerange(now.minusHours(1), ImmutableSet.of("a", "b"))).isTrue();
        assertThat(dependencyCheck.canProcessTimerange(now.minusHours(1), ImmutableSet.of("a", "c"))).isFalse();

        // Processors "a", "b" and "c" have been processed at now - 2h
        assertThat(dependencyCheck.canProcessTimerange(now.minusHours(2), ImmutableSet.of("a", "b", "c"))).isTrue();
        assertThat(dependencyCheck.canProcessTimerange(now.minusHours(2), ImmutableSet.of("a", "b"))).isTrue();
        assertThat(dependencyCheck.canProcessTimerange(now.minusHours(2), ImmutableSet.of("a", "c"))).isTrue();
        assertThat(dependencyCheck.canProcessTimerange(now.minusHours(2), ImmutableSet.of("a"))).isTrue();
        assertThat(dependencyCheck.canProcessTimerange(now.minusHours(2), ImmutableSet.of("b"))).isTrue();
        assertThat(dependencyCheck.canProcessTimerange(now.minusHours(2), ImmutableSet.of("c"))).isTrue();
    }

    @Test
    public void hasMessagesIndexedUpTo() {
        final DateTime timestamp = DateTime.now(DateTimeZone.UTC);

        when(dbProcessingStatusService.earliestPostIndexingTimestamp()).thenReturn(Optional.of(timestamp));

        assertThat(dependencyCheck.hasMessagesIndexedUpTo(timestamp)).isTrue();
        assertThat(dependencyCheck.hasMessagesIndexedUpTo(timestamp.minusHours(1))).isTrue();
        assertThat(dependencyCheck.hasMessagesIndexedUpTo(timestamp.plusHours(1))).isFalse();

        // The method should always return false if there is no value for the max indexed timestamp available
        when(dbProcessingStatusService.earliestPostIndexingTimestamp()).thenReturn(Optional.empty());

        assertThat(dependencyCheck.hasMessagesIndexedUpTo(timestamp)).isFalse();
        assertThat(dependencyCheck.hasMessagesIndexedUpTo(timestamp.minusHours(1))).isFalse();
        assertThat(dependencyCheck.hasMessagesIndexedUpTo(timestamp.plusHours(1))).isFalse();
    }
}
