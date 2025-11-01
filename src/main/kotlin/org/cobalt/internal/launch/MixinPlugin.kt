package org.cobalt.internal.launch

import com.llamalad7.mixinextras.MixinExtrasBootstrap
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class MixinPlugin : IMixinConfigPlugin {

  override fun onLoad(mixinPackage: String?) {
    MixinExtrasBootstrap.init()
  }

  override fun getRefMapperConfig(): String? = null
  override fun shouldApplyMixin(p0: String?, p1: String?): Boolean = true
  override fun acceptTargets(p0: Set<String>?, p1: Set<String>?) {}
  override fun getMixins() = null

  override fun preApply(
    p0: String?,
    p1: ClassNode?,
    p2: String?,
    p3: IMixinInfo?,
  ) {
  }

  override fun postApply(
    p0: String?,
    p1: ClassNode?,
    p2: String?,
    p3: IMixinInfo?,
  ) {
  }

}
