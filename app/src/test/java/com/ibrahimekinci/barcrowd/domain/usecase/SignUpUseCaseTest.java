package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link SignUpUseCase}.
 * Focuses on input validation and correct repository interaction.
 */
@RunWith(MockitoJUnitRunner.class)
public class SignUpUseCaseTest {

    @Mock
    private UserRepository mockRepository;

    @Mock
    private FirebaseAuthWrapper.AuthCallback mockCallback;

    // Class Under Test
    private SignUpUseCase useCase;

    @Before
    public void setUp() {
        // MockitoRule handles initialization, but we initialize the class under test
        useCase = new SignUpUseCase(mockRepository);
    }

    @Test
    public void testExecute_WhenInputIsValid_CallsRepository() throws ValidationException {
        // Use MockedStatic for the Validator helper class
        try (MockedStatic<Validators> mockedValidators = Mockito.mockStatic(Validators.class)) {
            // 1. Arrange
            String email = "valid@example.com";
            String password = "StrongPassword123";

            // Program the static mock to return true for valid inputs
            mockedValidators.when(() -> Validators.isValidEmail(email)).thenReturn(true);
            mockedValidators.when(() -> Validators.isValidPassword(password)).thenReturn(true);

            // 2. Act
            useCase.execute(email, password, mockCallback);

            // 3. Assert
            // Verify that the repository's signUp method was called exactly once
            verify(mockRepository, Mockito.times(1)).signUp(
                    eq(email),
                    eq(password),
                    eq(mockCallback)
            );
        }
    }

    @Test
    public void testExecute_WhenInvalidEmail_ThrowsValidationExceptionAndDoesNotCallRepository() {
        // Use MockedStatic for the Validator helper class
        try (MockedStatic<Validators> mockedValidators = Mockito.mockStatic(Validators.class)) {
            // 1. Arrange
            String email = "invalid-email";
            String password = "StrongPassword123";

            // Program the static mock to return false for the email
            mockedValidators.when(() -> Validators.isValidEmail(email)).thenReturn(false);
            // Program the static mock to return true for the password (though it won't be checked)
            mockedValidators.when(() -> Validators.isValidPassword(password)).thenReturn(true);


            // 2. Act & Assert
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                useCase.execute(email, password, mockCallback);
            });

            // Verify the exception message
            assertEquals("Invalid email format", exception.getMessage());

            // Verify that the repository was NEVER called
            verify(mockRepository, never()).signUp(any(), any(), any());
        }
    }

    @Test
    public void testExecute_WhenInvalidPassword_ThrowsValidationExceptionAndDoesNotCallRepository() {
        // Use MockedStatic for the Validator helper class
        try (MockedStatic<Validators> mockedValidators = Mockito.mockStatic(Validators.class)) {
            // 1. Arrange
            String email = "valid@example.com";
            String password = "weak";

            // Program the static mock to return true for the email
            mockedValidators.when(() -> Validators.isValidEmail(email)).thenReturn(true);
            // Program the static mock to return false for the password
            mockedValidators.when(() -> Validators.isValidPassword(password)).thenReturn(false);


            // 2. Act & Assert
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                useCase.execute(email, password, mockCallback);
            });

            // Verify the exception message
            assertEquals("Password must be at least 8 characters with uppercase and number", exception.getMessage());

            // Verify that the repository was NEVER called
            verify(mockRepository, never()).signUp(any(), any(), any());
        }
    }
}