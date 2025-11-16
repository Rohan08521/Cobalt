package org.cobalt.internal.loader

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlinx.io.IOException
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.impl.launch.FabricLauncherBase
import org.spongepowered.asm.mixin.Mixins

object Loader {

  private val addonsDir: Path = Paths.get("config/cobalt/addons/")

  fun loadAddons() {
    if (!Files.isDirectory(addonsDir)) {
      Files.createDirectories(addonsDir)
      return
    }

    Files.newDirectoryStream(addonsDir, "*.jar").use { stream ->
      for (jarPath in stream) {
        FabricLauncherBase.getLauncher().addToClassPath(jarPath)
      }
    }

    try {
      Files.newDirectoryStream(addonsDir, "*.jar").use { stream ->
        for (jarPath in stream) {
          registerMixin(jarPath)
        }
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }

  private fun registerMixin(jarPath: Path) {
    ZipFile(jarPath.toFile()).use { zipFile ->
      val entries = zipFile.entries()

      while (entries.hasMoreElements()) {
        val entry: ZipEntry = entries.nextElement()
        val name = entry.getName()

        if (name.endsWith(".mixins.json") && name != "cobalt.mixins.json") {
          if (name.contains("client") && FabricLoader.getInstance().environmentType != EnvType.CLIENT) {
            continue
          }

          synchronized(Mixins::class.java) {
            Mixins.addConfiguration(name)
          }
        }
      }
    }
  }

}
