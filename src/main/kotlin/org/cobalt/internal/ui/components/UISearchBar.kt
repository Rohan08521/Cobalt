package org.cobalt.internal.ui.components

import java.awt.Color
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.CharInput
import net.minecraft.client.input.KeyInput
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.internal.ui.UIComponent
import org.cobalt.internal.ui.util.TextInputHandler
import org.cobalt.internal.ui.util.isHoveringOver
import org.cobalt.internal.ui.util.mouseX
import org.lwjgl.glfw.GLFW

internal class UISearchBar : UIComponent(
  x = 0F,
  y = 0F,
  width = 300F,
  height = 40F,
) {

  private val inputHandler = TextInputHandler("", 64)
  private var focused = false
  private var dragging = false

  fun getSearchText(): String = inputHandler.getText()

  fun clearSearch() {
    inputHandler.setText("")
    focused = false
  }

  override fun render() {
    val borderColor = if (focused) Color(61, 94, 149).rgb else Color(42, 42, 42).rgb

    NVGRenderer.rect(x, y, width, height, Color(42, 42, 42, 50).rgb, 5F)
    NVGRenderer.hollowRect(x, y, width, height, 2F, borderColor, 5F)

    val textX = x + 15F
    val textY = y + 14F

    if (focused) inputHandler.updateScroll(width - 30F, 13F)

    NVGRenderer.pushScissor(x + 15F, y + 5F, width - 30F, height - 10F)

    if (focused) {
      inputHandler.renderSelection(textX, textY, 13F, 13F, Color(70, 130, 180, 100).rgb)
    }

    val text = inputHandler.getText()

    if (text.isEmpty() && !focused) {
      NVGRenderer.text("Search...", textX, textY, 13F, Color(128, 128, 128).rgb)
    } else {
      NVGRenderer.text(text, textX - inputHandler.getTextOffset(), textY, 13F, Color(230, 230, 230).rgb)
    }

    if (focused) {
      inputHandler.renderCursor(textX, textY, 13F, Color(230, 230, 230).rgb)
    }

    NVGRenderer.popScissor()
  }

  override fun mouseClicked(button: Int): Boolean {
    if (button != 0) return false

    if (isHoveringOver(x, y, width, height)) {
      focused = true
      dragging = true
      inputHandler.startSelection(mouseX.toFloat(), x + 15F, 13F)
      return true
    }

    if (focused) {
      focused = false
      return true
    }

    return false
  }

  override fun mouseReleased(button: Int): Boolean {
    if (button == 0) dragging = false
    return false
  }

  override fun mouseDragged(button: Int, offsetX: Double, offsetY: Double): Boolean {
    if (button == 0 && dragging && focused) {
      inputHandler.updateSelection(mouseX.toFloat(), x + 15F, 13F)
      return true
    }
    return false
  }

  override fun charTyped(input: CharInput): Boolean {
    if (!focused) return false

    val char = input.codepoint.toChar()
    if (char.code >= 32 && char != '\u007f') {
      inputHandler.insertText(char.toString())
      return true
    }

    return false
  }

  override fun keyPressed(input: KeyInput): Boolean {
    if (!focused) return false

    val ctrl = input.modifiers and GLFW.GLFW_MOD_CONTROL != 0
    val shift = input.modifiers and GLFW.GLFW_MOD_SHIFT != 0

    when (input.key) {
      GLFW.GLFW_KEY_ESCAPE -> {
        focused = false
        return true
      }

      GLFW.GLFW_KEY_BACKSPACE -> {
        inputHandler.backspace(); return true
      }

      GLFW.GLFW_KEY_DELETE -> {
        inputHandler.delete(); return true
      }

      GLFW.GLFW_KEY_LEFT -> {
        inputHandler.moveCursorLeft(shift); return true
      }

      GLFW.GLFW_KEY_RIGHT -> {
        inputHandler.moveCursorRight(shift); return true
      }

      GLFW.GLFW_KEY_HOME -> {
        inputHandler.moveCursorToStart(shift); return true
      }

      GLFW.GLFW_KEY_END -> {
        inputHandler.moveCursorToEnd(shift); return true
      }

      GLFW.GLFW_KEY_A -> if (ctrl) {
        inputHandler.selectAll(); return true
      }

      GLFW.GLFW_KEY_C -> if (ctrl) {
        inputHandler.copy()?.let { MinecraftClient.getInstance().keyboard.clipboard = it }
        return true
      }

      GLFW.GLFW_KEY_X -> if (ctrl) {
        inputHandler.cut()?.let { MinecraftClient.getInstance().keyboard.clipboard = it }
        return true
      }

      GLFW.GLFW_KEY_V -> if (ctrl) {
        val clipboard = MinecraftClient.getInstance().keyboard.clipboard
        if (clipboard.isNotEmpty()) inputHandler.insertText(clipboard)
        return true
      }
    }

    return false
  }

}
