package com.mcristi;

import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;
import com.mcristi.controllers.AmtFs2;
import com.mcristi.controllers.PaintAudioMidiCaptain;
import com.mcristi.controllers.RolandA800Pro;

public class DrivenByMeliExtension extends ControllerExtension
{
   /**
    * Controllers handlers
    */
   private RolandA800Pro rolandA800Pro;
   private AmtFs2 amtFs2;
   private PaintAudioMidiCaptain paintAudioMidiCaptain;

   protected DrivenByMeliExtension(final DrivenByMeliExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }

   @Override
   public void init()
   {
      ControllerHost host = getHost();

      Application application = host.createApplication();
      Project project = host.getProject();
      DetailEditor detailEditor = host.createDetailEditor();

      Transport transport = host.createTransport();
      transport.isPlaying().markInterested();
      transport.defaultLaunchQuantization().markInterested();

      MasterTrack masterTrack = host.createMasterTrack(0);
      masterTrack.volume().markInterested();
      masterTrack.volume().setIndication(true);

      TrackBank trackBank = host.createTrackBank(Globals.NUMBER_OF_TRACKS, Globals.NUMBER_OF_SENDS, Globals.NUMBER_OF_SCENES);
      trackBank.itemCount().markInterested();

      SceneBank sceneBank = trackBank.sceneBank();

      for (int i = 0; i < Globals.NUMBER_OF_TRACKS; i++) {
         Track track = trackBank.getItemAt(i);
         track.arm().markInterested();
         track.trackType().markInterested();
         track.exists().markInterested();

         SourceSelector inputSelector = track.sourceSelector();
         inputSelector.hasAudioInputSelected().markInterested();

         for (int j = 0; j < Globals.NUMBER_OF_SCENES; j++) {
            sceneBank.getScene(j).exists().markInterested();

            ClipLauncherSlotBank clipLauncherSlotBank = track.clipLauncherSlotBank();
            clipLauncherSlotBank.getItemAt(j).hasContent().markInterested();
            clipLauncherSlotBank.getItemAt(j).isRecording().markInterested();
            clipLauncherSlotBank.getItemAt(j).isPlaying().markInterested();
         }
      }

      Clip cursorClip = host.createLauncherCursorClip(Globals.NUMBER_OF_TRACKS, Globals.NUMBER_OF_SCENES);
      cursorClip.exists().markInterested();
      cursorClip.getLoopLength().markInterested();
      cursorClip.getPlayStop().markInterested();

      CursorTrack cursorTrack = host.createCursorTrack("CURSOR_TRACK", "My Cursor Track", Globals.NUMBER_OF_SENDS, Globals.NUMBER_OF_SCENES, true);
      cursorTrack.position().markInterested();

      PinnableCursorDevice cursorDevice = cursorTrack.createCursorDevice("CURSOR_DEVICE", "My Cursor Device", Globals.NUMBER_OF_SENDS, CursorDeviceFollowMode.FOLLOW_SELECTION);

      CursorRemoteControlsPage cursorRemoteControlsPage = cursorDevice.createCursorRemoteControlsPage(9);
      cursorRemoteControlsPage.hasNext().markInterested();
      cursorRemoteControlsPage.hasPrevious().markInterested();
      cursorRemoteControlsPage.selectedPageIndex().markInterested();
      cursorRemoteControlsPage.setHardwareLayout(HardwareControlType.KNOB, 9);

      for (int i = 0; i < 9; i++) {
         final RemoteControl parameter = cursorRemoteControlsPage.getParameter(i);
         parameter.markInterested();
         parameter.exists().markInterested();
         parameter.setIndication(true);
      }


      // Create NoteInputs + Omni
      MidiIn midiIn0 = host.getMidiInPort(0);
      NoteInput multiBi = midiIn0.createNoteInput("MultiBi - Omni", "??????");
      NoteInput multiBi1 = midiIn0.createNoteInput("MultiBi - Ch 1", "?0????");
      NoteInput multiBi2 = midiIn0.createNoteInput("MultiBi - Ch 2", "?1????");

      multiBi.setShouldConsumeEvents(false);
      multiBi1.setShouldConsumeEvents(false);

      midiIn0.setMidiCallback(this::onMidi);
      midiIn0.setSysexCallback(this::onSysex);


      // initialize controllers
      rolandA800Pro = new RolandA800Pro(
              host, transport, application, trackBank, sceneBank,
              cursorClip, project, detailEditor, cursorRemoteControlsPage, masterTrack
      );

      amtFs2 = new AmtFs2(
              host, application, trackBank, sceneBank, project,
              detailEditor, transport, cursorClip
      );

      paintAudioMidiCaptain = new PaintAudioMidiCaptain(
              host, transport, application, trackBank, sceneBank,
              cursorClip, project, detailEditor, cursorTrack, cursorDevice,
              cursorRemoteControlsPage
      );


      host.showPopupNotification("driven-by-meli Initialized");
   }

   private void onMidi(int status, int data1, int data2) {
      // host.showPopupNotification(status + " " + data1 + " " + data2);

      if (status == 176) { // check it the event is not a midi note
         if (data1 >= 14 && data1 <= 15) {  // AMT FS2 controller range
            amtFs2.handleMidiEvent(data1, data2);
         } else if (
           (data1 >= 20 && data1 <= 32) ||   // L and B buttons
           (data1 >= 102 && data1 <= 119)    // R and S controls
        ) {
            rolandA800Pro.handleMidiEvent(data1, data2);
         } else if (data1 >= 60 && data1 <= 80) {
            paintAudioMidiCaptain.handleMidiEvent(data1, data2);
         }
      }
   }

   private void onSysex(String data) {

   }

   @Override
   public void exit()
   {
      // Perform any cleanup once the driver exits
      getHost().showPopupNotification("driven-by-meli Exited");
   }

   @Override
   public void flush()
   {
      // Send any updates you need here.
   }

}
