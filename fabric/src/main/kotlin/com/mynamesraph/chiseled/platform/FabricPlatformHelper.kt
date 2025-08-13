package com.mynamesraph.chiseled.platform

import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.platform.services.PlatformHelper
import com.mynamesraph.chiseled.registry.ChiseledBlocks
import com.mynamesraph.chiseled.registry.FabricBlocks
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Block

class FabricPlatformHelper() : PlatformHelper {
    override fun getPlatformName(): String {
        return "Fabric"
    }

    override fun isModLoaded(modId: String?): Boolean {
        return FabricLoader.getInstance().isModLoaded(modId)
    }

    override fun isDevelopmentEnvironment(): Boolean {
        return FabricLoader.getInstance().isDevelopmentEnvironment
    }

    override fun sendClientboundPacket(
        player: ServerPlayer,
        payload: CustomPacketPayload
    ) {
        ServerPlayNetworking.send(player,payload)
    }

    override fun sendServerboundPacket(
        payload: CustomPacketPayload
    ) {
        ClientPlayNetworking.send(payload)
    }

    override val blockMap: Map<ChiseledBlocks, Block>
        get() {
            return FabricBlocks.map
        }
}