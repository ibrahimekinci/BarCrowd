package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.domain.model.User;

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
 * Unit test for {@link GetCurrentUserUseCase}.
 * Tests delegation to UserRepository.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetCurrentUserUseCaseTest {

    @Mock
    private UserRepository mockRepository;

    private GetCurrentUserUseCase useCase;

    @Before
    public void setUp() {
        useCase = new GetCurrentUserUseCase(mockRepository);
    }

    @Test
    public void testExecute_DelegatesToRepositoryAndReturnsResult() {
        // 1. Arrange
        User testUser = new User("u1", "test@example.com", 123L);
        when(mockRepository.getCurrentUser()).thenReturn(testUser);

        // 2. Act
        User result = useCase.execute();

        // 3. Assert
        verify(mockRepository, Mockito.times(1)).getCurrentUser();
        assertEquals(testUser, result);
    }
}