package org.gang.rpgsystem.party

import io.github.monun.heartbeat.coroutines.HeartbeatScope
import io.github.monun.heartbeat.coroutines.Suspension
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.openFrame
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.SkullMeta
import org.gang.debugcat.item.metadataItem
import org.gang.rpgsystem.RpgInventorySystem.Companion.pi
import org.gang.rpgsystem.RpgInventorySystem.Companion.ps
import org.gang.rpgsystem.translateColor

fun showMemberInventory(opener:Player){
    var partys = ps.getParty(opener)
    Bukkit.getLogger().info(partys.leader.name)
    Bukkit.getLogger().info(partys.players.toString())
    if (partys != null){
        val inv = InvFX.frame(2, Component.text("${partys.leader.name}님의 파티")) {
            HeartbeatScope().launch {
                while (true) {
                    partys = ps.getParty(opener)
                    item(4, 0, metadataItem<SkullMeta>(
                        Material.PLAYER_HEAD
                    ) {
                        owningPlayer = partys.leader
                    }
                    )
                    partys.players.forEachIndexed { index, player -> // 아이템 생성
                        pi.load()
                        val pData = pi.getPlayerData(player)
                        if (index == 0) {
                            item(4, 1, metadataItem<SkullMeta>(
                                Material.PLAYER_HEAD
                            ) {
                                owningPlayer = player
                                setDisplayName("${player.name}")
                                lore = listOf(
                                    "&6&l파티장".translateColor(),
                                    "${pData.playerLevel.level}"
                                )
                                ItemFlag.values().forEach {
                                    addItemFlags(it)
                                }
                            }
                            )
                        } else {
                            item(4 + ((if (index % 2 == 1) -1 else 1) * index), 1, metadataItem<SkullMeta>(
                                Material.PLAYER_HEAD
                            ) {
                                setDisplayName("${player.name}")
                                lore = listOf(
                                    "&6&l파티장".translateColor()
                                )
                                ItemFlag.values().forEach {
                                    addItemFlags(it)
                                }
                                owningPlayer = player
                            }
                            )
                        }
                    }
                    onClose {
                        cancel()
                    }
                    Suspension().delay(2)
                }
            }
        }
        opener.openFrame(inv)
    }else{
        opener.sendMessage("파티가 결성되어 있지 않습니다.")
    }

}

