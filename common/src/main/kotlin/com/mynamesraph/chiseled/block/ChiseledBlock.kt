package com.mynamesraph.chiseled.block

import com.mynamesraph.chiseled.block.entity.ChiseledBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class ChiseledBlock(properties: Properties) : Block(properties.dynamicShape().noOcclusion().forceSolidOn()), EntityBlock, SimpleWaterloggedBlock {

    companion object {
        val TOP_NORTH_WEST: BooleanProperty = BooleanProperty.create(BlockCorner.TOP_NORTH_WEST.cornerName)
        val BOTTOM_NORTH_WEST: BooleanProperty = BooleanProperty.create(BlockCorner.BOTTOM_NORTH_WEST.cornerName)
        val TOP_SOUTH_WEST: BooleanProperty = BooleanProperty.create(BlockCorner.TOP_SOUTH_WEST.cornerName)
        val BOTTOM_SOUTH_WEST: BooleanProperty = BooleanProperty.create(BlockCorner.BOTTOM_SOUTH_WEST.cornerName)
        val TOP_NORTH_EAST: BooleanProperty = BooleanProperty.create(BlockCorner.TOP_NORTH_EAST.cornerName)
        val BOTTOM_NORTH_EAST: BooleanProperty = BooleanProperty.create(BlockCorner.BOTTOM_NORTH_EAST.cornerName)
        val TOP_SOUTH_EAST: BooleanProperty = BooleanProperty.create(BlockCorner.TOP_SOUTH_EAST.cornerName)
        val BOTTOM_SOUTH_EAST: BooleanProperty = BooleanProperty.create(BlockCorner.BOTTOM_SOUTH_EAST.cornerName)

        val SHAPE_TOP_NORTH_WEST: VoxelShape = Shapes.box(0.0, 0.5, 0.0, 0.5, 1.0, 0.5)
        val SHAPE_BOTTOM_NORTH_WEST: VoxelShape = Shapes.box(0.0, 0.0, 0.0, 0.5, 0.5, 0.5)
        val SHAPE_TOP_SOUTH_WEST: VoxelShape = Shapes.box(0.0, 0.5, 0.5, 0.5, 1.0, 1.0)
        val SHAPE_BOTTOM_SOUTH_WEST: VoxelShape = Shapes.box(0.0, 0.0, 0.5, 0.5, 0.5, 1.0)
        val SHAPE_TOP_NORTH_EAST: VoxelShape = Shapes.box(0.5, 0.5, 0.0, 1.0, 1.0, 0.5)
        val SHAPE_BOTTOM_NORTH_EAST: VoxelShape =  Shapes.box(0.5, 0.0, 0.0, 1.0, 0.5, 0.5)
        val SHAPE_TOP_SOUTH_EAST: VoxelShape = Shapes.box(0.5, 0.5, 0.5, 1.0, 1.0, 1.0)
        val SHAPE_BOTTOM_SOUTH_EAST: VoxelShape = Shapes.box(0.5, 0.0, 0.5, 1.0, 0.5, 1.0)
    }

    val defaultStateNoWater: BlockState = defaultBlockState().setValue(BlockStateProperties.WATERLOGGED,false)

    override fun useShapeForLightOcclusion(state: BlockState): Boolean = true

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(
            TOP_NORTH_WEST,
            BOTTOM_NORTH_WEST,
            TOP_SOUTH_WEST,
            BOTTOM_SOUTH_WEST,
            TOP_NORTH_EAST,
            BOTTOM_NORTH_EAST,
            TOP_SOUTH_EAST,
            BOTTOM_SOUTH_EAST,
            BlockStateProperties.WATERLOGGED
        )
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(BlockStateProperties.WATERLOGGED)) Fluids.WATER.getSource(false)
        else super.getFluidState(state)
    }

    override fun getShape(
        blockState: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {

        var shape = Shapes.empty()

        if (blockState.getValue(TOP_NORTH_WEST))
            shape = SHAPE_TOP_NORTH_WEST
        if (blockState.getValue(BOTTOM_NORTH_WEST))
            shape = Shapes.join(shape,SHAPE_BOTTOM_NORTH_WEST, BooleanOp.OR)
        if (blockState.getValue(TOP_SOUTH_WEST))
            shape = Shapes.join(shape,SHAPE_TOP_SOUTH_WEST, BooleanOp.OR)
        if (blockState.getValue(BOTTOM_SOUTH_WEST))
            shape = Shapes.join(shape,SHAPE_BOTTOM_SOUTH_WEST, BooleanOp.OR)
        if (blockState.getValue(TOP_NORTH_EAST))
            shape = Shapes.join(shape,SHAPE_TOP_NORTH_EAST, BooleanOp.OR)
        if (blockState.getValue(BOTTOM_NORTH_EAST))
            shape = Shapes.join(shape,SHAPE_BOTTOM_NORTH_EAST, BooleanOp.OR)
        if (blockState.getValue(TOP_SOUTH_EAST))
            shape = Shapes.join(shape,SHAPE_TOP_SOUTH_EAST, BooleanOp.OR)
        if (blockState.getValue(BOTTOM_SOUTH_EAST))
            shape = Shapes.join(shape,SHAPE_BOTTOM_SOUTH_EAST, BooleanOp.OR)

        return shape
    }



    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity {
        return ChiseledBlockEntity(pos,state)
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        val be = level.getBlockEntity(pos)

        if (be is ChiseledBlockEntity && player.getItemInHand(hand).`is`(Items.STICK)) {
            if (level.isClientSide) {
                player.displayClientMessage(Component.literal("Client CopiedState: ${be.copiedState}"),false)
            } else {
                player.displayClientMessage(Component.literal("Server CopiedState: ${be.copiedState}"),false)
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }


}