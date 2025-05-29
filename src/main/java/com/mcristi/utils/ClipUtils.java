package com.mcristi.utils;

import com.bitwig.extension.controller.api.Clip;

public class ClipUtils {

    private ClipUtils() {
    }

    public static void delete(Clip cursorClip) {
        cursorClip.clipLauncherSlot().deleteObject();
    }
}
