package com.mynamesraph.chiseled.mixin;

import com.mynamesraph.chiseled.item.ChiselItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MixinMultiPlayerGameMode {

    @Shadow @Final private Minecraft minecraft;

    @Inject(at = @At(value = "HEAD"), method = "destroyBlock", cancellable = true)
    private void chiseled$destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        assert this.minecraft.level != null;

        BlockState state = this.minecraft.level.getBlockState(pos);

        assert this.minecraft.player != null;
        ItemStack item = this.minecraft.player.getMainHandItem();

        if (item.getItem() instanceof ChiselItem) {
            if (ChiselItem.Companion.chiselBlock(this.minecraft.level,state,pos,this.minecraft.player)) {
                item.mineBlock(this.minecraft.level,state,pos,this.minecraft.player);
                cir.setReturnValue(true);
            }
        }
    }
}
