package com.github.syari.yululi.letsgotogether

import com.github.syari.spigot.api.string.toColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun CommandSender.send(message: String) {
    sendMessage("&b[LGT] &r$message".toColor())
}

fun Player.sendAction(message: String) {
    spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(message.toColor()))
}
