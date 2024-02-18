package org.gang.rpgsystem

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.gang.rpgsystem.quest.kQuest


class QuestAdderDsl : JavaPlugin(),Listener {

    companion object{
        lateinit var INSTANCE : Plugin
        val qm = Bukkit.getPluginManager().getPlugin("QuestManager") as QuestManager
    }

    override fun onLoad() {
        INSTANCE = this
    }
    override fun onEnable() {
        server.pluginManager.registerEvents(this,this)
        kQuest {
            register("example", listOf("예제 퀘스트"),Material.GRASS_BLOCK){
                text(listOf("run first"),Material.GREEN_WOOL,"§l§a확인"){
                    text(listOf("run second"),Material.BOW,"second"){
                        accept(Material.GREEN_WOOL,"it in second as select button")
                        reject(Material.RED_WOOL,"it in second as reject button")
                        text(listOf("run third"),Material.BOW,"third"){
                            accept(Material.GREEN_WOOL,"it in third as select button")
                            reject(Material.RED_WOOL,"it in third as reject button")
                        }
                    }
                }
                end(listOf("this is set at register last part"), 20,"example", "helpmessage", Material.GREEN_WOOL, "성공"){
                    //success do
                }
            }
        }
    }
}

