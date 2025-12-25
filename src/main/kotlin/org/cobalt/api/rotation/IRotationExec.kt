package org.cobalt.api.rotation

import net.minecraft.client.network.ClientPlayerEntity

interface IRotationExec {

  fun onRotate(player: ClientPlayerEntity)

}
