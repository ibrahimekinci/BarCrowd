package com.ibrahimekinci.barcrowd.data.local;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class VenueDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private VenueDao venueDao;

    private VenueEntity venue1; // showOnHomePage = true, older
    private VenueEntity venue2; // showOnHomePage = true, newer
    private VenueEntity venue3; // showOnHomePage = false

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        venueDao = db.venueDao();

        OpeningHoursEmbedded hours = new OpeningHoursEmbedded();
        hours.setOhMonday("Closed");
        long now = System.currentTimeMillis();

        venue1 = new VenueEntity();
        venue1.setVenueId("v1");
        venue1.setName("Test Bar 1");
        venue1.setCreatedAt(new Date(now - 1000)); // Older
        venue1.setShowOnHomePage(true);
        venue1.setOpeningHours(hours);
        venue1.setType("Bar");
        venue1.setAddress("Address 1");
        venue1.setLogoUrl("logo1.url");

        venue2 = new VenueEntity();
        venue2.setVenueId("v2");
        venue2.setName("Test Cafe 2");
        venue2.setCreatedAt(new Date(now)); // Newer
        venue2.setShowOnHomePage(true);
        venue2.setOpeningHours(hours);
        venue2.setType("Bar");
        venue2.setAddress("Address 2");
        venue2.setLogoUrl("logo2.url");

        venue3 = new VenueEntity();
        venue3.setVenueId("v3");
        venue3.setName("Demo Pub 3");
        venue3.setCreatedAt(new Date(now + 1000)); // Newest
        venue3.setShowOnHomePage(false); // This one is not on the home page
        venue3.setOpeningHours(hours);
        venue3.setType("Pub");
        venue3.setAddress("Address 3");
        venue3.setLogoUrl("logo3.url");
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsertAndGetById() throws Exception {
        venueDao.insertAll(Arrays.asList(venue1));
        VenueEntity retrieved = venueDao.getVenueById("v1");

        assertNotNull(retrieved);
        assertEquals(venue1.getVenueId(), retrieved.getVenueId());
        assertEquals("Closed", retrieved.getOpeningHours().getOhMonday());
    }

    @Test
    public void testGetHomePageVenues_ReturnsAllVenuesSortedByShowOnHome() throws Exception {
        List<VenueEntity> allVenues = Arrays.asList(venue1, venue2, venue3);
        venueDao.insertAll(allVenues);

        // Act
        List<VenueEntity> retrievedList = getOrAwaitValue(venueDao.getHomePageVenues());

        // Assert
        assertNotNull(retrievedList);
        // Test now expects all 3 venues, sorted by showOnHomePage DESC, then createdAt DESC
        assertEquals(3, retrievedList.size());
        assertEquals("v2", retrievedList.get(0).getVenueId()); // show=true, newer
        assertEquals("v1", retrievedList.get(1).getVenueId()); // show=true, older
        assertEquals("v3", retrievedList.get(2).getVenueId()); // show=false
    }

    @Test
    public void testGetAllVenuesSortedByName() throws Exception {
        List<VenueEntity> allVenues = Arrays.asList(venue1, venue2, venue3);
        venueDao.insertAll(allVenues);

        List<VenueEntity> retrievedList = getOrAwaitValue(venueDao.getAllVenuesSortedByName());

        assertNotNull(retrievedList);
        assertEquals(3, retrievedList.size());
        assertEquals("Demo Pub 3", retrievedList.get(0).getName());
        assertEquals("Test Bar 1", retrievedList.get(1).getName());
        assertEquals("Test Cafe 2", retrievedList.get(2).getName());
    }

    @Test
    public void testSearchVenues() throws Exception {
        List<VenueEntity> allVenues = Arrays.asList(venue1, venue2, venue3);
        venueDao.insertAll(allVenues);

        List<VenueEntity> searchResult = getOrAwaitValue(venueDao.searchVenues("%Bar%"));

        assertNotNull(searchResult);
        assertEquals(1, searchResult.size());
        assertEquals("v1", searchResult.get(0).getVenueId());
    }

    @Test
    public void testInsertAll_OnConflict_ReplacesExisting() throws Exception {
        venueDao.insertAll(Arrays.asList(venue1));

        VenueEntity updatedVenue1 = new VenueEntity();
        updatedVenue1.setVenueId("v1");
        updatedVenue1.setName("Updated Name");
        updatedVenue1.setShowOnHomePage(true);
        updatedVenue1.setOpeningHours(new OpeningHoursEmbedded());
        updatedVenue1.setCreatedAt(new Date());
        updatedVenue1.setAddress("Address 1");
        updatedVenue1.setLogoUrl("logo1.url");
        updatedVenue1.setType("Bar");

        venueDao.insertAll(Arrays.asList(updatedVenue1));

        List<VenueEntity> retrievedList = getOrAwaitValue(venueDao.getAllVenuesSortedByName());

        assertEquals(1, retrievedList.size());
        assertEquals("Updated Name", retrievedList.get(0).getName());
    }

    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);

        liveData.observeForever(o -> {
            data[0] = o;
            latch.countDown();
        });

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }

        //noinspection unchecked
        return (T) data[0];
    }
}