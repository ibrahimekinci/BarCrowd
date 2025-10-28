package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link GetMyContributionsUseCase}.
 * Focuses on user ID validation and correct repository delegation.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetMyContributionsUseCaseTest {

    @Mock
    private LiveUpdateRepository mockRepository;

    private GetMyContributionsUseCase useCase;

    @Before
    public void setUp() {
        useCase = new GetMyContributionsUseCase(mockRepository);
    }

    @Test
    public void testExecute_WhenValidUserId_CallsRepository() throws ValidationException {
        // 1. Arrange
        String userId = "user123";

        // 2. Act
        useCase.execute(userId);

        // 3. Assert
        verify(mockRepository, Mockito.times(1)).getUserContributions(eq(userId));
    }

    @Test
    public void testExecute_WhenNullUserId_ThrowsValidationException() {
        try (MockedStatic<Validators> mockedValidators = Mockito.mockStatic(Validators.class)) {
            // 1. Arrange
            String userId = null;
            mockedValidators.when(() -> Validators.isNotEmpty(userId)).thenReturn(false);

            // 2. Act & Assert
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                useCase.execute(userId);
            });

            assertEquals("User ID is required", exception.getMessage());
            verify(mockRepository, never()).getUserContributions(any());
        }
    }

    @Test
    public void testExecute_WhenEmptyUserId_ThrowsValidationException() {
        try (MockedStatic<Validators> mockedValidators = Mockito.mockStatic(Validators.class)) {
            // 1. Arrange
            String userId = "";
            mockedValidators.when(() -> Validators.isNotEmpty(userId)).thenReturn(false);

            // 2. Act & Assert
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                useCase.execute(userId);
            });

            assertEquals("User ID is required", exception.getMessage());
            verify(mockRepository, never()).getUserContributions(any());
        }
    }
}