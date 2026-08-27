package com.mynamesraph.chiseled

import com.mynamesraph.chiseled.block.entity.ChiseledBlockEntity
import com.mynamesraph.chiseled.registry.ChiseledBlocks
import com.mynamesraph.chiseled.registry.FabricBlocks
import com.mynamesraph.chiseled.rendering.ChiseledBlockModelWrapper
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry
import net.fabricmc.fabric.api.client.rendering.v1.ColorResolverRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.color.block.BlockTintSource
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.util.ARGB
import net.minecraft.world.level.ColorResolver
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState


@Environment(EnvType.CLIENT)
object ChiseledFabricClient: ClientModInitializer {
    override fun onInitializeClient() {
        ChiseledBlockModelWrapper

        BlockColorRegistry.register(listOf(object : BlockTintSource {
            override fun colorInWorld(state: BlockState, level: BlockAndTintGetter, pos: BlockPos): Int {
                val be = level.getBlockEntity(pos)

                return if (be is ChiseledBlockEntity && be.copiedState.`is`(Blocks.GRASS_BLOCK)) {
                    Minecraft.getInstance().level!!.getBiome(pos).value().getGrassColor(pos.x.toDouble(),pos.y.toDouble())
                }
                else {
                    Minecraft.getInstance().level!!.getBiome(pos).value().foliageColor
                }
            }

            override fun color(state: BlockState): Int {
                return ARGB.opaque(0x0) // Color code in hex format
            }
        }), FabricBlocks.map[ChiseledBlocks.CHISELED_BLOCK]!!)
    }
}