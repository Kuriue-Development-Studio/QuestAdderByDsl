package org.gang.rpgsystem

import org.bukkit.entity.Player

inline val Player.isLeader : Boolean get() = RpgInventorySystem.ps.getParty(this).leader == this