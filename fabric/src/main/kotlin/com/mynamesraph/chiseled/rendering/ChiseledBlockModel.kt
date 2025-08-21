package com.mynamesraph.chiseled.rendering

import com.mynamesraph.chiseled.ChiseledFabricClient
import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.block.entity.ChiseledBlockEntity
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper
import net.fabricmc.fabric.api.util.TriState
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockModelPart
import net.minecraft.client.renderer.block.model.BlockStateModel
import net.minecraft.client.renderer.chunk.ChunkSectionLayer
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.function.Predicate

class ChiseledBlockModel(model: BlockStateModel): WrapperBlockStateModel() {

    init {
        wrapped = model
    }

    override fun collectParts(
        random: RandomSource,
        parts: List<BlockModelPart>
    ) {
        super.collectParts(random, parts)
    }

    override fun collectParts(random: RandomSource): List<BlockModelPart> {
        return super.collectParts(random)
    }

    override fun particleIcon(): TextureAtlasSprite {
        return super.particleIcon()
    }

    /**
     * Horribly unoptimised.
     */
    override fun emitQuads(
        emitter: QuadEmitter,
        blockView: BlockAndTintGetter,
        pos: BlockPos,
        state: BlockState,
        random: RandomSource,
        cullTest: Predicate<Direction?>
    ) {
        val be = blockView.getBlockEntity(pos)
        if (be is ChiseledBlockEntity) {
            val copiedState = be.copiedState
            val isTranslucent = !copiedState.isSolidRender


            //Constants.LOG.info("Copied block at (${pos.x}, ${pos.y}, ${pos.z}) : $copiedState")
            val copiedSprite = Minecraft.getInstance().blockRenderer.getBlockModel(copiedState).particleIcon()
            //Constants.LOG.info("Copied sprite at (${pos.x}, ${pos.y}, ${pos.z}) : $copiedSprite")

            // If something goes wrong the particle texture is used as fallback
            val copiedSprites = arrayOf(
                copiedSprite,
                copiedSprite,
                copiedSprite,
                copiedSprite,
                copiedSprite,
                copiedSprite,
                copiedSprite
            )

            val copiedTints = arrayOf(
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1
            )

            val parts = this.collectParts(random)

            val copiedParts = Minecraft.getInstance().blockRenderer.getBlockModel(copiedState).collectParts(random)

            //Constants.LOG.info("Number of parts: ${copiedParts.size}")
            for (copiedPart in copiedParts) {
                //Constants.LOG.info("copiedPart: $copiedPart")
                for (i in 0..<ModelHelper.NULL_FACE_ID) {
                    val cullFace = ModelHelper.faceFromIndex(i)

                    val quads: MutableList<BakedQuad> = copiedPart.getQuads(cullFace)

                    //Constants.LOG.info("Number of quads: ${quads.size}")

                    for (quad in quads) {
                        //Constants.LOG.info("Texture: ${quad.sprite.contents().name()}")
                        //Constants.LOG.info("Tint: ${quad.tintIndex}")
                        copiedSprites[i] = quad.sprite()
                        if (copiedState.`is`(Blocks.GRASS_BLOCK) && !copiedState.getValue(BlockStateProperties.SNOWY)) {
                            copiedTints[i] = ChiseledFabricClient.TintIndexOverrides.GRASS_BLOCK.tintIndex
                        }
                        else {
                            copiedTints[i] = quad.tintIndex
                        }
                    }
                }
            }

            for (part in parts) {
                val ao = if (part.useAmbientOcclusion()) TriState.DEFAULT else TriState.FALSE

                for (i in 0..ModelHelper.NULL_FACE_ID) {
                    val cullFace = ModelHelper.faceFromIndex(i)

                    if (cullTest.test(cullFace)) {
                        // Skip entire quad list if possible.
                        continue
                    }

                    val quads: MutableList<BakedQuad> = part.getQuads(cullFace)

                    for (quad in quads.withIndex()) {
                        val newQuad = BakedQuad(
                            quad.value.vertices,
                            copiedTints[quad.index],
                            quad.value.direction,
                            copiedSprites[quad.index],
                            quad.value.shade,
                            quad.value.lightEmission
                        )

                        try {
                            emitter.cullFace(cullFace)
                            emitter.fromBakedQuad(newQuad)
                            emitter.spriteBake(copiedSprites[quad.index], MutableQuadView.BAKE_LOCK_UV)
                            if (isTranslucent) emitter.renderLayer(ChunkSectionLayer.TRANSLUCENT)
                            emitter.ambientOcclusion(ao)
                            emitter.shadeMode(ShadeMode.VANILLA)
                            emitter.emit()
                        }
                        catch (e: NullPointerException) {
                            Constants.LOG.error("NullPointerException caught while rendering ChiseledBlock, please report immediately: ${e.message}")
                            Constants.LOG.error("Exception rendering Quad:" +
                                    " $newQuad from ${quad.value.vertices}," +
                                    " ${copiedTints[quad.index]}," +
                                    " ${quad.value.direction}," +
                                    " ${copiedSprites[quad.index]}," +
                                    " ${quad.value.shade}," +
                                    " ${quad.value.lightEmission}." +
                                    " ${if (FabricLoader.getInstance().isModLoaded("sodium")) "Sodium is installed!" else "Sodium is not installed!"}"
                            )

                            Minecraft.getInstance().player?.displayClientMessage(
                                Component.literal(
                                    "Chiseled: An exception was caught while rendering! Please report it immediately!"
                                ).withColor(CommonColors.YELLOW),false
                            )
                        }

                    }
                }
            }
            return
        }

        super.emitQuads(emitter, blockView, pos, state, random, cullTest)
    }

    override fun createGeometryKey(
        blockView: BlockAndTintGetter,
        pos: BlockPos,
        state: BlockState,
        random: RandomSource
    ): Any? {
        return super.createGeometryKey(blockView, pos, state, random)
    }

    override fun particleSprite(
        blockView: BlockAndTintGetter,
        pos: BlockPos,
        state: BlockState
    ): TextureAtlasSprite {
        val be = blockView.getBlockEntity(pos)
        if (be is ChiseledBlockEntity) {
            return Minecraft.getInstance().blockRenderer.getBlockModel(be.copiedState).particleSprite(blockView,pos,state)
        }

        return super.particleSprite(blockView, pos, state)
    }
}