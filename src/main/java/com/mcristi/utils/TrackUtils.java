package com.mcristi.utils;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;


public class TrackUtils {

    private TrackUtils() {
    }

    public static void setSend(TrackBank trackBank, int trackIndex, int sendIndex, int value) {
        trackBank.getItemAt(trackIndex).sendBank().getItemAt(sendIndex).set(value, 128);
    }

    public static void setVolume(TrackBank trackBank, CursorTrack cursorTrack, int value) {
        int trackPosition = cursorTrack.position().get();
        trackBank.getItemAt(trackPosition).volume().set(value, 128);
    }

    public static void arm(TrackBank trackBank, CursorTrack cursorTrack, int index) {
        for (int i = 0; i < 3; i++) {
            trackBank.getItemAt(i).arm().set(false);
        }

        Track track = trackBank.getItemAt(index);
        track.arm().set(true);

        cursorTrack.selectChannel(track);
    }
}
