package com.mcristi.controllers;

import com.bitwig.extension.controller.api.*;
import com.mcristi.utils.DeviceUtils;
import com.mcristi.utils.RecordUtils;
import com.mcristi.utils.TrackUtils;

public class RolandA800Pro {

    // Midi CC mappings
    private static final int L1 = 21, L2 = 22, L3 = 23, L4 = 24, L5 = 25, L6 = 26, L7 = 27, L8 = 28, L9 = 20;
    private static final int R1 = 102, R2 = 103, R3 = 104, R4 = 105, R5 = 106, R6 = 107, R7 = 108, R8 = 109, R9 = 119;
    private static final int S1 = 110, S2 = 111, S3 = 112, S4 = 113, S5 = 114, S6 = 115, S7 = 116, S8 = 117, S9 = 118;
    private static final int B1 = 29, B2 = 30, B3 = 31, B4 = 32;

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
    private final CursorRemoteControlsPage cursorRemoteControlsPage;
    private final MasterTrack masterTrack;

    // State
    private boolean isSend1Enabled = false;


    public RolandA800Pro(ControllerHost host, Transport transport, Application application,
                         TrackBank trackBank, SceneBank sceneBank, Clip cursorClip,
                         Project project, DetailEditor detailEditor,
                         CursorRemoteControlsPage cursorRemoteControlsPage, MasterTrack masterTrack) {
        this.host = host;
        this.transport = transport;
        this.application = application;
        this.trackBank = trackBank;
        this.sceneBank = sceneBank;
        this.cursorClip = cursorClip;
        this.project = project;
        this.detailEditor = detailEditor;
        this.cursorRemoteControlsPage = cursorRemoteControlsPage;
        this.masterTrack = masterTrack;
    }

    public void handleMidiEvent(int data1, int data2) {
        switch (data1) {
            // L buttons
            case L5:
                if (data2 == ON) {
                    transport.stop();
                }
                break;
            case L6:
            case L7:
                if (data2 == ON) {
                    transport.continuePlayback();
                }
                break;
            case L8:
                if (data2 == ON) {
                    RecordUtils.recordClip(host, trackBank, sceneBank, project, detailEditor, transport, cursorClip);
                }
                break;
            case L9:
                if (data2 == OFF) {
                    isSend1Enabled = false;
                } else if (data2 == ON) {
                    isSend1Enabled = true;
                }
                break;

            // B buttons
            case B1:
                if (data2 == ON) {
                    application.undo();
                }
                break;
            case B2:
                if (data2 == ON) {
                    cursorClip.quantize(0.5);
                }
                break;
            case B3:
                if (data2 == ON) {
                    cursorClip.quantize(0.75);
                }
                break;
            case B4:
                if (data2 == ON) {
                    cursorClip.quantize(1);
                }
                break;

            // Sliders (S)
            case S1:
                handleSlider(0, data2);
                break;
            case S2:
                handleSlider(1, data2);
                break;
            case S3:
                handleSlider(2, data2);
                break;
            case S4:
                handleSlider(3, data2);
                break;
            case S5:
                handleSlider(4, data2);
                break;
            case S6:
                handleSlider(5, data2);
                break;
            case S7:
                handleSlider(6, data2);
                break;
            case S8:
                handleSlider(7, data2);
                break;
            case S9:
                handleSlider(8, data2);
                break;

            default:
                break;
        }
    }

    private void handleSlider(int trackIndex, int value) {
        if (isSend1Enabled) {
            TrackUtils.setSend(trackBank, trackIndex, 1, value);
        } else {
            TrackUtils.setSend(trackBank, trackIndex, 0, value);
        }
    }
}
