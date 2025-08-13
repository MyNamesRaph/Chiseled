package com.mynamesraph.chiseled.networking

import com.mynamesraph.chiseled.Constants
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.BlockState

@JvmRecord
data class ClientboundChiseledBlockPayload(val blockPos: BlockPos, val blockState: BlockState): CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }

    companion object {
        val TYPE = CustomPacketPayload.Type<ClientboundChiseledBlockPayload>(
            ResourceLocation.fromNamespaceAndPath(
                Constants.MOD_ID,
                "chiseled_block_client_sync"
            )
        )

        val STREAM_CODEC: StreamCodec<ByteBuf, ClientboundChiseledBlockPayload> = StreamCodec.composite(
            ByteBufCodecs.fromCodec(BlockPos.CODEC),
            ClientboundChiseledBlockPayload::blockPos,
            ByteBufCodecs.fromCodec(BlockState.CODEC),
            ClientboundChiseledBlockPayload::blockState,
            ::ClientboundChiseledBlockPayload
        )

    }
}
