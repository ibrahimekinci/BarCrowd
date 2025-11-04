package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PostLiveUpdateUseCase}.
 * Tests validation and data enrichment logic.
 */
@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class PostLiveUpdateUseCaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private LiveUpdateRepository mockLiveUpdateRepository;
    @Mock
    private GetVenueByIdUseCase mockGetVenueByIdUseCase;

    @Mock
    private Venue mockVenue; // Mocked venue to be returned

    @Captor
    private ArgumentCaptor<LiveUpdate> liveUpdateCaptor;

    // Class Under Test
    private PostLiveUpdateUseCase useCase;

    @Before
    public void setUp() {
        useCase = new PostLiveUpdateUseCase(mockLiveUpdateRepository, mockGetVenueByIdUseCase);

        // Setup the mock venue that the GetVenueByIdUseCase will return
        when(mockVenue.getName()).thenReturn("Test Venue Name");
        when(mockVenue.getType()).thenReturn("Bar");
        when(mockVenue.getLogoUrl()).thenReturn("logo.url");
    }

    /** Helper method to create a valid update model from the UI. */
    private LiveUpdate createValidUpdateFromUI() {
        LiveUpdate update = new LiveUpdate();
        update.setVenueId("v1");
        update.setUserId("user1");
        update.setCrowdLevel("Medium");
        update.setWaitTime("5–15");
        update.setAgeRange("21–24");
        update.setMediaUrl("media.url");
        // Note: venueName, venueType, and venueLogoUrl are null
        return update;
    }

    @Test
    public void testExecute_WhenValid_EnrichesAndCallsRepository() throws ValidationException {
        // 1. Arrange
        LiveUpdate update = createValidUpdateFromUI();
        // When the use case asks for venue "v1", return our mock venue
        when(mockGetVenueByIdUseCase.execute("v1")).thenReturn(mockVenue);

        // 2. Act
        // This is a blocking call, which is fine in a unit test.
        useCase.execute(update);

        // 3. Assert
        // Verify it fetched the venue data
        verify(mockGetVenueByIdUseCase).execute("v1");

        // Verify it called the repository
        verify(mockLiveUpdateRepository).postUpdate(liveUpdateCaptor.capture());

        // Check that the object passed to the repository is now ENRICHED
        LiveUpdate enrichedUpdate = liveUpdateCaptor.getValue();
        assertEquals("v1", enrichedUpdate.getVenueId());
        assertEquals("user1", enrichedUpdate.getUserId());
        assertEquals("Medium", enrichedUpdate.getCrowdLevel());
        assertEquals("Test Venue Name", enrichedUpdate.getVenueName()); // Denormalized data
        assertEquals("Bar", enrichedUpdate.getVenueType()); // Denormalized data
        assertEquals("logo.url", enrichedUpdate.getVenueLogoUrl()); // Denormalized data
    }

    @Test
    public void testExecute_WhenVenueNotFound_ThrowsValidationException() {
        // 1. Arrange
        LiveUpdate update = createValidUpdateFromUI();
        // When the use case asks for venue "v1", return null
        when(mockGetVenueByIdUseCase.execute("v1")).thenReturn(null);

        // 2. Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            useCase.execute(update);
        });

        assertEquals("Invalid Venue ID. Venue not found.", exception.getMessage());
        verify(mockLiveUpdateRepository, never()).postUpdate(any());
    }

    @Test
    public void testExecute_WhenInvalidCrowdLevel_ThrowsValidationException() {
        // 1. Arrange
        LiveUpdate update = createValidUpdateFromUI();
        update.setCrowdLevel("super_busy"); // Invalid value

        // 2. Act & Assert
        // Use MockedStatic for the Validator helper class
        try (MockedStatic<Validators> mockedValidators = Mockito.mockStatic(Validators.class)) {
            mockedValidators.when(() -> Validators.isNotEmpty(any())).thenReturn(true);
            mockedValidators.when(() -> Validators.isValidCrowdLevel("super_busy")).thenReturn(false);

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                useCase.execute(update);
            });

            assertEquals("A valid crowd level is required", exception.getMessage());
            verify(mockLiveUpdateRepository, never()).postUpdate(any());
        }
    }
}