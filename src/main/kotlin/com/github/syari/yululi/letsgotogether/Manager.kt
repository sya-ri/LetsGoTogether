package com.github.syari.yululi.letsgotogether

import com.github.syari.spigot.api.scheduler.runTaskTimer
import com.github.syari.spigot.api.uuid.UUIDPlayer
import com.github.syari.yululi.letsgotogether.Main.Companion.plugin
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

object Manager {
    private var dataList = mapOf<UUIDPlayer, PairData>()

    const val DefaultRadius = 3.0
    const val DefaultParticleAmount = 100
    const val DefaultAllowRide = true

    var radius = DefaultRadius
    var particleType = Particle.REDSTONE
    var particleTypeData: Any? = Particle.DustOptions(Color.RED, 1F)
    var particleAmount = DefaultParticleAmount
    var allowRide = DefaultAllowRide

    private var updateTask: BukkitTask? = null

    @OptIn(ExperimentalStdlibApi::class)
    fun start(players: Set<Pair<UUIDPlayer, UUIDPlayer>>) {
        dataList = buildMap {
            players.forEach { (player, partner) ->
                val data = PairData(player, partner)
                put(player, data)
                put(partner, data)
            }
        }
        updateTask = plugin.runTaskTimer(5) {
            dataList.values.forEach(PairData::update)
        }
    }

    fun stop() {
        updateTask?.cancel()
        updateTask = null
    }

    fun isStart() = updateTask != null

    val Player.pairData
        get() = dataList[UUIDPlayer.from(this)]
}
