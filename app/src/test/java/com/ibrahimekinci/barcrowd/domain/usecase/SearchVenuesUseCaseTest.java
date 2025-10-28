package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.MutableLiveData;
import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq; // <-- ADDED THIS IMPORT
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link SearchVenuesUseCase}.
 * Tests delegation to VenueRepository.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchVenuesUseCaseTest {

    @Mock
    private VenueRepository mockRepository;

    private SearchVenuesUseCase useCase;

    @Before
    public void setUp() {
        useCase = new SearchVenuesUseCase(mockRepository);
    }

    @Test
    public void testExecute_DelegatesToRepository() {
        // 1. Arrange
        String searchTerm = "Bar";
        MutableLiveData<List<Venue>> dummyLiveData = new MutableLiveData<>(Collections.emptyList());
        when(mockRepository.searchVenues(eq("%" + searchTerm + "%"))).thenReturn(dummyLiveData); // 'eq' is now recognized

        // 2. Act
        useCase.execute(searchTerm);

        // 3. Assert
        // Verify repository was called with the correct search format (e.g., "%Bar%")
        verify(mockRepository, Mockito.times(1)).searchVenues(eq("%" + searchTerm + "%"));
    }
}