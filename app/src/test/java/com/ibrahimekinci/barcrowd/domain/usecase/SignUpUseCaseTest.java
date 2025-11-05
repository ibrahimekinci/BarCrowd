package com.ibrahimekinci.barcrowd.domain.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class SignUpUseCaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private UserRepository mockRepository;

    @Mock
    private FirebaseAuthWrapper.AuthCallback mockCallback;

    private SignUpUseCase useCase;

    @Before
    public void setUp() {
        useCase = new SignUpUseCase(mockRepository);
    }

    @Test
    public void testExecute_WhenInputIsValid_CallsRepository() throws ValidationException {
        try (MockedStatic<Validators> mockedValidators = Mockito.mockStatic(Validators.class)) {
            // 1. Arrange
            String email = "valid@example.com";
            String password = "StrongPassword123";
            String fullName = "Test User";
            String username = "testuser";

            // Program mocks to return true for valid inputs
            when(Validators.isValidEmail(email)).thenReturn(true);
            when(Validators.isValidPassword(password)).thenReturn(true);
            when(Validators.isValidName(fullName)).thenReturn(true);
            when(Validators.isValidName(username)).thenReturn(true);

            // 2. Act
            useCase.execute(email, password, fullName, username, mockCallback);

            // 3. Assert
            verify(mockRepository).signUp(
                    eq(email),
                    eq(password),
                    eq(fullName),
                    eq(username),
                    eq(mockCallback)
            );
        }
    }

    @Test
    public void testExecute_WhenInvalidName_ThrowsValidationException() {
        try (MockedStatic<Validators> mockedValidators = Mockito.mockStatic(Validators.class)) {
            // 1. Arrange
            String email = "valid@example.com";
            String password = "StrongPassword123";
            String fullName = ""; // Invalid name
            String username = "testuser";

            when(Validators.isValidEmail(email)).thenReturn(true);
            when(Validators.isValidPassword(password)).thenReturn(true);
            when(Validators.isValidName(fullName)).thenReturn(false);

            // 2. Act & Assert
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                useCase.execute(email, password, fullName, username, mockCallback);
            });

            assertEquals("Please enter a valid full name", exception.getMessage());
            verify(mockRepository, never()).signUp(any(), any(), any(), any(), any());
        }
    }
}