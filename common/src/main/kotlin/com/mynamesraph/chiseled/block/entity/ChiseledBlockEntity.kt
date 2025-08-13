package com.mynamesraph.chiseled.block.entity

import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.block.ChiseledBlock
import com.mynamesraph.chiseled.registry.ChiseledBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import org.kodein.di.direct
import org.kodein.di.instance
import kotlin.jvm.optionals.getOrElse

class ChiseledBlockEntity(
    val pos: BlockPos,
    blockState: BlockState
): BlockEntity(
    Constants.di.direct.instance(ChiseledBlockEntities.CHISELED_BLOCK_ENTITY),
    pos,
    blockState
) {


    var copiedState: BlockState = Blocks.BEDROCK.defaultBlockState()
        private set

    val cornerCount: Int
        get() {
            var count = 0

            if (blockState.getValue(ChiseledBlock.TOP_NORTH_WEST)) count++
            if (blockState.getValue(ChiseledBlock.BOTTOM_NORTH_WEST)) count++
            if (blockState.getValue(ChiseledBlock.TOP_SOUTH_WEST)) count++
            if (blockState.getValue(ChiseledBlock.BOTTOM_SOUTH_WEST)) count++
            if (blockState.getValue(ChiseledBlock.TOP_NORTH_EAST)) count++
            if (blockState.getValue(ChiseledBlock.BOTTOM_NORTH_EAST)) count++
            if (blockState.getValue(ChiseledBlock.TOP_SOUTH_EAST)) count++
            if (blockState.getValue(ChiseledBlock.BOTTOM_SOUTH_EAST)) count++

            return count
        }

    fun setCopiedState(state: BlockState) {
        copiedState = state
        setChanged()
    }


    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)

        this.copiedState = input.read("copied_state", BlockState.CODEC).getOrElse {
            Constants.LOG.warn("Could not retrieve copied state for $this at (${pos.x}, ${pos.y}, ${pos.z}!! Using default blockState from Bedrock")
            Blocks.BEDROCK.defaultBlockState()
        }

    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.store("copied_state", BlockState.CODEC,copiedState)
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return saveWithoutMetadata(registries)
    }
}