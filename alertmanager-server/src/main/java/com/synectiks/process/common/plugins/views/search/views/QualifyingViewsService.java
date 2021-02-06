/*
 * */
package com.synectiks.process.common.plugins.views.search.views;

import com.google.common.base.Functions;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.db.SearchDbService;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class QualifyingViewsService {
    private final SearchDbService searchDbService;
    private final ViewService viewService;

    @Inject
    public QualifyingViewsService(SearchDbService searchDbService, ViewService viewService) {
        this.searchDbService = searchDbService;
        this.viewService = viewService;
    }

    public Collection<ViewParameterSummaryDTO> forValue() {
        final Set<String> searches = viewService.streamAll()
                .map(ViewDTO::searchId)
                .collect(Collectors.toSet());
        final Map<String, Search> qualifyingSearches = this.searchDbService.findByIds(searches).stream()
                .filter(search -> !search.parameters().isEmpty())
                .collect(Collectors.toMap(Search::id, Functions.identity()));

        return viewService.streamAll()
                .filter(view -> qualifyingSearches.keySet().contains(view.searchId()))
                .map(view -> ViewParameterSummaryDTO.create(view, qualifyingSearches.get(view.searchId())))
                .collect(Collectors.toSet());
    }

}
