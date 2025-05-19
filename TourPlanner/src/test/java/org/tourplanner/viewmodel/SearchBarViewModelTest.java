package org.tourplanner.viewmodel;

import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tourplanner.service.TourLogManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SearchBarViewModelTest {

    private TourListViewModel mockTourListViewModel;
    private TourLogManager mockTourLogManager;
    private SearchBarViewModel viewModel;

    @BeforeEach
    void setUp() {
        mockTourListViewModel = mock(TourListViewModel.class);
        mockTourLogManager = mock(TourLogManager.class);
        viewModel = new SearchBarViewModel(mockTourListViewModel, mockTourLogManager);
    }

    @Test
    void testInitialSearchQueryIsEmpty() {
        assertEquals("", viewModel.getSearchQuery());
    }

    @Test
    void testSearchQueryPropertyCanBeUpdated() {
        StringProperty searchProperty = viewModel.searchQueryProperty();
        searchProperty.set("donau");

        assertEquals("donau", viewModel.getSearchQuery());
    }

    @Test
    void testPerformSearchDelegatesToTourListViewModelWithQueryAndLogManager() {
        viewModel.searchQueryProperty().set("adventure");

        viewModel.performSearch();

        verify(mockTourListViewModel, times(1)).filterByFullText("adventure", mockTourLogManager);
    }

    @Test
    void testPerformSearchWithEmptyStringStillCallsFilter() {
        viewModel.searchQueryProperty().set("");

        viewModel.performSearch();

        verify(mockTourListViewModel, times(1)).filterByFullText("", mockTourLogManager);
    }
}
