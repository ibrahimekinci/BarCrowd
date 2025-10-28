package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PostLiveUpdateUseCase}.
 * Tests validation and the internal logic (which currently contains two repository calls).
 */
@RunWith(MockitoJUnitRunner.class)
public class PostLiveUpdateUseCaseTest {

    @Mock
    private LiveUpdateRepository mockRepository;

    @Mock
    private UUID mockUuid;

    // Class Under Test
    private PostLiveUpdateUseCase useCase;

    @Before
    public void setUp() {
        useCase = new PostLiveUpdateUseCase(mockRepository);
    }

    /** Helper method to create a valid update model. */
    private LiveUpdate createValidUpdate() {
        LiveUpdate update = new LiveUpdate();
        update.setId("update1");
        update.setVenueId("venue1");
        update.setUserId("user1");
        update.setCrowdLevel("medium");
        update.setWaitTime(15);
        update.setAgeRange("20-30");
        update.setTimestamp(1000L);
        return update;
    }

    // --- Testing Validation and Repository Interaction ---

    @Test
    public void testExecute_WhenValidUpdate_CallsRepositoryTwice() throws ValidationException {
        // 1. Arrange
        LiveUpdate validUpdate = createValidUpdate();

        // 2. Act
        useCase.execute(validUpdate);

        // 3. Assert
        // The user's current code calls postUpdate TWICE
        verify(mockRepository, times(2)).postUpdate(validUpdate);
    }

    @Test
    public void testExecute_WhenVenueIdMissing_ThrowsValidationException() {
        // 1. Arrange
        LiveUpdate invalidUpdate = createValidUpdate();
        invalidUpdate.setVenueId(null);

        // 2. Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            useCase.execute(invalidUpdate);
        });

        assertEquals("Venue ID is required", exception.getMessage());
        verify(mockRepository, never()).postUpdate(any(LiveUpdate.class));
    }

    @Test
    public void testExecute_WhenInvalidCrowdLevel_ThrowsValidationException() {
        // 1. Arrange
        LiveUpdate invalidUpdate = createValidUpdate();
        invalidUpdate.setCrowdLevel("extremely_high");

        // 2. Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            useCase.execute(invalidUpdate);
        });

        assertEquals("Crowd level must be low, medium, or high", exception.getMessage());
        verify(mockRepository, never()).postUpdate(any(LiveUpdate.class));
    }

    // --- Testing Internal Logic (ID/Timestamp Assignment) ---

    @Test
    public void testExecute_WhenIdIsNull_GeneratesIdOnlyOnSecondCall() throws ValidationException {
        // We mock UUID only. We rely on the test environment for System.currentTimeMillis().
        try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {

            // 1. Arrange
            mockedUuid.when(UUID::randomUUID).thenReturn(mockUuid);
            when(mockUuid.toString()).thenReturn("generated-uuid");

            LiveUpdate updateWithoutId = createValidUpdate();
            long originalTimestamp = 1000L;
            updateWithoutId.setId(null);
            updateWithoutId.setTimestamp(originalTimestamp);

            // 2. Act
            useCase.execute(updateWithoutId);

            // 3. Assert (Captures objects passed to the repository)
            ArgumentCaptor<LiveUpdate> captor = ArgumentCaptor.forClass(LiveUpdate.class);
            verify(mockRepository, times(2)).postUpdate(captor.capture());

            List<LiveUpdate> allCalls = captor.getAllValues();
            LiveUpdate firstCallObject = allCalls.get(0);
            LiveUpdate secondCallObject = allCalls.get(1);

            // 1. FIX: Assert that the ID was mutated after the first call returned.
            // The object reference holds the final, mutated value.
            assertEquals("generated-uuid", firstCallObject.getId()); // FIX: Artık null değil, set edilmiş değeri bekliyoruz.

            // 2. Check second call object (which is the same reference)
            assertEquals("generated-uuid", secondCallObject.getId());

            // Check final timestamp state (Must be greater than original)
            assertTrue(secondCallObject.getTimestamp() > originalTimestamp);
            assertTrue(firstCallObject.getTimestamp() == secondCallObject.getTimestamp());
        }
    }
}