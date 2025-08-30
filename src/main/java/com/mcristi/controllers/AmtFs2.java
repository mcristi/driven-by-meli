package com.mcristi.controllers;

import com.bitwig.extension.controller.api.*;
import com.mcristi.utils.RecordUtils;

public class AmtFs2 {

    // Midi CC mappings
    private static final int LEFT = 14;
    private static final int RIGHT = 15;

    // Constants
    private static final int ON = 127;

    // API objects
    private final Application application;
    private final ControllerHost host;
    private final TrackBank trackBank;
    private final SceneBank sceneBank;
    private final Project project;
    private final DetailEditor detailEditor;
    private final Transport transport;
    private final Clip cursorClip;


    public AmtFs2(ControllerHost host, Application application,
                  TrackBank trackBank, SceneBank sceneBank,
                  Project project, DetailEditor detailEditor,
                  Transport transport, Clip cursorClip) {
        this.host = host;
        this.application = application;
        this.trackBank = trackBank;
        this.sceneBank = sceneBank;
        this.project = project;
        this.detailEditor = detailEditor;
        this.transport = transport;
        this.cursorClip = cursorClip;
    }

    public void handleMidiEvent(int data1, int data2) {
        switch (data1) {
            case LEFT:
                application.undo();
                break;

            case RIGHT:
                RecordUtils.recordClip(host, trackBank, sceneBank, project, detailEditor, transport, cursorClip, true);
                break;

            default:
                break;
        }
    }

}
