package com.mynamesraph.chiseled.block

import net.minecraft.core.Direction
import net.minecraft.core.Direction.*

enum class BlockCorner(val cornerName: String, val asDirections: Triple<Direction, Direction, Direction>) {
    TOP_NORTH_WEST("top_north_west",Triple(WEST,UP,NORTH)),
    BOTTOM_NORTH_WEST("bottom_north_west",Triple(WEST,DOWN,NORTH)),
    TOP_SOUTH_WEST("top_south_west",Triple(WEST,UP,SOUTH)),
    BOTTOM_SOUTH_WEST("bottom_south_west",Triple(WEST,DOWN,SOUTH)),
    TOP_NORTH_EAST("top_north_east",Triple(EAST,UP,NORTH)),
    BOTTOM_NORTH_EAST("bottom_north_east",Triple(EAST,DOWN,NORTH)),
    TOP_SOUTH_EAST("top_south_east",Triple(EAST,UP,SOUTH)),
    BOTTOM_SOUTH_EAST("bottom_south_east",Triple(EAST,DOWN,SOUTH));

    companion object {
        private val directionsMap = entries.associateBy { it.asDirections }

        operator fun get(directions: Triple<Direction, Direction, Direction>) = directionsMap[directions]
    }
}