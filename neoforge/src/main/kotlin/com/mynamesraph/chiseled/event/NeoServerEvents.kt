package com.mynamesraph.chiseled.event

import com.mynamesraph.chiseled.networking.ServerboundChiseledBlockPayload
import com.mynamesraph.chiseled.networking.onServerboundChiseledBlockPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext

object NeoServerEvents {
    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playToServer(
            ServerboundChiseledBlockPayload.TYPE,
            ServerboundChiseledBlockPayload.STREAM_CODEC,
            ::onNeoServerboundChiseledBlockPayload
        )
    }

    fun onNeoServerboundChiseledBlockPayload(payload: ServerboundChiseledBlockPayload, context: IPayloadContext) {
        onServerboundChiseledBlockPayload(payload, context.player() as ServerPlayer)
    }
}

