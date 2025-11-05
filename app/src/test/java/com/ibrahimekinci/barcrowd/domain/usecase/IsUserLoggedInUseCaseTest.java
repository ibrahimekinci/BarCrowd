package com.ibrahimekinci.barcrowd.domain.usecase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.ibrahimekinci.barcrowd.data.repository.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

@RunWith(MockitoJUnitRunner.class)
public class IsUserLoggedInUseCaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private UserRepository mockRepository;

    private IsUserLoggedInUseCase useCase;

    @Before
    public void setUp() {
        useCase = new IsUserLoggedInUseCase(mockRepository);
    }

    @Test
    public void testExecute_whenLoggedIn_returnsTrue() {
        when(mockRepository.isUserLoggedIn()).thenReturn(true);
        assertTrue(useCase.execute());
    }

    @Test
    public void testExecute_whenLoggedOut_returnsFalse() {
        when(mockRepository.isUserLoggedIn()).thenReturn(false);
        assertFalse(useCase.execute());
    }
}