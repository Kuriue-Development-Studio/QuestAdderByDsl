package org.gang.rpgsystem

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.gang.rpgsystem.custominv.CustomInventorySystem
import org.gang.rpgsystem.party.PartyInventorySystem
import taewookim.PartySystem
import taewookim.PlayerInformation
import xyz.icetang.lib.kommand.kommand


class RpgInventorySystem : JavaPlugin() {
    companion object{
        lateinit var INSTANCE : Plugin
        val ps = Bukkit.getPluginManager().getPlugin("RPGParty") as PartySystem
        val pi = Bukkit.getPluginManager().getPlugin("RPGPlayerInformation") as PlayerInformation
        var serverList = listOf<ServerData>()
    }

    override fun onLoad() {
        INSTANCE = this
    }
    override fun onEnable() {
        this.kommand {
            register("inv"){
                then("member"){
                    executes {
                        PartyInventorySystem.showMemberInventory(sender as Player)
                    }
                }
                then("customInv"){
                    executes {
                        CustomInventorySystem.CustomInv(sender as Player)
                    }
                }
            }
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
data class ServerData(
    val ping : String,
    val name:String
)