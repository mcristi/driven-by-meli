package com.mcristi.utils;

import com.bitwig.extension.controller.api.TrackBank;


public class TrackUtils {

    private TrackUtils() {
    }

    public static void setSend(TrackBank trackBank, int trackIndex, int sendIndex, int value) {
        trackBank.getItemAt(trackIndex).sendBank().getItemAt(sendIndex).set(value, 128);
    }

}
