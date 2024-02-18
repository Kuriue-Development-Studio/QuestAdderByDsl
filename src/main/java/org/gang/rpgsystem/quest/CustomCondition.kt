package org.gang.rpgsystem.quest

import org.bukkit.entity.Player
import taewookim.clearcondition.ClearCondition

class CustomCondition(var objective : Int,var name:String) : ClearCondition() {
    var index = 0

    override fun isClear(p: Player?): Boolean {
        return super.isClear(p) && this.objective <= this.index
    }

    override fun save(d: MutableMap<String?, Any?>) {
        d[name] = index
    }

    override fun load(d: Map<String, Any?>?) {
        if (d != null) {
            val keys = d.keys
            if (keys.contains(name)) {
                this.index = (d[name] as Int?)!!
            }
        }
    }
}
