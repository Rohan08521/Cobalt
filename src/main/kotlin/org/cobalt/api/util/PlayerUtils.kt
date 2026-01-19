package org.cobalt.api.util

import kotlin.math.ceil
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import org.cobalt.api.util.helper.Rotation

object PlayerUtils {

  private val mc: Minecraft =
    Minecraft.getInstance()

  /**
   * @return The player's current position
   */
  @JvmStatic
  val position: Vec3?
    get() = mc.player?.position()

  /**
   * @return The player's current FOV
   */
  @JvmStatic
  val fov: Int
    get() = mc.options.fov().get()


  /**
   * @return The player's current yaw & pitch
   */
  @JvmStatic
  val rotation: Rotation?
    get() = mc.player?.let {
      Rotation(it.yRot, it.xRot)
    }

  /**
   * @return The current position of a ClientPlayerEntity
   */
  @JvmStatic
  fun LocalPlayer.playerPos(): BlockPos {
    return BlockPos.containing(x, ceil(y - 0.25), z)
  }

}
