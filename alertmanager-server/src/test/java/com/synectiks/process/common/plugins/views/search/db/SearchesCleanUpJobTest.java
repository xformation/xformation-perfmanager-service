/*
 * */
package com.synectiks.process.common.plugins.views.search.db;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.db.SearchDbService;
import com.synectiks.process.common.plugins.views.search.db.SearchesCleanUpJob;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewService;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SearchesCleanUpJobTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ViewService viewService;

    @Mock
    private SearchDbService searchDbService;

    private SearchesCleanUpJob searchesCleanUpJob;

    @Before
    public void setup() {
        this.searchesCleanUpJob = new SearchesCleanUpJob(viewService, searchDbService, Duration.standardDays(4));
    }

    @Test
    public void testForAllEmpty() {
        when(viewService.streamAll()).thenReturn(Stream.empty());
        when(searchDbService.streamAll()).thenReturn(Stream.empty());

        this.searchesCleanUpJob.doRun();

        verify(searchDbService, never()).delete(any());
    }

    @Test
    public void testForEmptySearches() {
        final ViewDTO view = mock(ViewDTO.class);

        when(viewService.streamAll()).thenReturn(Stream.of(view));
        when(searchDbService.streamAll()).thenReturn(Stream.empty());

        this.searchesCleanUpJob.doRun();

        verify(searchDbService, never()).delete(any());
    }

    @Test
    public void testForNonexpiredSearches() {
        when(viewService.streamAll()).thenReturn(Stream.empty());

        final Search search1 = mock(Search.class);
        when(search1.createdAt()).thenReturn(DateTime.now(DateTimeZone.UTC).minus(Duration.standardDays(1)));
        final Search search2 = mock(Search.class);
        when(search2.createdAt()).thenReturn(DateTime.now(DateTimeZone.UTC).minus(Duration.standardHours(4)));

        when(searchDbService.streamAll()).thenReturn(Stream.of(search1, search2));

        this.searchesCleanUpJob.doRun();

        verify(searchDbService, never()).delete(any());
    }

    @Test
    public void testForReferencedSearches() {
        final String searchId = "This search is in use";
        final ViewDTO view = mock(ViewDTO.class);
        when(view.searchId()).thenReturn(searchId);
        when(viewService.streamAll()).thenReturn(Stream.of(view));

        final Search search = mock(Search.class);
        when(search.createdAt()).thenReturn(DateTime.now(DateTimeZone.UTC).minus(Duration.standardDays(30)));
        when(search.id()).thenReturn(searchId);

        when(searchDbService.streamAll()).thenReturn(Stream.of(search));

        this.searchesCleanUpJob.doRun();

        verify(searchDbService, never()).delete(any());
    }

    @Test
    public void testForMixedReferencedNonReferencedExpiredAndNonexpiredSearches() {
        final String searchId = "This search is in use";
        final ViewDTO view = mock(ViewDTO.class);
        when(view.searchId()).thenReturn(searchId);
        when(viewService.streamAll()).thenReturn(Stream.of(view));

        final Search search1 = mock(Search.class);
        when(search1.createdAt()).thenReturn(DateTime.now(DateTimeZone.UTC).minus(Duration.standardDays(30)));
        when(search1.id()).thenReturn(searchId);

        final Search search2 = mock(Search.class);
        when(search2.createdAt()).thenReturn(DateTime.now(DateTimeZone.UTC).minus(Duration.standardHours(2)));

        final Search search3 = mock(Search.class);
        when(search3.createdAt()).thenReturn(DateTime.now(DateTimeZone.UTC).minus(Duration.standardDays(30)));
        when(search3.id()).thenReturn("This search is expired and should be deleted");

        when(searchDbService.streamAll()).thenReturn(Stream.of(search1, search2, search3));

        this.searchesCleanUpJob.doRun();

        final ArgumentCaptor<String> deletedSearchId = ArgumentCaptor.forClass(String.class);
        verify(searchDbService, times(1)).delete(deletedSearchId.capture());
        assertThat(deletedSearchId.getValue()).isEqualTo("This search is expired and should be deleted");
    }

    @Test
    public void testForEmptyViews() {
        when(viewService.streamAll()).thenReturn(Stream.empty());

        final Search search = mock(Search.class);
        when(search.createdAt()).thenReturn(DateTime.now(DateTimeZone.UTC).minus(Duration.standardDays(30)));
        when(search.id()).thenReturn("This search is expired and should be deleted");
        when(searchDbService.streamAll()).thenReturn(Stream.of(search));

        this.searchesCleanUpJob.doRun();

        final ArgumentCaptor<String> deletedSearchId = ArgumentCaptor.forClass(String.class);
        verify(searchDbService, times(1)).delete(deletedSearchId.capture());
        assertThat(deletedSearchId.getValue()).isEqualTo("This search is expired and should be deleted");
    }
}
