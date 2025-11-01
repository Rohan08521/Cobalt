package org.cobalt.internal.launch

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.cobalt.internal.module.ModuleManager

class PreLaunch : PreLaunchEntrypoint {

  override fun onPreLaunch() {
    ModuleManager.loadModules()
  }

}
