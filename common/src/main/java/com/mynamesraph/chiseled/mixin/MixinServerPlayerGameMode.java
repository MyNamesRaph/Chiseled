package com.mynamesraph.chiseled.mixin;

import com.mynamesraph.chiseled.item.ChiselItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode {

    @Shadow @Final protected ServerPlayer player;

    @Shadow protected ServerLevel level;

    @Inject(at = @At(value = "HEAD"), method = "destroyBlock", cancellable = true)
    private void chiseled$destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = this.level.getBlockState(pos);

        ItemStack item = this.player.getMainHandItem();

        if (item.getItem() instanceof ChiselItem) {
            if (ChiselItem.Companion.chiselBlock(this.level,state,pos,this.player)) {
                item.mineBlock(this.level,state,pos,this.player);
                cir.setReturnValue(true);
            }
        }
    }
}