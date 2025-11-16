package org.cobalt

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.cobalt.internal.loader.Loader

class PreLaunch : PreLaunchEntrypoint {

  override fun onPreLaunch() {
    Loader.loadAddons()
  }

}
