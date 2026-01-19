package org.cobalt.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.cobalt.api.event.impl.client.BlockChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
abstract class BlockBreak_ClientPlayerInteractionManagerMixin {

  @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"))
  private void onBlockChange(BlockPos pos, BlockState newState, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
    if (Minecraft.getInstance().level != (Object) this) {
      return;
    }

    BlockState oldBlock = ((Level) (Object) this).getBlockState(pos);

    if (oldBlock.getBlock() != newState.getBlock()) {
      new BlockChangeEvent(pos.immutable(), oldBlock, newState).post();
    }
  }

}
