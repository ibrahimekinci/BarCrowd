package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.List;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetHomePageVenuesUseCaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private VenueRepository mockRepository;

    private GetHomePageVenuesUseCase useCase;

    @Before
    public void setUp() {
        useCase = new GetHomePageVenuesUseCase(mockRepository);
    }

    @Test
    public void testExecute_delegatesToRepository() {
        LiveData<List<Venue>> mockLiveData = new MutableLiveData<>();
        when(mockRepository.getHomePageVenues()).thenReturn(mockLiveData);

        useCase.execute();

        verify(mockRepository).getHomePageVenues();
    }
}