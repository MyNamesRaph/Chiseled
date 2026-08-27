package com.mynamesraph.chiseled.rendering

import com.mojang.blaze3d.platform.Transparency
import com.mynamesraph.chiseled.ChiseledFabricClient
import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.block.entity.ChiseledBlockEntity
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter
import net.fabricmc.fabric.api.client.renderer.v1.mesh.ShadeMode
import net.fabricmc.fabric.api.client.renderer.v1.model.ModelHelper
import net.fabricmc.fabric.api.util.TriState
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.chunk.ChunkSectionLayer
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.util.RandomSource
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.client.renderer.block.dispatch.BlockStateModel
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart
import net.minecraft.client.resources.model.geometry.BakedQuad
import net.minecraft.client.resources.model.sprite.Material
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.function.Predicate

class ChiseledBlockModel(model: BlockStateModel): WrapperBlockStateModel() {

    init {
        wrapped = model
    }

    override fun collectParts(random: RandomSource, parts: MutableList<BlockStateModelPart>) {
        super.collectParts(random, parts)
    }

    override fun particleMaterial(): Material.Baked {
        return super.particleMaterial()
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
            val copiedSprite = Minecraft.getInstance().modelManager.blockStateModelSet.get(copiedState).particleMaterial().sprite
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

            val parts = mutableListOf<BlockStateModelPart>()

            this.collectParts(random,parts)

            val copiedParts = mutableListOf<BlockStateModelPart>()
            Minecraft.getInstance().modelManager.blockStateModelSet.get(copiedState).collectParts(random, copiedParts)

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
                        copiedSprites[i] = quad.materialInfo.sprite
                        copiedTints[i] = quad.materialInfo().tintIndex
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
                        val material = quad.value.materialInfo()
                        val bakedMaterial = Material.Baked(copiedSprites[quad.index],isTranslucent)
                        val newMaterial = BakedQuad.MaterialInfo.of(
                            bakedMaterial,
                            if (isTranslucent) {
                                Transparency.TRANSPARENT_AND_TRANSLUCENT
                            }
                            else {
                                Transparency.NONE
                            },
                            copiedTints[quad.index],
                            material.shade,
                            material.lightEmission
                        )

                        val newQuad = BakedQuad(
                            quad.value.position0(),
                            quad.value.position1(),
                            quad.value.position2(),
                            quad.value.position3(),
                            quad.value.packedUV0(),
                            quad.value.packedUV1(),
                            quad.value.packedUV2(),
                            quad.value.packedUV3(),
                            quad.value.direction,
                            newMaterial
                        )

                        try {
                            emitter.cullFace(cullFace)
                            emitter.fromBakedQuad(newQuad)
                            emitter.materialBake(bakedMaterial, MutableQuadView.BAKE_LOCK_UV)
                            //if (isTranslucent) emitter. renderLayer(ChunkSectionLayer.TRANSLUCENT)
                            emitter.ambientOcclusion(ao)
                            emitter.shadeMode(ShadeMode.VANILLA)
                            emitter.emit()
                        }
                        catch (e: NullPointerException) {
                            Constants.LOG.error("NullPointerException caught while rendering ChiseledBlock, please report immediately: ${e.message}")
                            Constants.LOG.error("Exception rendering Quad:" +
                                    " $newQuad from ${quad.value.position0}, ${quad.value.position1}, ${quad.value.position2}, ${quad.value.position3}}," +
                                    " ${copiedTints[quad.index]}," +
                                    " ${quad.value.direction}," +
                                    " ${copiedSprites[quad.index]}," +
                                    " ${quad.value.materialInfo()}," +
                                    " ${if (FabricLoader.getInstance().isModLoaded("sodium")) "Sodium is installed!" else "Sodium is not installed!"}"
                            )

                            Minecraft.getInstance().player?.sendSystemMessage(
                                Component.literal(
                                    "Chiseled: An exception was caught while rendering! Please report it immediately!"
                                ).withColor(CommonColors.YELLOW)
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

    override fun particleMaterial(
        level: BlockAndTintGetter,
        pos: BlockPos,
        state: BlockState
    ): Material.Baked {
        val be = level.getBlockEntity(pos)
        if (be is ChiseledBlockEntity) {
            return Minecraft.getInstance().modelManager.blockStateModelSet.get(be.copiedState).particleMaterial()
        }
        return super.particleMaterial(level, pos, state)
    }
}