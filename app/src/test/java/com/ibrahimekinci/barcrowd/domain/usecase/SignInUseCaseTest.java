package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
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
 * Unit tests for {@link SignInUseCase}.
 * Focuses on input validation and correct repository interaction.
 */
@RunWith(MockitoJUnitRunner.class)
public class SignInUseCaseTest {

    @Mock
    private UserRepository mockRepository;
    @Mock
    private FirebaseAuthWrapper.AuthCallback mockCallback;

    private SignInUseCase useCase;

    @Before
    public void setUp() {
        useCase = new SignInUseCase(mockRepository);
    }

    @Test
    public void testExecute_WhenInputIsValid_CallsRepository() throws ValidationException {
        try (MockedStatic<Validators> mockedValidators = Mockito.mockStatic(Validators.class)) {
            // 1. Arrange
            String email = "valid@example.com";
            String password = "password";

            mockedValidators.when(() -> Validators.isValidEmail(email)).thenReturn(true);

            // 2. Act
            useCase.execute(email, password, mockCallback);

            // 3. Assert
            verify(mockRepository, Mockito.times(1)).signIn(
                    eq(email),
                    eq(password),
                    eq(mockCallback)
            );
        }
    }

    @Test
    public void testExecute_WhenInvalidEmail_ThrowsValidationException() {
        try (MockedStatic<Validators> mockedValidators = Mockito.mockStatic(Validators.class)) {
            // 1. Arrange
            String email = "invalid-email";
            String password = "password";

            mockedValidators.when(() -> Validators.isValidEmail(email)).thenReturn(false);

            // 2. Act & Assert
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                useCase.execute(email, password, mockCallback);
            });

            assertEquals("Invalid email format", exception.getMessage());
            verify(mockRepository, never()).signIn(any(), any(), any());
        }
    }

    @Test
    public void testExecute_WhenEmptyPassword_ThrowsValidationException() {
        try (MockedStatic<Validators> mockedValidators = Mockito.mockStatic(Validators.class)) {
            // 1. Arrange
            String email = "valid@example.com";
            String password = "";

            mockedValidators.when(() -> Validators.isValidEmail(email)).thenReturn(true);

            // 2. Act & Assert
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                useCase.execute(email, password, mockCallback);
            });

            assertEquals("Password cannot be empty", exception.getMessage());
            verify(mockRepository, never()).signIn(any(), any(), any());
        }
    }
}