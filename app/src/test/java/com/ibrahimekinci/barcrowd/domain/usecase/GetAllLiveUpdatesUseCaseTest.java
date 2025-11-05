package com.ibrahimekinci.barcrowd.domain.usecase;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GetAllLiveUpdatesUseCaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private LiveUpdateRepository mockRepository;

    private GetAllLiveUpdatesUseCase useCase;

    @Before
    public void setUp() {
        useCase = new GetAllLiveUpdatesUseCase(mockRepository);
    }

    @Test
    public void testExecute_delegatesToRepository() {
        LiveData<List<LiveUpdate>> mockLiveData = new MutableLiveData<>();
        when(mockRepository.getAllLiveUpdates()).thenReturn(mockLiveData);

        useCase.execute();

        verify(mockRepository).getAllLiveUpdates();
    }
}