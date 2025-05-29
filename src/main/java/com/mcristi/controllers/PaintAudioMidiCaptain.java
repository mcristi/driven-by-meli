package com.mcristi.controllers;

import com.bitwig.extension.controller.api.*;
import com.mcristi.utils.ClipUtils;
import com.mcristi.utils.RecordUtils;
import com.mcristi.utils.SceneUtils;
import com.mcristi.utils.TrackUtils;

public class PaintAudioMidiCaptain {

    // Midi CC mappings
    private static final int B1 = 60, B2 = 61, B3 = 62, B4 = 63, UP = 64;
    private static final int BA = 65, BB = 66, BC = 67, BD = 68, DOWN = 69;
    private static final int EXP1 = 70, EXP2 = 71;

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

    public PaintAudioMidiCaptain(ControllerHost host, Transport transport, Application application,
                                 TrackBank trackBank, SceneBank sceneBank, Clip cursorClip,
                                 Project project, DetailEditor detailEditor, CursorTrack cursorTrack) {
        this.host = host;
        this.transport = transport;
        this.application = application;
        this.trackBank = trackBank;
        this.sceneBank = sceneBank;
        this.cursorClip = cursorClip;
        this.project = project;
        this.detailEditor = detailEditor;
        this.cursorTrack = cursorTrack;
    }

    public void handleMidiEvent(int data1, int data2) {
        switch (data1) {
            case B1:
                if (data2 == OFF) {
                    TrackUtils.arm(trackBank, cursorTrack, 0);
                }
                break;
            case B2:
                if (data2 == OFF) {
                    TrackUtils.arm(trackBank, cursorTrack, 1);
                }
                break;
            case B3:
                if (data2 == OFF) {
                    TrackUtils.arm(trackBank, cursorTrack, 2);
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

            default:
                break;
        }
    }
}
