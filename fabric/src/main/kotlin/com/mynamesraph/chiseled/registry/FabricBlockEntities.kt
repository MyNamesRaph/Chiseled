package com.mynamesraph.chiseled.registry

import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState


object FabricBlockEntities {

    val map = enumValues<ChiseledBlockEntities>().associateWith { be ->
        register(
            be.blockEntity.name,
            be.blockEntity.factory as (BlockPos, BlockState) -> BlockEntity,
            *FabricBlocks.map.entries.filter { it.key in be.blockEntity.blocks }.map { it.value }.toTypedArray()
        )
    }

    fun <T : BlockEntity> register(
        name: String,
        factory: FabricBlockEntityTypeBuilder.Factory<T>,
        vararg blocks: Block
    ) : BlockEntityType<T> {
        val location = Identifier.fromNamespaceAndPath(
            com.mynamesraph.chiseled.Constants.MOD_ID,
            name
        )

        val blockEntityType = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            location,
            FabricBlockEntityTypeBuilder<T>.create(
                factory,
                *blocks
            ).build()
        )

        return blockEntityType
    }




    /*private fun getFactory(kClass: KClass<*>) : (BlockPos, BlockState) -> BlockEntity {
        val beClass = Class.forName("com.mynamesraph.cyanide.block.entity.Fabric${kClass.simpleName}")
        return beClass.kotlin.primaryConstructor as (BlockPos, BlockState) -> BlockEntity
    }*/

}