package com.mynamesraph.chiseled

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mynamesraph.chiseled.compat.arealib.ChiseledAreaComponents
import com.mynamesraph.chiseled.compat.arealib.ChiseledAreaGamerules
import com.mynamesraph.chiseled.event.FabricServerEvents
import com.mynamesraph.chiseled.registry.FabricBlockEntities
import com.mynamesraph.chiseled.registry.FabricBlocks
import com.mynamesraph.chiseled.registry.FabricCreativeTabs
import com.mynamesraph.chiseled.registry.FabricItems
import dev.doublekekse.area_lib.command.argument.AreaArgument
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.IdentifierArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permissions
import net.minecraft.util.Unit

object ChiseledFabric : ModInitializer {
    override fun onInitialize() {
        ChiseledCommon.init()

        FabricItems
        FabricBlocks
        FabricBlockEntities
        FabricCreativeTabs

        FabricServerEvents.registerServerboundPayloads()
        FabricServerEvents.registerEvents()

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
                    //it.hasPermission(2)
                    it.permissions().hasPermission(Permissions.COMMANDS_ADMIN)
                }.then(Commands.literal("set")
                    .then(Commands.literal("area")
                    .then(Commands.argument(
                        "id", IdentifierArgument.id()
                    ).then(Commands.argument("active", BoolArgumentType.bool()).executes {
                        val area = AreaArgument.getArea(it,"id")
                        val active = BoolArgumentType.getBool(it,"active")

                        if (active) {
                            area.put(
                                it.source.server,
                                ChiseledAreaComponents.ALLOW_CHISELING_AREA,
                                Unit.INSTANCE
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
}