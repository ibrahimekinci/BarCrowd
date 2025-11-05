package com.ibrahimekinci.barcrowd.domain.usecase;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.lifecycle.MutableLiveData;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

/**
 * Unit test for {@link GetUpdatesForVenueUseCase}.
 * Tests delegation to LiveUpdateRepository.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetUpdatesForVenueUseCaseTest {

    @Mock
    private LiveUpdateRepository mockRepository;

    private GetUpdatesForVenueUseCase useCase;

    @Before
    public void setUp() {
        useCase = new GetUpdatesForVenueUseCase(mockRepository);
    }

    @Test
    public void testExecute_DelegatesToRepository() {
        // 1. Arrange
        String venueId = "v1";
        MutableLiveData<List<LiveUpdate>> dummyLiveData = new MutableLiveData<>(Collections.emptyList());
        when(mockRepository.getUpdatesForVenue(venueId)).thenReturn(dummyLiveData);

        // 2. Act
        useCase.execute(venueId);

        // 3. Assert
        verify(mockRepository, Mockito.times(1)).getUpdatesForVenue(venueId);
    }
}