package com.mynamesraph.chiseled.loot

import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.block.entity.ChiseledBlockEntity
import com.mynamesraph.chiseled.registry.ChiseledLootContextParamSets
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3

fun spawnChiselingLoot(rolls: Int, level: ServerLevel, pos: BlockPos, player: ServerPlayer) {
    if (rolls <= 0) return

    for (i in 0..<rolls) {
        val state = level.getBlockState(pos)
        val be = level.getBlockEntity(pos)

        if (be is ChiseledBlockEntity) {
            val items = getChiselingDrops(pos,player.mainHandItem,state,be,level)

            for (item in items) {
                val itemEntity = ItemEntity(level, pos.center.x, pos.center.y, pos.center.z,item)
                itemEntity.setDefaultPickUpDelay()
                level.addFreshEntity(itemEntity)
            }
        }
        else {
            Constants.LOG.warn("Tried to spawn chiseling loot on a block that was not a ChiseledBlockEntity!")
        }

    }
}

fun getChiselingDrops(pos: BlockPos,tool: ItemStack, state: BlockState,blockEntity: ChiseledBlockEntity, level: ServerLevel): List<ItemStack> {

    val lootParams = LootParams.Builder(level).withParameter(
            LootContextParams.ORIGIN, Vec3(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()
            ))
        .withParameter(LootContextParams.TOOL,tool)
        .withParameter(LootContextParams.BLOCK_STATE, state)
        .withParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
        .create(ChiseledLootContextParamSets.CHISELING)

    val blockLocation = BuiltInRegistries.BLOCK.getKey(blockEntity.copiedState.block)
    val lootTableLocation = ResourceLocation.fromNamespaceAndPath(
        blockLocation.namespace,
        "chiseling/${blockLocation.path}"
    )

    val lootTable = level.server.reloadableRegistries().getLootTable(
        ResourceKey.create(Registries.LOOT_TABLE,lootTableLocation)
    )

    return lootTable.getRandomItems(lootParams)
}