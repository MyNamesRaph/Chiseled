package com.mynamesraph.chiseled.item

import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.block.BlockCorner
import com.mynamesraph.chiseled.block.ChiseledBlock
import com.mynamesraph.chiseled.block.entity.ChiseledBlockEntity
import com.mynamesraph.chiseled.directionFromPositionOnBlock
import com.mynamesraph.chiseled.networking.ClientboundChiseledBlockPayload
import com.mynamesraph.chiseled.networking.ServerboundChiseledBlockPayload
import com.mynamesraph.chiseled.platform.Services
import com.mynamesraph.chiseled.registry.ChiseledBlocks
import com.mynamesraph.chiseled.tag.ChiseledTags
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.item.ToolMaterial
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult

class ChiselItem(material: ToolMaterial,attackDamage: Float,attackSpeed: Float, properties: Properties) :
    Item(
        properties.tool(
            material,
            ChiseledTags.CHISEL_MINEABLE,
            attackDamage,
            attackSpeed,
            0.0f
        )
    )
{
    companion object {
        fun stateWithoutCorner(state: BlockState, corner: Triple<Direction, Direction, Direction>): BlockState {
            when (corner) {
                BlockCorner.TOP_NORTH_WEST.asDirections -> {
                    return state.setValue(ChiseledBlock.TOP_NORTH_WEST,false)
                }
                BlockCorner.BOTTOM_NORTH_WEST.asDirections -> {
                    return state.setValue(ChiseledBlock.BOTTOM_NORTH_WEST,false)
                }
                BlockCorner.TOP_SOUTH_WEST.asDirections -> {
                    return state.setValue(ChiseledBlock.TOP_SOUTH_WEST,false)
                }
                BlockCorner.BOTTOM_SOUTH_WEST.asDirections -> {
                    return state.setValue(ChiseledBlock.BOTTOM_SOUTH_WEST,false)
                }
                BlockCorner.TOP_NORTH_EAST.asDirections -> {
                    return state.setValue(ChiseledBlock.TOP_NORTH_EAST,false)
                }
                BlockCorner.BOTTOM_NORTH_EAST.asDirections -> {
                    return state.setValue(ChiseledBlock.BOTTOM_NORTH_EAST,false)
                }
                BlockCorner.TOP_SOUTH_EAST.asDirections -> {
                    return state.setValue(ChiseledBlock.TOP_SOUTH_EAST,false)
                }
                BlockCorner.BOTTOM_SOUTH_EAST.asDirections -> {
                    return state.setValue(ChiseledBlock.BOTTOM_SOUTH_EAST,false)
                }
                else -> throw IllegalArgumentException("$corner is not a valid direction")
            }
        }

        //TODO move this stuff to a place that makes sense and make it readable and remove code duplication

        /**
         * Chisels a block on the client side and sends a [ServerboundChiseledBlockPayload]
         * to chisel the block on the server.
         *
         * On the server-side it simply checks if a block can be chiseled or not
         * @return true if block can be chiseled
         */
        fun chiselBlock(
            level: Level,
            brokenBlock: BlockState,
            pos: BlockPos,
            miningEntity: LivingEntity
        ): Boolean {
            if (brokenBlock.block is ChiseledBlock || brokenBlock.isCollisionShapeFullBlock(level, pos)) {
                if (level.isClientSide) {
                    val playerReach = miningEntity.attributes.getValue(Attributes.BLOCK_INTERACTION_RANGE)
                    val hit = miningEntity.pick(playerReach, 0.0f, false)

                    if (hit is BlockHitResult) {
                        val cornerResult = directionFromPositionOnBlock(hit, pos, miningEntity)

                        if (cornerResult.isSuccess) {

                            if (brokenBlock.block is ChiseledBlock) {
                                val newState = stateWithoutCorner(brokenBlock, cornerResult.getOrThrow())
                                level.setBlock(
                                    pos,
                                    newState,
                                    Block.UPDATE_ALL
                                )

                                val be = level.getBlockEntity(pos)

                                if (be is ChiseledBlockEntity) {
                                    if (be.cornerCount < 1) {
                                        level.destroyBlock(pos, false)
                                    }

                                    Services.PLATFORM.sendServerboundPacket(
                                        ServerboundChiseledBlockPayload(
                                            pos,
                                            BlockCorner[cornerResult.getOrThrow()]!!.ordinal.toByte()
                                        )
                                    )
                                } else {
                                    throw IllegalStateException("Chiseled Block at $pos did not have a ChiseledBlockEntity!")
                                }


                                return true
                            } else {

                                if (brokenBlock.`is`(BlockTags.AIR)) {
                                    Constants.LOG.error("Player attempted to chisel air at $pos !!")
                                    return false
                                }

                                val defaultState = (Services.PLATFORM.blockMap[ChiseledBlocks.CHISELED_BLOCK]!!
                                        as ChiseledBlock).defaultStateNoWater

                                val newState = stateWithoutCorner(defaultState, cornerResult.getOrThrow())
                                level.setBlock(
                                    pos,
                                    newState,
                                    Block.UPDATE_ALL
                                )

                                val be = level.getBlockEntity(pos)
                                if (be is ChiseledBlockEntity) {
                                    be.setCopiedState(brokenBlock)

                                }

                                Services.PLATFORM.sendServerboundPacket(
                                    ServerboundChiseledBlockPayload(
                                        pos,
                                        BlockCorner[cornerResult.getOrThrow()]!!.ordinal.toByte()
                                    )
                                )

                                return true
                            }
                        }
                        else {
                            Constants.LOG.warn("Corner resolution was not successful for block at $pos. Reason: ${cornerResult.exceptionOrNull()}")
                        }
                    }
                    else {
                        Constants.LOG.warn("Chiseling raycast at $pos failed!")
                    }
                }
                else {
                    return true
                }
            }
            Constants.LOG.debug("Player tried chiseling a block that couldn't be chiseled at {}", pos)
            return false
        }
    }

    /*override fun mineBlock(
        stack: ItemStack,
        level: Level,
        brokenBlock: BlockState,
        pos: BlockPos,
        miningEntity: LivingEntity
    ): Boolean {
        val tool = stack.get(DataComponents.TOOL)
        if (tool == null) {
            return false
        } else {
            if (!level.isClientSide) {
                if (brokenBlock.getDestroySpeed(level, pos) != 0.0f && tool.damagePerBlock() > 0) {
                    stack.hurtAndBreak(tool.damagePerBlock(), miningEntity, EquipmentSlot.MAINHAND)
                }

                if (brokenBlock.block is ChiseledBlock || brokenBlock.isCollisionShapeFullBlock(level,pos) ) {
                    //Set a temporary block so you can calculate a hit
                    level.setBlock(pos, brokenBlock, Block.UPDATE_ALL)
                    val playerReach = miningEntity.attributes.getValue(Attributes.BLOCK_INTERACTION_RANGE)
                    val hit = miningEntity.pick(playerReach,0.0f,false)

                    if (hit is BlockHitResult) {
                        val cornerResult = directionFromPositionOnBlock(hit,pos,miningEntity)

                        if (cornerResult.isSuccess) {
                            Constants.LOG.info(cornerResult.getOrThrow().toString())

                            if (brokenBlock.block is ChiseledBlock) {
                                level.setBlock(pos,stateWithoutCorner(brokenBlock,cornerResult.getOrThrow()),Block.UPDATE_ALL)

                                val be = level.getBlockEntity(pos)

                                if (be is ChiseledBlockEntity) {
                                    if (be.cornerCount < 1) {
                                        level.destroyBlock(pos,false)
                                    }
                                }
                            }
                            else {
                                val defaultState = Services.PLATFORM.blockMap[ChiseledBlocks.CHISELED_BLOCK]!!.defaultBlockState()
                                level.setBlock(pos, stateWithoutCorner(defaultState,cornerResult.getOrThrow()), Block.UPDATE_ALL)
                            }
                        }
                        else {
                            level.destroyBlock(pos,false)
                        }
                    } else {
                        level.destroyBlock(pos,false)
                    }
                }
            }
            return true
        }
    }*/
}