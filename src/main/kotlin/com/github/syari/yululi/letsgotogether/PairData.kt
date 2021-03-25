package com.github.syari.yululi.letsgotogether

import com.github.syari.spigot.api.uuid.UUIDPlayer
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.sin

data class PairData(private val uuidPlayer: UUIDPlayer, private val uuidPartner: UUIDPlayer) {
    private var followPlayerUniqueId = uuidPlayer.uniqueId

    fun update() {
        val player = uuidPlayer.player ?: return
        val partner = uuidPartner.player ?: return
        val radius = Manager.radius
        val playerLocation = player.location
        val partnerLocation = partner.location
        var distance = if (player.world != partner.world) {
            radius
        } else {
            playerLocation.distance(partnerLocation)
        }
        if (radius <= distance) {
            if (followPlayerUniqueId == player.uniqueId) {
                player.teleport(partner)
            } else {
                partner.teleport(player)
            }
            distance = 0.0
        }
        setOf(player, partner).forEach {
            it.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.let { attribute ->
                attribute.baseValue = if (radius < (distance + 1)) 0.02 else 0.1
            }
        }
        if (player.vehicle != partner && partner.vehicle != player) {
            drawCircle(player, partnerLocation)
            drawCircle(partner, playerLocation)
        }
    }

    private fun drawCircle(viewer: Player, center: Location) {
        val increment = 2 * Math.PI / Manager.particleAmount
        repeat(Manager.particleAmount) {
            val angle = it * increment
            val x = center.x + Manager.radius * cos(angle)
            val z = center.z + Manager.radius * sin(angle)
            viewer.spawnParticle(Manager.particleType, x, center.y, z, 1, Manager.particleTypeData)
        }
    }

    fun getPartner(player: Player): Player? {
        return when (player.uniqueId) {
            uuidPlayer.uniqueId -> uuidPartner.player
            uuidPartner.uniqueId -> uuidPlayer.player
            else -> null
        }
    }

    fun setFollowPlayer(player: Player) {
        followPlayerUniqueId = player.uniqueId
    }

    fun setFollowPartner(player: Player) {
        followPlayerUniqueId = (getPartner(player) ?: player).uniqueId
    }
}
