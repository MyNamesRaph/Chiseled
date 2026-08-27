package com.mynamesraph.chiseled

import com.mynamesraph.chiseled.registry.ChiseledBlocks
import com.mynamesraph.chiseled.registry.FabricBlocks
import com.mynamesraph.chiseled.rendering.ChiseledBlockModelWrapper
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.color.block.BlockTintSource
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.util.ARGB
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import java.util.List


@Environment(EnvType.CLIENT)
object ChiseledFabricClient: ClientModInitializer {
    override fun onInitializeClient() {
        ChiseledBlockModelWrapper

        /*BlockColorRegistry.register(listOf(object : BlockTintSource {
            override fun colorInWorld(state: BlockState, level: BlockAndTintGetter, pos: BlockPos): Int {
                return if (level. == TintIndexOverrides.GRASS_BLOCK.tintIndex) {
                    Minecraft.getInstance().level!!.getBiome(pos).value().getGrassColor(pos.x.toDouble(),pos.y.toDouble())
                } else {
                    Minecraft.getInstance().level!!.getBiome(pos).value().foliageColor
                }
            }

            override fun color(state: BlockState): Int {
                return ARGB.opaque(0xFFDAB9) // Color code in hex format
            }
        }),FabricBlocks.map[ChiseledBlocks.CHISELED_BLOCK])

        ColorProviderRegistry.BLOCK.register({ state, view, pos, tintIndex ->
            if (tintIndex == TintIndexOverrides.GRASS_BLOCK.tintIndex) {
                Minecraft.getInstance().level!!.getBiome(pos!!).value().getGrassColor(pos.x.toDouble(),pos.y.toDouble())
            }
            else {
                Minecraft.getInstance().level!!.getBiome(pos!!).value().foliageColor
            }
        }, FabricBlocks.map[ChiseledBlocks.CHISELED_BLOCK])*/



        /*ClientPlayNetworking.registerGlobalReceiver(ClientboundChiseledBlockPayload.TYPE) { payload, context ->

            Constants.LOG.info("RECEIVED ClientboundChiseledBlockPayload: ${payload.blockPos}, ${payload.blockState}")

            val level = context.client().level

            if (level == null) {
                Constants.LOG.warn("Level was null when processing ClientboundChiseledBlockPayload!")
                return@registerGlobalReceiver
            }


            val be = level.getBlockEntity(payload.blockPos)

            if (be is ChiseledBlockEntity) {
                be.setCopiedState(payload.blockState)

                Constants.LOG.info(be.copiedState.toString())
            }
            else {
                Constants.LOG.info("Not ChiseledBlockEntity: $be at ${payload.blockPos}, block is : ${level.getBlockState(payload.blockPos)}")
            }
        }*/
    }

    enum class TintIndexOverrides(val tintIndex: Int) {
        GRASS_BLOCK(612455);
    }
}