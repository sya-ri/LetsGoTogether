package com.github.syari.yululi.letsgotogether

import com.github.syari.spigot.api.uuid.UUIDPlayer
import org.bukkit.entity.Player

object Manager {
    private var dataList = mapOf<UUIDPlayer, PairData>()

    const val DefaultRadius = 3.0

    var radius = DefaultRadius

    @OptIn(ExperimentalStdlibApi::class)
    fun start(players: Set<Pair<UUIDPlayer, UUIDPlayer>>) {
        dataList = buildMap {
            players.forEach { (player, partner) ->
                val data = PairData(player, partner)
                put(player, data)
                put(partner, data)
            }
        }
    }

    val Player.pairData
        get() = dataList[UUIDPlayer.from(this)]
}
