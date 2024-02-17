package org.gang.rpgsystem.custominv

import io.github.monun.invfx.InvFX.frame
import io.github.monun.invfx.openFrame
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.PotionMeta
import org.gang.debugcat.item.item
import org.gang.debugcat.item.metadataItem
import org.gang.rpgsystem.RpgInventorySystem.Companion.pi

object CustomInventorySystem {
    fun CustomInv(opener:Player){
        val data = pi.getPlayerData(opener)
        val frame = frame(6, Component.text(opener.name)){
            repeat(9){x->
                repeat(6){y->
                    item(x,y, org.gang.debugcat.item.item(Material.GRAY_STAINED_GLASS_PANE){
                        setDisplayName("")
                    })
                }
            }
            slot(4,0){
                item = item(Material.DIAMOND_HELMET)
            }
            slot(4,1){
                item = item(Material.DIAMOND_CHESTPLATE)
            }
            slot(4,2){
                item = item(Material.DIAMOND_LEGGINGS)
            }
            slot(4,3){
                item = item(Material.DIAMOND_BOOTS)
            }
            slot(1,1){
                item = item(Material.DIAMOND_SWORD){
                }
                onClick {
                    opener.openInventory(data.playerInventory.inventory_Weapon)
                }
            }
            slot(1,3){
                item = metadataItem<PotionMeta>(Material.POTION){
                    setDisplayName(data.playerLevel.level.toString())
                    color = Color.RED
                }
                onClick {
                    opener.openInventory(data.playerInventory.inventory_Consumed)
                }
            }
            slot(7,1){
                item = item(Material.CHEST){
                    setDisplayName("기타 아이템")
                }
                onClick {
                    opener.openInventory(data.playerInventory.inventory_Etc)
                }
            }
            slot(7,3){
                item = item(Material.FILLED_MAP){
                    setDisplayName("지도")
                }
            }
            onClick { x, y, event ->
                if (x == 4 && y in 0..3){
                    opener.openInventory(data.playerInventory.inventory_Armor)
                }
            }
        }
        opener.openFrame(frame)
    }
}