package org.tourplanner.viewmodel;

import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchBarViewModelTest {

    private RouteListViewModel mockRouteListViewModel;
    private SearchBarViewModel viewModel;

    @BeforeEach
    void setUp() {
        mockRouteListViewModel = Mockito.mock(RouteListViewModel.class);
        viewModel = new SearchBarViewModel(mockRouteListViewModel);
    }

    @Test
    void testInitialSearchQueryIsEmpty() {
        assertEquals("", viewModel.getSearchQuery());
    }

    @Test
    void testSearchQueryPropertyUpdatesValue() {
        StringProperty queryProperty = viewModel.searchQueryProperty();
        queryProperty.set("test search");

        assertEquals("test search", viewModel.getSearchQuery());
    }

    @Test
    void testPerformSearchDelegatesToRouteListViewModel() {
        viewModel.searchQueryProperty().set("alps");

        viewModel.performSearch();

        verify(mockRouteListViewModel, times(1)).filterByName("alps");
    }
}
