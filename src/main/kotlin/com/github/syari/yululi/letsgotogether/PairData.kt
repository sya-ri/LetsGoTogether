package com.github.syari.yululi.letsgotogether

import com.github.syari.spigot.api.uuid.UUIDPlayer
import org.bukkit.entity.Player

data class PairData(private val uuidPlayer: UUIDPlayer, private val uuidPartner: UUIDPlayer) {
    fun getPartner(player: Player): Player? {
        return when (player.uniqueId) {
            uuidPlayer.uniqueId -> uuidPartner.player
            uuidPartner.uniqueId -> uuidPlayer.player
            else -> null
        }
    }
}
