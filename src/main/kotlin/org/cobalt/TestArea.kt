package org.cobalt

import org.cobalt.api.event.annotation.SubscribeEvent
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import org.cobalt.api.util.ReflectionUtils

object TestArea {
    val TESTAREENABLEDTHISWILLSPAMURCHATPROB = false

    @SubscribeEvent
    fun onClick(event: org.cobalt.api.event.impl.client.MouseEvent.LeftClick) {
        if (!TESTAREENABLEDTHISWILLSPAMURCHATPROB) return
        org.cobalt.api.util.ChatUtils.sendDebug("left click!")
    }

    @SubscribeEvent
    fun onRelease(event: org.cobalt.api.event.impl.client.MouseEvent.LeftRelease) {
        if (!TESTAREENABLEDTHISWILLSPAMURCHATPROB) return
        org.cobalt.api.util.ChatUtils.sendDebug("left release!")
    }

    @SubscribeEvent
    fun onRightClick(event: org.cobalt.api.event.impl.client.MouseEvent.RightClick) {
        if (!TESTAREENABLEDTHISWILLSPAMURCHATPROB) return
        org.cobalt.api.util.ChatUtils.sendDebug("right click!")
    }
    @SubscribeEvent
    fun onRightRelease(event: org.cobalt.api.event.impl.client.MouseEvent.RightRelease) {
        if (!TESTAREENABLEDTHISWILLSPAMURCHATPROB) return
        org.cobalt.api.util.ChatUtils.sendDebug("right release!")
    }
}