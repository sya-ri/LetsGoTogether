package com.github.syari.yululi.letsgotogether

import com.github.syari.spigot.api.event.EventRegister
import com.github.syari.spigot.api.event.Events
import com.github.syari.yululi.letsgotogether.Manager.pairData
import org.bukkit.event.player.PlayerRespawnEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent

object EventListener : EventRegister {
    override fun Events.register() {
        event<PlayerRespawnEvent> {
            it.player.pairData?.getPartner(it.player)?.location?.let(it::setRespawnLocation)
        }
        event<PlayerSpawnLocationEvent> {
            it.player.pairData?.getPartner(it.player)?.location?.let(it::setSpawnLocation)
        }
    }
}
