package org.gang.rpgsystem

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.gang.rpgsystem.party.showMemberInventory
import taewookim.PartySystem
import taewookim.PlayerInformation
import xyz.icetang.lib.kommand.kommand

class RpgInventorySystem : JavaPlugin() {
    companion object{
        val ps = Bukkit.getPluginManager().getPlugin("RPGParty") as PartySystem
        val pi = Bukkit.getPluginManager().getPlugin("RPGPlayerInformation") as PlayerInformation
    }

    override fun onEnable() {
        this.kommand {
            register("inv"){
                then("member"){
                    executes {
                        showMemberInventory(sender as Player)
                    }
                }
            }
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}