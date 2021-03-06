package com.github.syari.yululi.letsgotogether

import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import com.github.syari.spigot.api.config.type.data.ConfigMaterialDataType
import com.github.syari.spigot.api.config.type.data.ConfigParticleDataType
import com.github.syari.spigot.api.item.editItemMeta
import com.github.syari.spigot.api.item.isUnbreakable
import com.github.syari.spigot.api.item.itemStack
import com.github.syari.spigot.api.uuid.UUIDPlayer
import com.github.syari.yululi.letsgotogether.Main.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

object CommandCreator {
    @OptIn(ExperimentalStdlibApi::class)
    fun register() {
        plugin.command("letsgotogether") {
            aliases = listOf("lgt")
            permission = "lgt.command"
            tab {
                argument { addAll("start", "stop", "radius", "particle", "ride") }
                argument("radius") { add(Manager.DefaultRadius.toString()) }
                argument("particle") { addAll("amount", "type") }
                argument("particle amount") { add(Manager.DefaultParticleAmount.toString()) }
                argument("particle type") { addAll(Particle.values().map(Particle::name)) }
                argument("particle type ${Particle.REDSTONE}") { addAll("FF0000") }
                argument("particle type ${Particle.ITEM_CRACK}") { addAll(Material.values().map(Material::name)) }
                argument(
                    "particle type ${Particle.BLOCK_CRACK}",
                    "particle type ${Particle.BLOCK_DUST}",
                    "particle type ${Particle.FALLING_DUST}"
                ) {
                    addAll(Material.values().filter(Material::isBlock).map(Material::name))
                }
                argument("ride") { add(Manager.DefaultAllowRide.toString()) }
            }
            execute {
                when (args.lowerOrNull(0)) {
                    "start" -> {
                        if (Manager.isStart()) {
                            sender.send("&cペア行動は既に開始されています")
                        } else {
                            val onlinePlayers = Bukkit.getOnlinePlayers().filter { it.gameMode != GameMode.SPECTATOR }.toMutableSet()
                            val players = buildSet {
                                onlinePlayers.toSet().forEach { player ->
                                    if (onlinePlayers.contains(player)) {
                                        onlinePlayers.remove(player)
                                        onlinePlayers.randomOrNull()?.apply(onlinePlayers::remove)?.let { partner ->
                                            add(UUIDPlayer.from(player) to UUIDPlayer.from(partner))
                                            partner.teleport(player)
                                            val colorCode = (0..0xFFFFFF).random()
                                            val color = Color.fromRGB(colorCode)
                                            val chatColor = "%6X".format(colorCode).toList().joinToString("§", "§x§")
                                            player.setDisplayName("$chatColor${player.name}")
                                            player.setPlayerListName("$chatColor${player.name}")
                                            partner.setDisplayName("$chatColor${partner.name}")
                                            partner.setPlayerListName("$chatColor${partner.name}")
                                            player.sendAction("&6&lパートナーは &a&l${partner.name} &6&lです")
                                            partner.sendAction("&6&lパートナーは &a&l${player.name} &6&lです")
                                            val helmet = itemStack(Material.LEATHER_HELMET, "${chatColor}組み分け帽子").apply {
                                                editItemMeta<LeatherArmorMeta> {
                                                    setColor(color)
                                                }
                                                isUnbreakable = true
                                            }
                                            player.inventory.helmet = helmet
                                            partner.inventory.helmet = helmet
                                        } ?: player.sendAction("&c&lパートナーが見つかりませんでした")
                                    }
                                }
                            }
                            Manager.start(players)
                        }
                    }
                    "stop" -> {
                        if (Manager.isStart()) {
                            Manager.stop()
                        } else {
                            sender.send("&cペア行動は開始されていません")
                        }
                    }
                    "radius" -> {
                        args.getOrNull(1)?.toDoubleOrNull()?.let {
                            Manager.radius = it
                            sender.send("&f行動可能範囲を &a$it &fにしました")
                        } ?: sender.send("&f現在の行動可能範囲は &a${Manager.radius} &fです")
                    }
                    "particle" -> {
                        when (args.lowerOrNull(1)) {
                            "amount" -> {
                                args.getOrNull(2)?.toIntOrNull()?.let {
                                    Manager.particleAmount = it
                                    sender.send("&fパーティクルの量を &a$it &fにしました")
                                } ?: sender.send("&f現在のパーティクルの量は ${Manager.particleAmount} &fです")
                            }
                            "type" -> {
                                args.getOrNull(2)?.let(ConfigParticleDataType::stringToEnum)?.let { particle ->
                                    Manager.particleType = particle
                                    Manager.particleTypeData = args.getOrNull(3)?.let {
                                        when (particle) {
                                            Particle.REDSTONE -> Particle.DustOptions(Color.fromRGB(it.toIntOrNull(16) ?: 0xFF0000), 1F)
                                            Particle.ITEM_CRACK -> ItemStack(ConfigMaterialDataType.stringToEnum(it) ?: Material.STONE)
                                            Particle.BLOCK_CRACK,
                                            Particle.BLOCK_DUST,
                                            Particle.FALLING_DUST -> (ConfigMaterialDataType.stringToEnum(it)?.takeIf(Material::isBlock) ?: Material.STONE).createBlockData()
                                            else -> null
                                        }
                                    }
                                    sender.send("&fパーティクルの種類を &a$particle &fにしました")
                                } ?: sender.send("&f現在のパーティクルの種類は ${Manager.particleType} &fです")
                            }
                            else -> {
                                sender.send(
                                    """
                                        &fコマンド一覧
                                        &a/$label particle amount &7パーティクルの量を変更します
                                        &a/$label particle type &7パーティクルの種類を変更します
                                    """.trimIndent()
                                )
                            }
                        }
                    }
                    "ride" -> {
                        Manager.allowRide = Manager.allowRide.not()
                        sender.send("&f肩車を ${if (Manager.allowRide) "&a有効化" else "&c無効化"} &fしました")
                    }
                    else -> {
                        sender.send(
                            """
                                &fコマンド一覧
                                &a/$label start &7ペア行動を開始します
                                &a/$label stop &7ペア行動を終了します
                                &a/$label radius <半径> &7行動可能範囲を設定します
                                &a/$label particle &7パーティクルの設定を変更します
                                &a/$label ride &7肩車の有効化を切り替えます
                            """.trimIndent()
                        )
                    }
                }
            }
        }
    }
}
