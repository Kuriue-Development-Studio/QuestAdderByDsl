package org.gang.rpgsystem.quest

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.Plugin
import org.gang.rpgsystem.QuestData
import org.gang.rpgsystem.QuestAdderDsl.Companion.qm
import taewookim.Core
import taewookim.QuestAdder
import taewookim.chatdata.QuestAcceptChatData
import taewookim.chatdata.QuestChatData
import taewookim.chatdata.QuestEndChatData
import taewookim.chatdata.QuestRejectChatData
import taewookim.clearcondition.ClearCondition
import taewookim.clearcondition.ClearConditionMonster
import taewookim.clearresult.ClearResult
import taewookim.datas.PlayerDataQuest
import java.util.function.Predicate
import kotlin.reflect.KClass


fun Plugin.kQuest(init: KQuestScope.() -> Unit){
    KQuestScope(this).init()
}

class KQuestScope internal constructor(private val plugin: Plugin) {

    fun register( name: String,texts: List<String>,material: Material, lambda: QuestNode.() -> Unit){
        val qcd = QuestChatData(name)
        qcd.messagelist = texts
        val cclist: ArrayList<ClearCondition?> = ArrayList()
        val qnode = QuestNode(name,qcd,qcd,cclist,plugin,texts[0],material)
        qnode.lambda()
        qnode.sort()
    }

}
class QuestNode(
    val name: String,
    private val prevQuestChatData: QuestChatData,
    private val LootNode: QuestChatData,
    val cc: ArrayList<ClearCondition?>,
    val plugin: Plugin,
    val item : String,
    val material: Material,
) {
    private lateinit var qcd : QuestChatData
    private var list = mutableListOf<ChatDataSystem>()
    fun text(chat : List<String>, material: Material, message: String, questNode: QuestNode.() -> Unit){
        qcd = QuestChatData(name)
        qcd.messagelist = chat
        list.add(ChatDataSystem(qcd,message,material))
        val qnode = QuestNode(name, qcd,LootNode, cc, plugin,item, material)
        qnode.questNode()
        qnode.sort()
    }
    fun sort(){
        val slotlist = list.size.slot
        slotlist.forEachIndexed { index, i ->
            val chatData = list[index]
            prevQuestChatData.addChatData(chatData.questChatData,chatData.message,chatData.material,i)
        }
    }

    fun accept(material: Material,message: String){
        qcd = QuestAcceptChatData(name)
        list.add(ChatDataSystem(qcd,message,material))

    }
    fun reject(material: Material,message: String){
        val qcd = QuestRejectChatData(name)
        list.add(ChatDataSystem(qcd,message,material))
    }

    fun end(chat : List<String>,
            objective : Int,
            npcName: String,
            helpMessage:String, material:
            Material, message:
            String, questNode: (Player) -> Unit){
        val qq = QuestChatData(name)
        qcd = QuestEndChatData(name)
        val c = ClearConditionMonster()
        c.monsterid = "$name thisisnoMonsterheyheyheyhyet"
        c.maxmonster = objective
        c.pre = Predicate { player -> true }
        cc.add(c)
        qq.messagelist = chat
        qq.addChatData(qcd,message,material,4)
        val pre: (Player) -> Boolean = { true }
        val cr: ClearResult = object : ClearResult() {
            override fun clear(player: Player) {
                questNode(player)
            }
        }
        val questAdder = Bukkit.getPluginManager().getPlugin("RPGQuestAdder") as QuestAdder
        questAdder.setNPC(name, npcName)
        questAdder.addQuest(name,helpMessage,cc,LootNode,pre,cr,qq)
        qm.addQuest(QuestData(material,item,name,helpMessage,objective))
    }

}
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
data class ChatDataSystem(
    val questChatData: QuestChatData,
    val message: String,
    val material: Material
)

fun AddingQuest(p: Player, id: String, amount: Int = 1): Int {
    val pd = Core.pi.getPlayerData(p)
    val pdq: PlayerDataQuest = pd.getPlayerQuest()
    var index = 0
    if (pdq.skillmonster["$id thisisnoMonsterheyheyheyhyet"] != null) {
        for (cc in pdq.skillmonster["$id thisisnoMonsterheyheyheyhyet"]!!) {
            cc.catchmonster+=amount
            index = cc.catchmonster
        }
    }
    return index
}
public inline fun <reified T : Event> KListener<*>.event(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline block: T.() -> Unit,
): Unit = event(plugin, priority, ignoreCancelled, block)

public fun <T : Event> KListener<*>.event(
    type: KClass<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    block: T.() -> Unit,
): Unit = event(plugin, type, priority, ignoreCancelled, block)

public inline fun <reified T : Event> Listener.event(
    plugin: Plugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline block: T.() -> Unit,
) {
    event<T>(plugin, T::class, priority, ignoreCancelled, block)
}

public fun <T : Event> Listener.event(
    plugin: Plugin,
    type: KClass<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    block: T.() -> Unit,
) {
    Bukkit.getServer().pluginManager.registerEvent(
        type.java,
        this,
        priority,
        { _, event ->
            if (type.isInstance(event)) {
                (event as? T)?.block()
            }
        },
        plugin,
        ignoreCancelled,
    )
}

public inline fun WithPlugin<*>.events(block: KListener<*>.() -> Unit): SimpleKListener = plugin.events(block)
public inline fun Plugin.events(block: KListener<*>.() -> Unit): SimpleKListener = SimpleKListener(this).apply(block)

public fun Listener.registerEvents(plugin: Plugin): Unit = plugin.server.pluginManager.registerEvents(this, plugin)

public fun Listener.unregisterListener(): Unit = HandlerList.unregisterAll(this)

public fun Event.callEvent(): Unit = Bukkit.getServer().pluginManager.callEvent(this)

public val PlayerMoveEvent.displaced: Boolean
    get() = this.from.x != this.to?.x || this.from.y != this.to?.y || this.from.z != this.to?.z

// TODO: remove KListener and move to Context Receivers
public interface KListener<T : Plugin> : Listener, WithPlugin<T>

public class SimpleKListener(override val plugin: Plugin) : KListener<Plugin>

public interface WithPlugin<T : Plugin> { public val plugin: T }