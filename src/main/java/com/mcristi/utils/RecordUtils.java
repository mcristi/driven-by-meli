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
        // TODO: find showNoteEditor, toggle does not work
        // application.toggleNoteEditor();

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
                detailEditor.zoomToFit();

                quantizeClipLength(host, cursorClip, transport);

                break; // Stop the loop
            } else if (!clip.hasContent().get()) {
                slotBank.record(i);

                clip.select();
                clip.showInEditor();

                break; // Stop the loop
            }
        }
    }

    public static void quantizeClipLength(ControllerHost host, Clip cursorClip, Transport transport) {
        host.scheduleTask(() -> { // Delay to ensure the clip is launched
            if (cursorClip.exists().get()) { // Ensure clip content is loaded
                String launchQuantization = transport.defaultLaunchQuantization().get();

                if (launchQuantization.equals("1/4")) { // Calculate the nearest multiple of 4
                    double clipLength = cursorClip.getLoopLength().get();
                    double barLength = 4.0; // bar is 4 beats
                    double numberOfBars = Math.round(clipLength / barLength);
                    numberOfBars = Math.max(1, numberOfBars); // Ensure at least 1 bar
                    double quantizedLength = numberOfBars * barLength;

                    cursorClip.getLoopLength().set(quantizedLength);
                    host.showPopupNotification("[1/4] Clip length quantized: " + clipLength + " -> " + quantizedLength);
                } else if (launchQuantization.equals("1/8")) { // Calculate the nearest beat
                    double clipLength = cursorClip.getLoopLength().get();
                    double quantizedLength = Math.floor(clipLength);

                    cursorClip.getLoopLength().set(quantizedLength);
                    host.showPopupNotification("[1/8] Clip length quantized: " + clipLength + " -> " + quantizedLength);
                }
            } else {
                host.showPopupNotification("Clip is not selected");
            }
        }, 1000);
    }

}
