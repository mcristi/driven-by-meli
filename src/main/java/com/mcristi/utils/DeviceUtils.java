package com.mcristi.utils;

import com.bitwig.extension.controller.api.CursorRemoteControlsPage;


public class DeviceUtils {

    private DeviceUtils() {
    }

    public static void setDevicePage(CursorRemoteControlsPage cursorRemoteControlsPage, int page) {
        cursorRemoteControlsPage.selectedPageIndex().set(page);
    }

    public static void setDevicePageParameter(CursorRemoteControlsPage cursorRemoteControlsPage, int index, int value) {
        cursorRemoteControlsPage.getParameter(index).set(value, 128);
    }

}
