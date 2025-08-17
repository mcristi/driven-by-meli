package com.mcristi.controllers;

import com.bitwig.extension.controller.api.*;
import com.mcristi.Globals;
import com.mcristi.utils.*;

public class PaintAudioMidiCaptain {

    // Midi CC mappings
    private static final int B1 = 53;
    private static final int B2 = 54;
    private static final int B3 = 55;
    private static final int B4 = 56;

    private static final int BA = 65, BA_LONG = 57;
    private static final int BB = 67, BB_LONG = 66;
    private static final int BC_1 = 60, BC_2 = 61, BC_3 = 62, BC_LONG = 59;
    private static final int BD = 68, BD_LONG = 63;

    private static final int UP = 58, DOWN = 69;
    private static final int EXP1 = 50, EXP2 = 51, ENCODER = 52;

    // State
    private enum ExpressionMode {
        VOLUME,
        DEVICE_PARAM_1,
        DEVICE_PARAM_2,
        PAN
    }
    private static ExpressionMode expressionMode = ExpressionMode.VOLUME;
    private static boolean openWindowOnArm = true;

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
    private final CursorRemoteControlsPage cursorRemoteControlsPage;

    public PaintAudioMidiCaptain(ControllerHost host, Transport transport, Application application,
                                 TrackBank trackBank, SceneBank sceneBank, Clip cursorClip,
                                 Project project, DetailEditor detailEditor, CursorTrack cursorTrack,
                                 PinnableCursorDevice cursorDevice, CursorRemoteControlsPage cursorRemoteControlsPage) {
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
        this.cursorRemoteControlsPage = cursorRemoteControlsPage;
    }

    public void handleMidiEvent(int data1, int data2) {
//        host.showPopupNotification("Data received: " + data1 + " " + data2);

//        Action[] actions = application.getActions();
//        for (Action action : actions) {
//            if (action.getName().contains("Follow") || action.getName().contains("follow")) {
//                host.println("Action: " + action.getName() + " : " + action.getId());
//            }
//        }

        switch (data1) {
            case B1:
                int loopStartIncrement = transport.defaultLaunchQuantization().get().equals("1/4") ? 4 : 1;
                if (data2 == 1) {
                    cursorClip.getLoopStart().inc(loopStartIncrement);
                    cursorClip.getLoopLength().inc(-loopStartIncrement);
                } else if (data2 == 2) {
                    cursorClip.getLoopStart().inc(-loopStartIncrement);
                    cursorClip.getLoopLength().inc(loopStartIncrement);
                }
                host.scheduleTask(() -> {
                    cursorClip.getPlayStart().set(cursorClip.getLoopStart().get());
                    detailEditor.zoomToFit();
                }, Globals.VISUAL_FEEDBACK_TIMEOUT);
                break;

            case B2:
                int loopLengthIncrement = transport.defaultLaunchQuantization().get().equals("1/4") ? 4 : 1;
                if (data2 == 1) {
                    cursorClip.getLoopLength().inc(-loopLengthIncrement);
                } else if (data2 == 2) {
                    cursorClip.getLoopLength().inc(loopLengthIncrement);
                }
                host.scheduleTask(() -> {
                    cursorClip.getPlayStart().set(cursorClip.getLoopStart().get());
                    detailEditor.zoomToFit();
                }, Globals.VISUAL_FEEDBACK_TIMEOUT);
                break;

            case B3:
                if (data2 == 1) {
                    PaintAudioMidiCaptain.expressionMode = ExpressionMode.VOLUME;
                    host.showPopupNotification("Expression Mode: Volume");
                } else if (data2 == 2) {
                    PaintAudioMidiCaptain.expressionMode = ExpressionMode.DEVICE_PARAM_1;
                    host.showPopupNotification("Expression Mode: Device Param 1");
                } else if (data2 == 3) {
                    PaintAudioMidiCaptain.expressionMode = ExpressionMode.DEVICE_PARAM_2;
                    host.showPopupNotification("Expression Mode: Device Param 2");
                } else if (data2 == 4) {
                    PaintAudioMidiCaptain.expressionMode = ExpressionMode.PAN;
                    host.showPopupNotification("Expression Mode: Pan");
                }
                break;

            case B4:
                final String quantization = switch (data2)
                {
                    case 16 -> "1/16";
                    case 8 -> "1/8";
                    case 4 -> "1/4";
                    case 1 -> "1";
                    case 0 -> "none";
                    default -> "1/4";
                };
                transport.defaultLaunchQuantization().set(quantization);
                host.showPopupNotification("Quantization: " + quantization);
                break;

            case BA:
                if (data2 == OFF) {
                    transport.isPlaying().set(false);
                } else if (data2 == ON) {
                    transport.continuePlayback();
                }
                break;
            case BA_LONG:
                if (data2 == OFF) {
                    transport.stop();
                }

            case BB:
                if (data2 == OFF) {
                    transport.tapTempo();
                }
                break;
            case BB_LONG:
                if (data2 == OFF) {
                    transport.isMetronomeEnabled().toggle();
                }
                break;

            case BC_1:
                if (data2 == OFF) {
                    application.getAction("Select sub panel 3").invoke();
                    TrackUtils.arm(host, trackBank, cursorTrack, cursorDevice, 0, PaintAudioMidiCaptain.openWindowOnArm);
                }
                break;
            case BC_2:
                if (data2 == OFF) {
                    application.getAction("Select sub panel 3").invoke();
                    TrackUtils.arm(host, trackBank, cursorTrack, cursorDevice, 1, PaintAudioMidiCaptain.openWindowOnArm);
                }
                break;
            case BC_3:
                if (data2 == OFF) {
                    application.getAction("Select sub panel 3").invoke();
                    TrackUtils.arm(host, trackBank, cursorTrack, cursorDevice, 2, PaintAudioMidiCaptain.openWindowOnArm);
                }
                break;
            case BC_LONG:
                if (data2 == OFF) {
                    PaintAudioMidiCaptain.openWindowOnArm = !PaintAudioMidiCaptain.openWindowOnArm;
                    host.scheduleTask(() -> cursorDevice.isWindowOpen().set(PaintAudioMidiCaptain.openWindowOnArm), Globals.VISUAL_FEEDBACK_TIMEOUT);
                    host.showPopupNotification("Open First Device Window On Arm: " + PaintAudioMidiCaptain.openWindowOnArm);
                }
                break;

            case BD:
                if (data2 == OFF) {
                    application.getAction("Select sub panel 1").invoke();
                    RecordUtils.recordClip(host, trackBank, sceneBank, project, detailEditor, transport, cursorClip);
                }
                break;
            case BD_LONG:
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

            case EXP1:
                if (PaintAudioMidiCaptain.expressionMode == ExpressionMode.VOLUME) {
                    TrackUtils.setVolume(trackBank, cursorTrack, data2);
                } else if (PaintAudioMidiCaptain.expressionMode == ExpressionMode.DEVICE_PARAM_1) {
                    DeviceUtils.setParameter(cursorRemoteControlsPage, 0, data2);
                } else if (PaintAudioMidiCaptain.expressionMode == ExpressionMode.DEVICE_PARAM_2) {
                    DeviceUtils.setParameter(cursorRemoteControlsPage, 1, data2);
                } else if (PaintAudioMidiCaptain.expressionMode == ExpressionMode.PAN) {
                    TrackUtils.setPan(trackBank, cursorTrack, data2);
                }
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
