package com.ibrahimekinci.barcrowd.domain.usecase;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.domain.model.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

@RunWith(MockitoJUnitRunner.class)
public class GetCurrentUserUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private UserRepository mockRepository;

    private GetCurrentUserUseCase useCase;

    @Before
    public void setUp() {
        useCase = new GetCurrentUserUseCase(mockRepository);
    }

    @Test
    public void testExecute_DelegatesToRepositoryAndReturnsLiveData() {
        // 1. Arrange
        User testUser = new User();
        testUser.setUserId("u1");
        MutableLiveData<User> liveData = new MutableLiveData<>();
        liveData.setValue(testUser);

        when(mockRepository.getCurrentUser()).thenReturn(liveData);

        // 2. Act
        LiveData<User> result = useCase.execute();

        // 3. Assert
        verify(mockRepository).getCurrentUser();
        assertEquals(liveData, result);
        assertEquals(testUser, result.getValue());
    }
}