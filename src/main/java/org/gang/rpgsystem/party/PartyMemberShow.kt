package org.gang.rpgsystem.party

import io.github.monun.heartbeat.coroutines.HeartbeatScope
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.InvFX.frame
import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.openFrame
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.SkullMeta
import org.gang.debugcat.item.getHead
import org.gang.debugcat.item.item
import org.gang.debugcat.item.metadataItem
import org.gang.rpgsystem.RpgInventorySystem.Companion.ps
import org.gang.rpgsystem.isLeader
import org.gang.rpgsystem.translateColor
import taewookim.Commands
import taewookim.Party

object PartyInventorySystem{
    private val partyPrefix = "§f[ §5파티 §f]"

    private val leaderOptionList = listOf(
        item(Material.BARRIER){
            setDisplayName("${ChatColor.WHITE}밴")
            lore = listOf(
                "${ChatColor.RED}${ChatColor.BOLD}좌클릭${ChatColor.RESET}${ChatColor.WHITE}시 플레이어를 추방합니다."
            )
        },
        item(Material.EMERALD){
            setDisplayName("${ChatColor.WHITE}권한 부여")
            lore = listOf(
                "${ChatColor.RED}${ChatColor.BOLD}좌클릭${ChatColor.RESET}${ChatColor.WHITE}시 플레이어를 파티장으로 설정합니다."
            )
        }
    )
    fun showMemberInventory(opener: Player){
        opener.openFrame(getMemberInventory(opener))
    }
    open fun showPlayerOptionInventory(opener: Player,target : Player,party: Party){
        opener.openFrame(frame(3, Component.text(target.name)){
            val slotList = leaderOptionList.size.slot
            item(4,0, getPlayerHead(target,target.isLeader))
            leaderOptionList.forEachIndexed { index, itemStack ->
                item(slotList[index],1,itemStack)
            }
            slot(slotList.first(),1){
                onClick { it ->
                    if (it.isLeftClick) {
                        Bukkit.getLogger().info("${party.players.size}")
                        target.sendMessage("$partyPrefix ${target.name}님이 ${opener.name}에게 강퇴당했습니다.")
                        Commands(ps).leave(target as CommandSender)
                        party.players.forEach{
                            it.sendMessage("$partyPrefix ${target.name}님이 ${opener.name}에게 강퇴당했습니다.")
                        }
                        opener.openFrame(getMemberInventory(opener))
                    }
                }
            }
        })
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
    private fun getMemberInventory(p:Player): InvFrame {
        var partys = ps.getParty(p)
        return InvFX.frame(2, Component.text("${partys.leader.name}님의 파티")) {
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
                            item.getHead?.let {
                                showPlayerOptionInventory(p,it,partys)
                            }
                        }
                    }
                }
            }
            val job = HeartbeatScope().launch {
                while (true) {
                    partys = ps.getParty(p)
                    partys?.let {
                        item(4, 0, getPlayerHead(partys.leader,true)
                        )
                        partys.players.forEachIndexed { index, player -> // 아이템 생성
                            item(4 + ((if (index % 2 == 1) -1 else 1) * index), 1, getPlayerHead(player,false))
                        }
                    }
                    if (partys == null) {
                        p.closeInventory()
                        cancel()
                    }
                    delay(100L)
                }

            }
        }
    }
}
//아이템 갯수에 따라 인벤토리 슬롯을 부여하는 인라인 변수.
inline val Int.slot : List<Int> get() = when(this){
    1 -> listOf(4)
    2 -> listOf(2,6)
    3 -> listOf(1,4,7)
    4 -> listOf(0,3,5,8)
    5 -> listOf(0,2,4,6,8)
    6 -> listOf(0,1,3,5,7,8)
    7 -> listOf(1,2,3,4,5,6,7)
    8 -> listOf(0,1,2,3,5,6,7,8)
    else -> listOf(0,1,2,3,4,5,6,7,8)
}

