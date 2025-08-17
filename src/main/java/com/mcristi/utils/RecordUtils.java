package com.mcristi.utils;

import com.bitwig.extension.controller.api.*;
import com.mcristi.Globals;

import java.util.ArrayList;
import java.util.List;


public class RecordUtils {

    private RecordUtils() {
    }

    public static void recordClip(ControllerHost host, TrackBank trackBank, SceneBank sceneBank,
                                  Project project, DetailEditor detailEditor, Transport transport,
                                  Clip cursorClip) {
        List<ClipLauncherSlotBank> slotBanks = new ArrayList<>();
        for (int i = 0; i < Globals.NUMBER_OF_TRACKS; i++) {
            Track track = trackBank.getItemAt(i);
            if (track.arm().get()) {
                ClipLauncherSlotBank slotBank = track.clipLauncherSlotBank();
                slotBanks.add(slotBank);
            }
        }

        if (slotBanks.isEmpty()) {
            host.showPopupNotification("No tracks armed!");
            return;
        }

        for (ClipLauncherSlotBank slotBank : slotBanks) {
            host.scheduleTask(() -> recordClipOnTrack(host, slotBank, sceneBank, project, detailEditor, transport, cursorClip), 0);
        }
    }

    private static void recordClipOnTrack(ControllerHost host, ClipLauncherSlotBank slotBank, SceneBank sceneBank,
                                          Project project, DetailEditor detailEditor, Transport transport,
                                          Clip cursorClip) {
        for (int i = 0; i < Globals.NUMBER_OF_SCENES; i++) {
            if (!sceneBank.getScene(i).exists().get()) {
                project.createScene();
                host.showPopupNotification("New scene created");
            }

            ClipLauncherSlot clip = slotBank.getItemAt(i);
            if (clip.isRecording().get()) {
                clip.launch();

                clip.select();
                clip.showInEditor();
                host.scheduleTask(detailEditor::zoomToFit, Globals.VISUAL_FEEDBACK_TIMEOUT);

                quantizeClipLength(host, cursorClip, transport, detailEditor);

                break; // Stop the loop
            } else if (!clip.hasContent().get()) {
                slotBank.record(i);

                clip.select();
                clip.showInEditor();

                break; // Stop the loop
            }
        }
    }

    public static void quantizeClipLength(ControllerHost host, Clip cursorClip, Transport transport, DetailEditor detailEditor) {
        String launchQuantization = transport.defaultLaunchQuantization().get();
        if (!(launchQuantization.equals("1/4") || launchQuantization.equals("1/8"))) {
            return;
        }
        if (!cursorClip.exists().get()) {
            host.showPopupNotification("No clip to quantize. Select a clip and try again.");
            return;
        }

        host.scheduleTask(() -> { // Delay to ensure the clip is launched
            double clipLength = cursorClip.getLoopLength().get();
            double quantizedLength = clipLength;

            if (launchQuantization.equals("1/4")) { // Calculate the nearest bar
                quantizedLength = Math.floor(clipLength / Globals.BEATS_PER_BAR) * Globals.BEATS_PER_BAR;
            } else if (launchQuantization.equals("1/8")) { // Calculate the nearest beat
                quantizedLength = Math.floor(clipLength);
            }

            // NOTE: if the quantizedLength is 0, the clip length remains the same, no need for check
            cursorClip.getLoopLength().set(quantizedLength);
            host.scheduleTask(detailEditor::zoomToFit, Globals.VISUAL_FEEDBACK_TIMEOUT);
        }, 1000);
    }

    public static void switchSource(ControllerHost host, TrackBank trackBank, CursorTrack cursorTrack) {
        int trackPosition = cursorTrack.position().get();
        Track track = trackBank.getItemAt(trackPosition);
        SourceSelector inputSelector = track.sourceSelector();
        host.showPopupNotification("Has input: " + inputSelector.hasAudioInputSelected().get());
        // TODO: set input source. No API currently
    }
}
