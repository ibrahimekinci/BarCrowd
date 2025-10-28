package com.ibrahimekinci.barcrowd.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ConnectivityReceiver}.
 * Simulates network changes and verifies if repository sync is triggered.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConnectivityReceiverTest {

    private ConnectivityReceiver receiver;

    @Mock
    private Context mockContext;
    @Mock
    private Intent mockIntent;
    @Mock
    private BarCrowdApplication mockApplication;
    @Mock
    private DependencyInjector mockInjector;
    @Mock
    private LiveUpdateRepository mockRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        receiver = new ConnectivityReceiver();

        // Standard setup for accessing repository via Application/Injector
        when(mockContext.getApplicationContext()).thenReturn(mockApplication);
        when(mockApplication.getDependencyInjector()).thenReturn(mockInjector);
        when(mockInjector.getLiveUpdateRepository()).thenReturn(mockRepository);
    }

    @Test
    public void testOnReceive_WhenOnline_TriggersSyncAndLogsInfo() {
        // Mock static ConnectivityUtil and AppLogger
        try (MockedStatic<ConnectivityUtil> mockedConn = Mockito.mockStatic(ConnectivityUtil.class);
             MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {

            // 1. Arrange
            mockedConn.when(() -> ConnectivityUtil.isOnline(mockContext)).thenReturn(true); // Simulate internet connection ON

            // 2. Act
            receiver.onReceive(mockContext, mockIntent);

            // 3. Assert
            // Verify syncPending was called on the repository
            verify(mockRepository, Mockito.times(1)).syncPending();

            // Verify log message
            mockedLogger.verify(() -> AppLogger.i("Connectivity restored; syncing pending updates"));
        }
    }

    @Test
    public void testOnReceive_WhenOffline_DoesNotTriggerSync() {
        // Mock static ConnectivityUtil
        try (MockedStatic<ConnectivityUtil> mockedConn = Mockito.mockStatic(ConnectivityUtil.class);
             MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {

            // 1. Arrange
            mockedConn.when(() -> ConnectivityUtil.isOnline(mockContext)).thenReturn(false); // Simulate internet connection OFF

            // 2. Act
            receiver.onReceive(mockContext, mockIntent);

            // 3. Assert
            // Verify syncPending was NEVER called
            verify(mockRepository, never()).syncPending();

            // Verify no log message about syncing was made
            mockedLogger.verify(() -> AppLogger.i(any()), never());
        }
    }
}