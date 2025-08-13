package com.mynamesraph.chiseled

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.BlockHitResult

/**
 * Calculates which corner of a block was hit from a BlockHitResult
 *
 * @property hit HitResult on the block
 * @property pos Position of the block being hit
 * @return A Result containing a Triple with West/East, Down/Up, North/South if successful
 */
fun directionFromPositionOnBlock(hit: BlockHitResult, pos: BlockPos, player: LivingEntity) : Result<Triple<Direction, Direction, Direction>> {

    val x = hit.location.x - pos.x
    val y = hit.location.y - pos.y
    val z = hit.location.z - pos.z

    if (x > 1.0 || x < 0.0) {
        return Result.failure(IllegalStateException("The x position $x ($x, $y, $z) of the block at (${pos.x},${pos.y},${pos.x}) [center: ${pos.center}] did not match the HitResult at ${hit.location}! It should be between 1.0 and 0.0"))
    }
    if (y > 1.0 || y < 0.0) {
        return Result.failure(IllegalStateException("The y position $y ($x, $y, $z) of the block at (${pos.x},${pos.y},${pos.x}) [center: ${pos.center}] did not match the HitResult at ${hit.location}! It should be between 1.0 and 0.0"))
    }
    if (z > 1.0 || z < 0.0) {
        return Result.failure(IllegalStateException("The z position $z ($x, $y, $z) of the block at (${pos.x},${pos.y},${pos.x}) [center: ${pos.center}] did not match the HitResult at ${hit.location}! It should be between 1.0 and 0.0"))
    }

    //Constants.LOG.info("x: $x, y: $y, z: $z")

    val we = if (x == 0.5) {
        Direction.getFacingAxis(player,Direction.EAST.axis)
    }
    else if (x > 0.5) {
        Direction.EAST
    } else {
        Direction.WEST
    }

    val ud = if (y == 0.5) {
        Direction.getFacingAxis(player,Direction.UP.axis)
    } else if (y > 0.5) {
        Direction.UP
    } else {
        Direction.DOWN
    }

    val ns =if (z == 0.5) {
        Direction.getFacingAxis(player,Direction.SOUTH.axis)
    } else if (z > 0.5) {
        Direction.SOUTH
    } else {
        Direction.NORTH
    }

    return Result.success(Triple(we,ud,ns))
}
