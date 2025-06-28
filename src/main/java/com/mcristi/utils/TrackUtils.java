package com.mcristi.utils;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import com.bitwig.extension.controller.api.PinnableCursorDevice;


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

    public static void unarmAll(TrackBank trackBank) {
        for (int i = 0; i < trackBank.itemCount().get(); i++) {
            try {
                Track track = trackBank.getItemAt(i);
                if (track.exists().get()) {
                    track.arm().set(false);
                }
            } catch (Exception e) {
                break; // Stop iterating if we hit an invalid index
            }
        }
    }

    public static void arm(TrackBank trackBank, CursorTrack cursorTrack, PinnableCursorDevice cursorDevice, int index) {
        TrackUtils.unarmAll(trackBank);

        Track track = trackBank.getItemAt(index);
        track.arm().set(true);
        track.selectInEditor();

        cursorTrack.selectChannel(track);

        cursorDevice.selectFirst();
        cursorDevice.isWindowOpen().set(true);
    }
}
