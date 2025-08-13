package com.mynamesraph.chiseled.networking

import com.mynamesraph.chiseled.Constants
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

@JvmRecord
data class ServerboundChiseledBlockPayload(val blockPos: BlockPos, val blockCornerOrdinal: Byte): CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }

    companion object {
        val TYPE = CustomPacketPayload.Type<ServerboundChiseledBlockPayload>(
            ResourceLocation.fromNamespaceAndPath(
                Constants.MOD_ID,
                "chiseled_block_server_sync"
            )
        )

        val STREAM_CODEC: StreamCodec<ByteBuf, ServerboundChiseledBlockPayload> = StreamCodec.composite(
            ByteBufCodecs.fromCodec(BlockPos.CODEC),
            ServerboundChiseledBlockPayload::blockPos,
            ByteBufCodecs.BYTE,
            ServerboundChiseledBlockPayload::blockCornerOrdinal,
            ::ServerboundChiseledBlockPayload
        )

    }
}
