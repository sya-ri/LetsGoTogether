package com.github.syari.yululi.letsgotogether

import com.github.syari.spigot.api.event.EventRegister
import com.github.syari.spigot.api.event.Events
import com.github.syari.yululi.letsgotogether.Manager.pairData
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent

object EventListener : EventRegister {
    override fun Events.register() {
        event<PlayerRespawnEvent> {
            val player = it.player
            player.pairData?.getPartner(player)?.location?.let(it::setRespawnLocation)
        }
        event<PlayerSpawnLocationEvent> {
            val player = it.player
            player.pairData?.getPartner(player)?.location?.let(it::setSpawnLocation)
        }
        event<PlayerMoveEvent> {
            val player = it.player
            player.pairData?.setFollowPlayer(player)
        }
        event<PlayerTeleportEvent> {
            val player = it.player
            player.pairData?.setFollowPartner(player)
        }
        event<PlayerChangedWorldEvent> {
            val player = it.player
            player.pairData?.setFollowPartner(player)
        }
    }
}
