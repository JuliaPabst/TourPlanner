package org.tourplanner.viewmodel;

import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchBarViewModelTest {

    private TourListViewModel mockTourListViewModel;
    private SearchBarViewModel viewModel;

    @BeforeEach
    void setUp() {
        mockTourListViewModel = Mockito.mock(TourListViewModel.class);
        viewModel = new SearchBarViewModel(mockTourListViewModel);
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
    void testPerformSearchDelegatesToTourListViewModel() {
        viewModel.searchQueryProperty().set("alps");

        viewModel.performSearch();

        verify(mockTourListViewModel, times(1)).filterByName("alps");
    }
}
