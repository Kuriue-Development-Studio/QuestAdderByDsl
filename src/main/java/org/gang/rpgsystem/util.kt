package org.gang.rpgsystem

import io.github.monun.heartbeat.coroutines.HeartbeatScope
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.frame.InvFrame
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.gang.debugcat.item.getHead
import org.gang.debugcat.item.metadataItem
import org.gang.rpgsystem.party.PartyInventorySystem
import taewookim.Party
import taewookim.datas.playerdatabar.BarSlotType
import taewookim.datas.playerdatabar.PlayerDataBar

inline val Player.isLeader : Boolean get() = RpgInventorySystem.ps.getParty(this).leader == this

 fun InvFrame.lineByListItem(num : Int, opener: Player, list : List<SlotItem>){
    val workList =if(opener.isLeader){
        list
    }else{
        list.filter {
            it.onlyOp
        }
    }
    val slotlist = workList.size.slot
    slotlist.forEachIndexed { index, i ->
        item(i,num,workList[index].item)
    }
}
 inline fun InvFrame.lineByOnClick(target: Player? = null, num : Int, opener: Player, list : List<SlotItem>){
    val workList =if(opener.isLeader){
        list
    }else{
        list.filter {
            it.onlyOp
        }
    }
    val slotList = workList.size.slot
    slotList.forEachIndexed { index, i ->
        slot(i, num){
            onClick{
                workList[index].onClick(it,target,opener, RpgInventorySystem.ps.getParty(opener))
            }
        }
    }
}
 fun getPlayerHead(p: Player, lead : Boolean) = metadataItem<SkullMeta>(
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
// get Inv of Members List
 fun getMemberInventory(p:Player): InvFrame {
    var partys = RpgInventorySystem.ps.getParty(p)
    return InvFX.frame(3, Component.text("${partys.leader.name}님의 파티")) {
        partys?.let {// if party is not null run code
            item(4, 0, getPlayerHead(partys.leader,true)
            )
            partys.players.forEachIndexed { index, player -> // 아이템 생성
                item(4 + ((if (index % 2 == 1) -1 else 1) * index), 1, getPlayerHead(player,false))
            }
            onClick{x,y,event->//any inventory slot click event
                if (event.whoClicked == it.leader){
                    val item = item(x,y)
                    if (item != null){
                        item.getHead?.let {
                            PartyInventorySystem.showPlayerOptionInventory(p, it, partys)
                        }
                    }
                }
            }
            lineByOnClick(num = 2, opener = p, list = PartyInventorySystem.MemberOptionItems)
            lineByListItem(num = 2, opener = p, list = PartyInventorySystem.MemberOptionItems)
        }
        HeartbeatScope().launch {
            while (true) {
                partys = RpgInventorySystem.ps.getParty(p)
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

val PlayerDataBar.getSlotTypes : MutableList<BarSlotType> get() {
    val clazz = this::class.java
    val types = mutableListOf<BarSlotType>()
    repeat(9){i->
        types[i] = clazz.declaredFields.filter {
            it.name.contains("slot$i")
        }.first().get(this) as BarSlotType
    }
    return types
}
val PlayerDataBar.consumedItems : HashMap<Int,ItemStack> get() {
    val clazz = this::class.java
    val types = this.getSlotTypes
    val items = hashMapOf<Int,ItemStack>()
    repeat(9){i->
        (clazz.declaredFields.filterIndexed { index, field ->
            field.name.contains("Consumeditem$i") && types[index] == BarSlotType.CONSUMED
        }.first().get(this) as ItemStack)?.let {
            items[i] =it
        }
    }
    return items
}
data class SlotItem(
    var onlyOp : Boolean = false,
    val item : ItemStack,
    val onClick : (event: InventoryClickEvent, target : Player?, openner : Player, party : Party) -> Unit
)
data class BarSlotItem(
    var slot: BarSlotType ,
    val item : ItemStack,
    val name : String
)
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