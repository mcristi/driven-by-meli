package com.mcristi.controllers;

import com.bitwig.extension.controller.api.*;
import com.mcristi.utils.ClipUtils;
import com.mcristi.utils.RecordUtils;
import com.mcristi.utils.SceneUtils;
import com.mcristi.utils.TrackUtils;

public class PaintAudioMidiCaptain {

    // Midi CC mappings
    private static final int B1 = 60, B2 = 61, B3 = 62, B4 = 63;
    private static final int BA = 65, BB = 66, BC = 67, BD = 68;
    private static final int UP = 64, DOWN = 69;
    private static final int EXP1 = 70, EXP2 = 71, ENCODER = 72;

    // Constants
    private static final int ON = 127, OFF = 0;

    // API objects
    private final ControllerHost host;
    private final Transport transport;
    private final Application application;
    private final TrackBank trackBank;
    private final SceneBank sceneBank;
    private final Clip cursorClip;
    private final Project project;
    private final DetailEditor detailEditor;
    private final CursorTrack cursorTrack;
    private final PinnableCursorDevice cursorDevice;

    public PaintAudioMidiCaptain(ControllerHost host, Transport transport, Application application,
                                 TrackBank trackBank, SceneBank sceneBank, Clip cursorClip,
                                 Project project, DetailEditor detailEditor, CursorTrack cursorTrack,
                                 PinnableCursorDevice cursorDevice) {
        this.host = host;
        this.transport = transport;
        this.application = application;
        this.trackBank = trackBank;
        this.sceneBank = sceneBank;
        this.cursorClip = cursorClip;
        this.project = project;
        this.detailEditor = detailEditor;
        this.cursorTrack = cursorTrack;
        this.cursorDevice = cursorDevice;
    }

    public void handleMidiEvent(int data1, int data2) {
        switch (data1) {
            case B1:
                if (data2 == OFF) {
                    TrackUtils.arm(trackBank, cursorTrack, cursorDevice, 0);
                }
                break;
            case B2:
                if (data2 == OFF) {
                    TrackUtils.arm(trackBank, cursorTrack, cursorDevice, 1);
                }
                break;
            case B3:
                if (data2 == OFF) {
                    TrackUtils.arm(trackBank, cursorTrack, cursorDevice, 2);
                }
                break;
            case B4:
                if (data2 == OFF) {
                    ClipUtils.delete(cursorClip);
                }
                break;

            case UP:
                if (data2 == OFF) {
                    SceneUtils.launchPrev(sceneBank, trackBank);
                }
                break;
            case DOWN:
                if (data2 == OFF) {
                    SceneUtils.launchNext(sceneBank, trackBank);
                }
                break;

            case BA:
                if (data2 == OFF) {
                    transport.isPlaying().set(false);
                } else if (data2 == ON) {
                    transport.continuePlayback();
                }
                break;
            case BB:
                if (data2 == OFF) {
                    transport.isMetronomeEnabled().toggle();
                }
                break;
            case BC:
                if (data2 == OFF) {
                    transport.tapTempo();
                }
                break;
            case BD:
                if (data2 == OFF) {
                    RecordUtils.recordClip(host, trackBank, sceneBank, project, detailEditor, transport, cursorClip);
                }
                break;

            case EXP1:
                TrackUtils.setVolume(trackBank, cursorTrack, data2);
                break;

            case ENCODER:
                // Map MIDI controller (0-127) to BPM range (53-180)
                double bpm = 53 + (data2 / 127.0) * (180 - 53);
                // Round to nearest integer (e.g., 66.0, 67.0, 68.0)
                bpm = Math.round(bpm * 1) / 1.0;
                // Normalize BPM to tempo range (20-666 BPM maps to 0-1)
                double normalizedTempo = (bpm - 20) / (666 - 20);

                transport.tempo().set(normalizedTempo);
                break;

            default:
                break;
        }
    }
}
