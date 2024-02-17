package org.gang.rpgsystem.party

import io.github.monun.invfx.InvFX.frame
import io.github.monun.invfx.openFrame
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.gang.debugcat.item.item
import org.gang.rpgsystem.*
import org.gang.rpgsystem.RpgInventorySystem.Companion.ps
import taewookim.Commands
import taewookim.Party

object PartyInventorySystem{
    private val partyPrefix = "§f[ §5파티 §f]"
    val MemberOptionItems = listOf(
        SlotItem(
            false,
            item(Material.BARRIER){
                setDisplayName("${ChatColor.WHITE}파티에서 나가기")
                lore = listOf(
                    "${ChatColor.RED}${ChatColor.BOLD}좌클릭${ChatColor.RESET}${ChatColor.WHITE}시 파티에서 탈퇴합니다."
                )
            }
        ){event,target, opener, party ->
            Commands(ps).leave(opener as CommandSender)
        }
    )

    private val playerOptionList = listOf(
        SlotItem(
            false,
            item(Material.GOLDEN_CARROT){
                setDisplayName("${ChatColor.WHITE}친구 추가")
                lore = listOf(
                    "${ChatColor.RED}${ChatColor.BOLD}좌클릭${ChatColor.RESET}${ChatColor.WHITE}시 플레이어에게 친구 추가 요청을 보냅니다."
                )
            }
        ) {event, target, opener, party ->

        },
        SlotItem(
            true,
            item(Material.BARRIER){
                setDisplayName("${ChatColor.WHITE}밴")
                lore = listOf(
                    "${ChatColor.RED}${ChatColor.BOLD}좌클릭${ChatColor.RESET}${ChatColor.WHITE}시 플레이어를 추방합니다."
                )
            }
        ) { event, target, opener, party ->
            if (event.isLeftClick) {
                Bukkit.getLogger().info("${party.players.size}")
                target?.sendMessage("$partyPrefix ${target.name}님이 ${opener.name}에게 강퇴당했습니다.")
                Commands(ps).leave(target as CommandSender)
                party.players.forEach {
                    it.sendMessage("$partyPrefix ${target.name}님이 ${opener.name}에게 강퇴당했습니다.")
                }
                opener.openFrame(getMemberInventory(opener))
            }
        },
        SlotItem(
            true,
            item(Material.EMERALD){
                setDisplayName("${ChatColor.WHITE}권한 부여")
                lore = listOf(
                    "${ChatColor.RED}${ChatColor.BOLD}좌클릭${ChatColor.RESET}${ChatColor.WHITE}시 플레이어를 파티장으로 설정합니다."
                )
            }
        ) {event, target, opener, party ->

        }
    )
    fun showMemberInventory(opener: Player){
        opener.openFrame(getMemberInventory(opener))
    }
    fun showPlayerOptionInventory(opener: Player,target : Player,party: Party){
        opener.openFrame(frame(3, Component.text(target.name)){
            item(4,0, getPlayerHead(target,target.isLeader))
            lineByOnClick(target,1,opener, playerOptionList)
            lineByListItem(1,opener, playerOptionList)
        })
    }


    // util code


}


