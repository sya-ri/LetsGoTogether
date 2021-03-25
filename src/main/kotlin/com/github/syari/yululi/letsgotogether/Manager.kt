package com.github.syari.yululi.letsgotogether

import com.github.syari.spigot.api.uuid.UUIDPlayer

object Manager {
    private var players = setOf<Pair<UUIDPlayer, UUIDPlayer>>()
    private var playerToPartner = mapOf<UUIDPlayer, UUIDPlayer>()

    @OptIn(ExperimentalStdlibApi::class)
    fun start(players: Set<Pair<UUIDPlayer, UUIDPlayer>>) {
        this.players = players
        playerToPartner = buildMap {
            players.forEach { (player, partner) ->
                put(player, partner)
                put(partner, player)
            }
        }
    }
}
