package com.mcristi;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class DrivenByMeliExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("c84bb5e0-73c1-47ad-8ce7-85a461a409dc");
   
   public DrivenByMeliExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "driven-by-meli";
   }
   
   @Override
   public String getAuthor()
   {
      return "mcristi";
   }

   @Override
   public String getVersion()
   {
      return "0.1";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "mcristi";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "dbm";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 22;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 1;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 1;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
      if (platformType == PlatformType.WINDOWS)
      {
         // Set the correct names of the ports for auto detection on Windows platform here
      }
      else if (platformType == PlatformType.MAC)
      {
          list.add(new String[]{"Scarlett 2i4 USB"}, new String[]{"Scarlett 2i4 USB"});
      }
      else if (platformType == PlatformType.LINUX)
      {
         // Set the correct names of the ports for auto detection on Windows platform here
      }
   }

   @Override
   public DrivenByMeliExtension createInstance(final ControllerHost host)
   {
      return new DrivenByMeliExtension(this, host);
   }
}
