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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when; // <-- ADDED THIS IMPORT

/**
 * Unit test for {@link GetAllVenuesUseCase}.
 * Tests delegation to VenueRepository.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetAllVenuesUseCaseTest {

    @Mock
    private VenueRepository mockRepository;

    private GetAllVenuesUseCase useCase;

    @Before
    public void setUp() {
        useCase = new GetAllVenuesUseCase(mockRepository);
    }

    @Test
    public void testExecute_DelegatesToRepository() {
        // 1. Arrange
        MutableLiveData<List<Venue>> dummyLiveData = new MutableLiveData<>(Collections.emptyList());
        when(mockRepository.getAllVenues()).thenReturn(dummyLiveData); // 'when' is now recognized

        // 2. Act
        useCase.execute();

        // 3. Assert
        verify(mockRepository, Mockito.times(1)).getAllVenues();
    }
}