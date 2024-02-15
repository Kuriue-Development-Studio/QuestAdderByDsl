package org.gang.rpgsystem.party

import io.github.monun.heartbeat.coroutines.HeartbeatScope
import io.github.monun.heartbeat.coroutines.Suspension
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.openFrame
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.SkullMeta
import org.gang.debugcat.item.metadataItem
import org.gang.rpgsystem.RpgInventorySystem
import org.gang.rpgsystem.translateColor

object PartyInventorySystem{
    private val partyPrefix = "§f[ §5파티 §f]"
    fun showMemberInventory(opener: Player){
        var partys = RpgInventorySystem.ps.getParty(opener)
        Bukkit.getLogger().info(partys.leader.name)
        Bukkit.getLogger().info(partys.players.toString())
        val inv = InvFX.frame(2, Component.text("${partys.leader.name}님의 파티")) {
            val job = HeartbeatScope().launch {
                while (partys != null) {
                    partys = RpgInventorySystem.ps.getParty(opener)
                    partys?.let {
                        item(4, 0, getPlayerHead(partys.leader,true)
                        )
                        partys.players.forEachIndexed { index, player -> // 아이템 생성
                            item(4 + ((if (index % 2 == 1) -1 else 1) * index), 1, getPlayerHead(player,false))
                        }
                        onClick{x,y,event->
                            if (event.whoClicked == it.leader){
                                val item = item(x,y)
                                if (item != null){

                                }
                            }
                        }
                        Suspension().delay(1)
                    }
                }
                opener.sendMessage("$partyPrefix 파티가 해제되었어요.")
            }
            job.invokeOnCompletion {
                opener.closeInventory()
            }
        }
        opener.openFrame(inv)

    }
    fun showPlayerOptionInventory(opener: Player,target : Player){

    }
    private fun getPlayerHead(p: Player, lead : Boolean) = metadataItem<SkullMeta>(
        Material.PLAYER_HEAD
    ) {
        setDisplayName("&r${p.name}".translateColor())
        lore = listOf(
            "&2&l${if (lead) "파티장" else "파티원"}".translateColor()
        )
        ItemFlag.values().forEach {
            addItemFlags(it)
        }
        owningPlayer = p
    }
}


