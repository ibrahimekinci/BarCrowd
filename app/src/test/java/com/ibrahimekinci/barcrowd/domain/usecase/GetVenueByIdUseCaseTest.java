package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link GetVenueByIdUseCase}.
 * Tests delegation to VenueRepository.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetVenueByIdUseCaseTest {

    @Mock
    private VenueRepository mockRepository;

    private GetVenueByIdUseCase useCase;

    @Before
    public void setUp() {
        useCase = new GetVenueByIdUseCase(mockRepository);
    }

    @Test
    public void testExecute_DelegatesToRepositoryAndReturnsResult() {
        // 1. Arrange
        String venueId = "v1";
        Venue testVenue = new Venue(venueId, "Bar", "Addr", 0, 0, "Desc");
        when(mockRepository.getVenueById(venueId)).thenReturn(testVenue);

        // 2. Act
        Venue result = useCase.execute(venueId);

        // 3. Assert
        verify(mockRepository, Mockito.times(1)).getVenueById(venueId);
        assertEquals(testVenue, result);
    }
}