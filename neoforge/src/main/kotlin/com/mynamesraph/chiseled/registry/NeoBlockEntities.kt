package com.mynamesraph.chiseled.registry

import com.mynamesraph.chiseled.Constants
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import org.kodein.di.bind
import org.kodein.di.singleton
import java.util.function.Supplier

object NeoBlockEntities {
    val BLOCK_ENTITY_TYPES = DeferredRegister.create(
        Registries.BLOCK_ENTITY_TYPE,
        Constants.MOD_ID
    )

    val map = enumValues<ChiseledBlockEntities>().associateWith { be ->
        BLOCK_ENTITY_TYPES.register(
            be.blockEntity.name,
            Supplier {
                BlockEntityType(
                    be.blockEntity.factory as (BlockPos, BlockState) -> BlockEntity,
                    NeoBlocks.map.entries.filter { it.key in be.blockEntity.blocks }.map { it.value.get() }.toSet(),
                    false
                )
            }
        )
    }

    init {
        map.entries.forEach {
            Constants.di.addConfig {
                bind<BlockEntityType<*>>(it.key) {singleton { it.value.get() }}
            }
        }
    }

    fun register(eventBus: IEventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus)
    }
}