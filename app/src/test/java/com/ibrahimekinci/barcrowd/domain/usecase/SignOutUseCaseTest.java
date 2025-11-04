package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SignOutUseCaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private UserRepository mockRepository;

    private SignOutUseCase useCase;

    @Before
    public void setUp() {
        useCase = new SignOutUseCase(mockRepository);
    }

    @Test
    public void testExecute_delegatesToRepository() {
        useCase.execute();
        verify(mockRepository).signOut();
    }
}