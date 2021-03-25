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
                argument { addAll("start") }
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
                    else -> {
                        sender.send(
                            """
                                &fコマンド一覧
                                &a/$label start &7ペア行動を開始します
                            """.trimIndent()
                        )
                    }
                }
            }
        }
    }
}
