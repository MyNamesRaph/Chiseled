package com.mynamesraph.chiseled.platform

import com.mynamesraph.chiseled.platform.services.PlatformHelper
import com.mynamesraph.chiseled.registry.ChiseledBlockEntities
import com.mynamesraph.chiseled.registry.ChiseledBlocks
import com.mynamesraph.chiseled.registry.NeoBlockEntities
import com.mynamesraph.chiseled.registry.NeoBlocks
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import net.neoforged.neoforge.network.PacketDistributor

class NeoForgePlatformHelper : PlatformHelper {
    override fun getPlatformName(): String {
        return "NeoForge"
    }

    override fun isModLoaded(modId: String?): Boolean {
        return ModList.get().isLoaded(modId)
    }

    override fun isDevelopmentEnvironment(): Boolean {
        return !FMLLoader.getCurrent().isProduction
    }

    override fun sendClientboundPacket(
        player: ServerPlayer,
        payload: CustomPacketPayload
    ) {
        PacketDistributor.sendToPlayer(player,payload)
    }

    override fun sendServerboundPacket(
        payload: CustomPacketPayload
    ) {
        ClientPacketDistributor.sendToServer(payload)
    }

    override val blockMap: Map<ChiseledBlocks, Block>
        get() {
            return NeoBlocks.map.mapValues { it.value.get() }
        }

    override val blockEntityMap: Map<ChiseledBlockEntities, BlockEntityType<BlockEntity>>
        get() {
            return NeoBlockEntities.map.mapValues { it.value.get() }
        }
}