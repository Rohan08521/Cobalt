package org.cobalt.api.util.render

import com.mojang.blaze3d.systems.RenderSystem
import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.ShapeRenderer
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.impl.render.WorldRenderContext
import org.cobalt.internal.helper.RenderLayers
import org.joml.Vector3f

object Render3D {

  @JvmStatic
  fun drawBox(context: WorldRenderContext, box: AABB, color: Color, esp: Boolean = false) {
    if (!FrustumUtils.isVisible(context.frustum, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
      return
    }

    val matrix = context.matrixStack ?: return
    val bufferSource = context.consumers as? MultiBufferSource.BufferSource ?: return

    val r = color.red / 255f
    val g = color.green / 255f
    val b = color.blue / 255f

    val fillLayer = if (esp) RenderLayers.TRIANGLE_STRIP_ESP else RenderLayers.TRIANGLE_STRIP
    val lineLayer = if (esp) RenderLayers.LINE_LIST_ESP else RenderLayers.LINE_LIST

    matrix.pushPose()
    with(context.camera.position) { matrix.translate(-x, -y, -z) }

    ShapeRenderer.addChainedFilledBoxVertices(
      matrix,
      bufferSource.getBuffer(fillLayer),
      box.minX, box.minY, box.minZ,
      box.maxX, box.maxY, box.maxZ,
      r, g, b, 150 / 255F
    )

    ShapeRenderer.renderLineBox(
      matrix.last(),
      bufferSource.getBuffer(lineLayer),
      box.minX, box.minY, box.minZ,
      box.maxX, box.maxY, box.maxZ,
      r, g, b, 1f
    )

    matrix.popPose()
    bufferSource.endBatch(fillLayer)
    bufferSource.endBatch(lineLayer)
  }

  @JvmStatic
  fun drawLine(
    context: WorldRenderContext,
    start: Vec3,
    end: Vec3,
    color: Color,
    esp: Boolean = false,
    thickness: Float = 1f,
  ) {
    if (!FrustumUtils.isVisible(
        context.frustum,
        min(start.x, end.x), min(start.y, end.y), min(start.z, end.z),
        max(start.x, end.x), max(start.y, end.y), max(start.z, end.z)
      )
    ) return

    val matrix = context.matrixStack ?: return
    val bufferSource = context.consumers as? MultiBufferSource.BufferSource ?: return
    val layer = if (esp) RenderLayers.LINE_LIST_ESP else RenderLayers.LINE_LIST
    RenderSystem.lineWidth(thickness)

    matrix.pushPose()
    with(context.camera.position) { matrix.translate(-x, -y, -z) }

    val startOffset = Vector3f(start.x.toFloat(), start.y.toFloat(), start.z.toFloat())
    val direction = end.subtract(start)

    ShapeRenderer.renderVector(
      matrix,
      bufferSource.getBuffer(layer),
      startOffset,
      direction,
      color.rgb
    )

    matrix.popPose()
    bufferSource.endBatch(layer)
  }

}
