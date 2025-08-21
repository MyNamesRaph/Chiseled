package com.mynamesraph.chiseled

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mynamesraph.chiseled.block.BlockCorner
import com.mynamesraph.chiseled.block.ChiseledBlock
import com.mynamesraph.chiseled.block.entity.ChiseledBlockEntity
import com.mynamesraph.chiseled.compat.arealib.AllowChiselingAreaComponent
import com.mynamesraph.chiseled.compat.arealib.ChiseledAreaComponents
import com.mynamesraph.chiseled.compat.arealib.ChiseledAreaGamerules
import com.mynamesraph.chiseled.item.ChiselItem
import com.mynamesraph.chiseled.loot.spawnChiselingLoot
import com.mynamesraph.chiseled.networking.ClientboundChiseledBlockPayload
import com.mynamesraph.chiseled.networking.ServerboundChiseledBlockPayload
import com.mynamesraph.chiseled.registry.ChiseledBlocks
import com.mynamesraph.chiseled.registry.FabricBlockEntities
import com.mynamesraph.chiseled.registry.FabricBlocks
import com.mynamesraph.chiseled.registry.FabricCreativeTabs
import com.mynamesraph.chiseled.registry.FabricItems
import dev.doublekekse.area_lib.command.argument.AreaArgument
import dev.doublekekse.area_lib.data.AreaSavedData
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.commands.Commands
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

object ChiseledFabric : ModInitializer {
    override fun onInitialize() {
        ChiseledCommon.init()

        FabricItems
        FabricBlocks
        FabricBlockEntities
        FabricCreativeTabs

        registerServerboundPayloads()
        //registerClientboundPayloads()
        registerEvents()

        if (FabricLoader.getInstance().isModLoaded("area_lib")) {
            Constants.LOG.info(
                "Area lib is installed!" +
                " Chiseling will be limited to areas marked [true] using the /chiseled set area [id] [true/false] command" +
                " This behaviour may be disabled using the limitChiselingToChiselingAreas gamerule."
            )
            ChiseledAreaComponents
            ChiseledAreaGamerules

            CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
                dispatcher.register(Commands.literal("chiseled").requires {
                    it.hasPermission(2)
                }.then(Commands.literal("set")
                    .then(Commands.literal("area")
                    .then(Commands.argument(
                        "id", AreaArgument.area()
                    ).then(Commands.argument("active", BoolArgumentType.bool()).executes {
                        val area = AreaArgument.getArea(it,"id")
                        val active = BoolArgumentType.getBool(it,"active")

                        if (active) {
                            area.put(
                                it.source.server,
                                ChiseledAreaComponents.ALLOW_CHISELING_AREA,
                                AllowChiselingAreaComponent()
                            )
                        } else {
                            area.remove(
                                it.source.server,
                                ChiseledAreaComponents.ALLOW_CHISELING_AREA
                            )
                        }

                        it.source.sendSuccess(
                            {
                                if (active) { Component.translatable("chiseled.command.chiseled_set_area.true") }
                                else { Component.translatable("chiseled.command.chiseled_set_area.false") }
                            },
                            true
                        )

                        return@executes 1
                    }))))
                )
            }
        }
    }

    fun registerEvents() {
        PlayerPickItemEvents.BLOCK.register(::onPickBlock)
    }

    fun onPickBlock(player: ServerPlayer ,pos: BlockPos , state: BlockState, requestIncludeData: Boolean): ItemStack? {
        val be = player.level().getBlockEntity(pos)

        if (be is ChiseledBlockEntity) {
            return be.copiedState.block.asItem().defaultInstance
        }

        return null
    }


    fun registerClientboundPayloads() {
        PayloadTypeRegistry.playS2C().register(
            ClientboundChiseledBlockPayload.TYPE,
            ClientboundChiseledBlockPayload.STREAM_CODEC
        )
    }

    fun registerServerboundPayloads() {
        PayloadTypeRegistry.playC2S().register(
            ServerboundChiseledBlockPayload.TYPE,
            ServerboundChiseledBlockPayload.STREAM_CODEC
        )
        ServerPlayNetworking.registerGlobalReceiver(ServerboundChiseledBlockPayload.TYPE, ::onServerboundChiseledBlockPayload)
    }

    //TODO move somewhere else and make it common code
    //TODO add areaLib support with a gamerule
    fun onServerboundChiseledBlockPayload(payload: ServerboundChiseledBlockPayload,context: ServerPlayNetworking.Context) {
        val player = context.player()
        val level = player.level()
        val pos = payload.blockPos

        if (FabricLoader.getInstance().isModLoaded("area_lib")) {
            if (!isValidArea(level,pos)) {
                player.displayClientMessage(Component.translatable("chiseled.warning.not_chiselable_area"),true)
                return
            }
        }

        if (!player.abilities.mayBuild) {
            Constants.LOG.warn(
                "Received ${ServerboundChiseledBlockPayload::class.simpleName} from player ${player.humanReadable()}"
                + " who cannot build!"
            )
            return
        }

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
                    context.player(),
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

        if (!player.canInteractWithBlock(pos, 1.0)) {
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
                (FabricBlocks.map[ChiseledBlocks.CHISELED_BLOCK]!! as ChiseledBlock).defaultStateNoWater,
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

    fun isValidArea(level: ServerLevel, pos: BlockPos): Boolean {
        if (level.gameRules.getBoolean(ChiseledAreaGamerules.LIMIT_CHISELING_TO_CHISELING_AREAS)) {
            val areas = AreaSavedData.getServerData(level.server).findTrackedAreasContaining(level,pos.center)

            for (area in areas) {
                Constants.LOG.error(area.type.toString())
                if (area.get(ChiseledAreaComponents.ALLOW_CHISELING_AREA) != null) {
                    return true
                }
            }
            return false
        }

        return true
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

}