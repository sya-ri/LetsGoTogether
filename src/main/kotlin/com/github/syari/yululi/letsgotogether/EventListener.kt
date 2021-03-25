package com.github.syari.yululi.letsgotogether

import com.github.syari.spigot.api.event.EventRegister
import com.github.syari.spigot.api.event.Events
import com.github.syari.spigot.api.scheduler.runTaskLater
import com.github.syari.spigot.api.uuid.UUIDPlayer
import com.github.syari.yululi.letsgotogether.Manager.pairData
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import java.util.concurrent.ConcurrentSkipListSet

object EventListener : EventRegister {
    private val ignoreMove = ConcurrentSkipListSet<UUIDPlayer>()

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
            if (ignoreMove.contains(UUIDPlayer.from(player))) return@event
            player.pairData?.setFollowPlayer(player)
        }
        event<PlayerTeleportEvent> {
            if (it.cause != PlayerTeleportEvent.TeleportCause.PLUGIN) {
                val player = it.player
                val uuidPlayer = UUIDPlayer.from(player)
                ignoreMove.add(uuidPlayer)
                plugin.runTaskLater(5) {
                    ignoreMove.remove(uuidPlayer)
                }
                player.pairData?.run {
                    setFollowPartner(player)
                    update()
                }
            }
        }
    }
}
