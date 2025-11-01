package org.cobalt.internal.module

import com.google.gson.Gson
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URI
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.util.jar.JarFile
import org.cobalt.api.module.Module
import org.spongepowered.asm.mixin.Mixins

internal object ModuleManager {

  private val modules = hashMapOf<ModuleData, Module>()
  private val directory = File("./config/cobalt/modules")
  private val gson = Gson()

  fun loadModules() {
    if (!directory.exists()) directory.mkdirs()

    val jars = directory.listFiles { it.extension == "jar" } ?: return
    if (jars.isEmpty()) return

    val modulesJsonBytes = mutableListOf<ByteArray>()

    // Dump class bytes and collect module data
    for (file in jars) {
      JarFile(file).use { jar ->
        val entries = jar.entries()
        while (entries.hasMoreElements()) {
          val entry = entries.nextElement()
          if (entry.isDirectory) continue

          val bytes = jar.getInputStream(entry).readBytes()

          when {
            entry.name.endsWith("cobalt.module.json") -> modulesJsonBytes.add(bytes)
            else -> ByteBasedStreamHandler[entry.name] = bytes
          }
        }
      }
    }

    // Make all dumped classes visible to classloader
    val classLoader = ModuleManager::class.java.classLoader
    val addUrlMethod = classLoader::class.java.methods.first { it.name == "addUrlFwd" }
    addUrlMethod.isAccessible = true
    addUrlMethod.invoke(classLoader, ByteBasedStreamHandler.url)

    // Instantize modules from JSON file
    for (jsonBytes in modulesJsonBytes) {
      val moduleData = gson.fromJson(String(jsonBytes), ModuleData::class.java)
      val clazz = Class.forName(moduleData.entryPoint, true, classLoader)
      val module = clazz.getDeclaredConstructor().newInstance() as Module

      modules[moduleData] = module
      Mixins.addConfiguration(moduleData.mixinsFile)
    }
  }


  fun getModules(): HashMap<ModuleData, Module> {
    return modules
  }

  private object ByteBasedStreamHandler : URLStreamHandler() {

    private val classBytes = mutableMapOf<String, ByteArray>()
    val url: URL = URL.of(URI("cobalt-modules", null, "/", ""), ByteBasedStreamHandler)

    operator fun set(path: String, bytes: ByteArray) {
      check(classBytes.put(path, bytes) == null)
    }

    operator fun get(path: String): ByteArray? = classBytes[path]

    override fun openConnection(url: URL): URLConnection? =
      classBytes[url.path.drop(1)]?.let { Connection(url, it) }

    private class Connection(url: URL, private val bytes: ByteArray) : URLConnection(url) {
      override fun getInputStream() = ByteArrayInputStream(bytes)
      override fun connect() = throw UnsupportedOperationException()
    }

  }

}
