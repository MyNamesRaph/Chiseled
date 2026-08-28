package com.mynamesraph.chiseled.networking

import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.block.BlockCorner
import com.mynamesraph.chiseled.block.ChiseledBlock
import com.mynamesraph.chiseled.block.entity.ChiseledBlockEntity
import com.mynamesraph.chiseled.item.ChiselItem
import com.mynamesraph.chiseled.loot.spawnChiselingLoot
import com.mynamesraph.chiseled.platform.Services
import com.mynamesraph.chiseled.registry.ChiseledBlocks
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.GameType
import net.minecraft.world.level.block.Block

fun onServerboundChiseledBlockPayload(payload: ServerboundChiseledBlockPayload, player: ServerPlayer) {
    val level = player.level()
    val pos = payload.blockPos

    if (payload.blockCornerOrdinal >= BlockCorner.entries.size) {
        Constants.LOG.warn(
            malformedFromPlayer(
                player,
                ServerboundChiseledBlockPayload::class.simpleName!!
            ) + " Unknown BlockCorner ordinal! : ${payload.blockCornerOrdinal}"
        )
        return
    }

    if (!level.isLoaded(pos)) {
        Constants.LOG.warn(
            malformedFromPlayer(
                player,
                ServerboundChiseledBlockPayload::class.simpleName!!
            ) + " Position is unloaded! : ${pos.x}, ${pos.y}, ${pos.z}")
        return
    }

    if (!level.mayInteract(player,pos)) {
        Constants.LOG.warn(
            "Received ${ServerboundChiseledBlockPayload::class.simpleName} from player ${player.humanReadable()}"
                    + " who cannot build at ${pos.x}, ${pos.y}, ${pos.z}!"
        )
        return
    }

    if (player.blockActionRestricted(level,pos,player.gameMode.gameModeForPlayer)) {
        Constants.LOG.warn(
            malformedFromPlayer(
                player,
                ServerboundChiseledBlockPayload::class.simpleName!!
            ) + " Block at ${pos.x}, ${pos.y}, ${pos.z} is out of player's range!"
        )
        return
    }

    val oldState = level.getBlockState(pos)

    if (oldState.block is ChiseledBlock) {
        val be = level.getBlockEntity(pos)

        if (be is ChiseledBlockEntity) {

            if (be.cornerCount-1 <= 0) {
                if (player.gameMode.gameModeForPlayer != GameType.CREATIVE) {
                    spawnChiselingLoot(1,level,pos,player)
                }
                level.destroyBlock(pos,false)
            }
            else {
                val newState = ChiselItem.stateWithoutCorner(
                    oldState,
                    BlockCorner.entries[payload.blockCornerOrdinal.toInt()].asDirections
                )

                level.setBlockAndUpdate(pos, newState)
                val newBe = level.getBlockEntity(pos)

                if (newBe is ChiseledBlockEntity) {
                    newBe.setCopiedState(be.copiedState)
                    if (player.gameMode.gameModeForPlayer != GameType.CREATIVE) {
                        spawnChiselingLoot(1,level,pos,player)
                    }
                }
                else {
                    throw IllegalStateException("Chiseled Block at $pos did not have a ChiseledBlockEntity!")
                }

                level.sendBlockUpdated(pos,oldState, newState, Block.UPDATE_ALL)
            }
        }
        else {
            throw IllegalStateException("Chiseled Block at $pos did not have a ChiseledBlockEntity!")
        }
    }
    else {
        if (oldState.`is`(BlockTags.AIR)) {
            Constants.LOG.error("Player attempted to chisel air at $pos !!")
            return
        }

        val newState = ChiselItem.stateWithoutCorner(
            (Services.PLATFORM.blockMap[ChiseledBlocks.CHISELED_BLOCK]!! as ChiseledBlock).defaultStateNoWater,
            BlockCorner.entries[payload.blockCornerOrdinal.toInt()].asDirections
        )

        level.setBlockAndUpdate(pos,newState)

        val be = level.getBlockEntity(pos)

        if (be is ChiseledBlockEntity) {
            be.setCopiedState(oldState)
            if (player.gameMode.gameModeForPlayer != GameType.CREATIVE) {
                spawnChiselingLoot(1,level,pos,player)
            }
            level.sendBlockUpdated(pos,oldState,newState,Block.UPDATE_ALL)
        }
        else {
            throw IllegalStateException("Chiseled Block at $pos did not have a ChiseledBlockEntity!")
        }
    }
}

/**
 * Generates a warning string blaming a player
 *
 * @param[player] player that generated the warning
 * @param[payloadName] name of the payload printing the warning
 * @return Received malformed [payloadName] from player [[ServerPlayer.getName], [ServerPlayer.stringUUID]]
 */
private fun malformedFromPlayer(player: ServerPlayer, payloadName: String): String {
    return "Received malformed $payloadName from player ${player.humanReadable()}"
}

private fun ServerPlayer.humanReadable(): String {
    return "[${this.name.string}, ${this.stringUUID}]"
}