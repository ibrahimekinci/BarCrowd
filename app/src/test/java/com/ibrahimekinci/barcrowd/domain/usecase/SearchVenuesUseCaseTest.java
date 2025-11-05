package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit test for {@link SearchVenuesUseCase}.
 * Tests delegation to VenueRepository.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchVenuesUseCaseTest {

    @Mock
    private VenueRepository mockRepository;

    private SearchVenuesUseCase useCase;

    @Before
    public void setUp() {
        useCase = new SearchVenuesUseCase(mockRepository);
    }

    @Test
    public void testExecute_DelegatesToRepository() {
        // TODO
    }
}