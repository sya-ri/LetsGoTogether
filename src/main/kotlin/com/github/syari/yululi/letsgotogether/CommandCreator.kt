package com.github.syari.yululi.letsgotogether

import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import com.github.syari.spigot.api.uuid.UUIDPlayer
import com.github.syari.yululi.letsgotogether.Main.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.GameMode

object CommandCreator {
    @OptIn(ExperimentalStdlibApi::class)
    fun register() {
        plugin.command("letsgotogether") {
            aliases = listOf("lgt")
            permission = "lgt.command"
            tab {
                argument { addAll("start", "radius") }
                argument("radius") { add(Manager.DefaultRadius.toString()) }
            }
            execute {
                when (args.lowerOrNull(0)) {
                    "start" -> {
                        val onlinePlayers = Bukkit.getOnlinePlayers().filter { it.gameMode != GameMode.SPECTATOR }.toMutableSet()
                        val players = buildSet {
                            onlinePlayers.toSet().forEach { player ->
                                if (onlinePlayers.contains(player)) {
                                    onlinePlayers.remove(player)
                                    onlinePlayers.randomOrNull()?.apply(onlinePlayers::remove)?.let { partner ->
                                        add(UUIDPlayer.from(player) to UUIDPlayer.from(partner))
                                        partner.teleport(player)
                                        player.sendAction("&6&lパートナーは &a&l${partner.displayName} &6&lです")
                                        partner.sendAction("&6&lパートナーは &a&l${player.displayName} &6&lです")
                                    } ?: player.sendAction("&c&lパートナーが見つかりませんでした")
                                }
                            }
                        }
                        Manager.start(players)
                    }
                    "radius" -> {
                        args.getOrNull(1)?.toDoubleOrNull()?.let {
                            Manager.radius = it
                            sender.send("&f行動可能範囲を &a$it &fにしました")
                        } ?: sender.send("&f現在の行動可能範囲は &a${Manager.radius} &fです")
                    }
                    else -> {
                        sender.send(
                            """
                                &fコマンド一覧
                                &a/$label start &7ペア行動を開始します
                                &a/$label radius <半径> &7行動可能範囲を設定します
                            """.trimIndent()
                        )
                    }
                }
            }
        }
    }
}
