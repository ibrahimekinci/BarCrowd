package com.ibrahimekinci.barcrowd.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ConnectivityUtil}.
 * Uses Mockito to simulate Android ConnectivityManager behavior.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConnectivityUtilTest {

    @Mock
    private Context mockContext;
    @Mock
    private ConnectivityManager mockConnectivityManager;
    @Mock
    private Network mockActiveNetwork;
    @Mock
    private NetworkCapabilities mockNetworkCapabilities;
    @Mock
    private AppLogger mockAppLogger; // Mock the static logger

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Setup the context to return the mocked ConnectivityManager
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
    }

    @Test
    public void testIsOnline_WhenConnectedAndValidated_ReturnsTrue() {
        // 1. Arrange
        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockActiveNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(mockActiveNetwork)).thenReturn(mockNetworkCapabilities);

        // Simulate having internet capability and being validated
        when(mockNetworkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true);
        when(mockNetworkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true);

        // 2. Act
        boolean result = ConnectivityUtil.isOnline(mockContext);

        // 3. Assert
        assertTrue(result);
    }

    @Test
    public void testIsOnline_WhenNetworkNotValidated_ReturnsFalse() {
        // 1. Arrange
        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockActiveNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(mockActiveNetwork)).thenReturn(mockNetworkCapabilities);

        // Simulate having internet but not validated
        when(mockNetworkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true);
        when(mockNetworkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(false);

        // 2. Act
        boolean result = ConnectivityUtil.isOnline(mockContext);

        // 3. Assert
        assertFalse(result);
    }

    @Test
    public void testIsOnline_WhenNoActiveNetwork_ReturnsFalse() {
        // 1. Arrange
        when(mockConnectivityManager.getActiveNetwork()).thenReturn(null); // No active network

        // 2. Act
        boolean result = ConnectivityUtil.isOnline(mockContext);

        // 3. Assert
        assertFalse(result);
    }

    @Test
    public void testIsOnline_WhenConnectivityManagerIsNull_ReturnsFalseAndLogsWarning() {
        // Mock the context to return null for the service system call
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null);

        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 2. Act
            boolean result = ConnectivityUtil.isOnline(mockContext);

            // 3. Assert
            assertFalse(result);
            // Verify that a warning was logged
            mockedLogger.verify(() -> AppLogger.w("ConnectivityManager is null"));
        }
    }
}