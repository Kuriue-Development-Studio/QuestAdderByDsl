package org.gang.debugcat.item

import net.minecraft.world.item.*
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.material.MaterialData

inline fun item(
    material: Material,
    amount: Int = 1,
    data: Short = 0,
    meta: ItemMeta.() -> Unit = {},
): ItemStack = ItemStack(material, amount, data).meta(meta)

inline fun <reified T : ItemMeta> metadataItem(
    material: Material,
    amount: Int = 1,
    data: Short = 0,
    meta: T.() -> Unit,
): ItemStack = ItemStack(material, amount, data).meta(meta)

inline fun <reified T : ItemMeta> ItemStack.meta(
    block: T.() -> Unit,
): ItemStack = apply {
    itemMeta = (itemMeta as? T)?.apply(block) ?: itemMeta
}

public fun ItemStack.displayName(displayName: String?): ItemStack = meta<ItemMeta> {
    this.setDisplayName(displayName)
}

public fun ItemStack.lore(lore: List<String>): ItemStack = meta<ItemMeta> {
    this.lore = lore
}

public inline fun Material.asItemStack(
    amount: Int = 1,
    data: Short = 0,
    meta: ItemMeta.() -> Unit = {},
): ItemStack = item(this, amount, data, meta)

public fun Material.asMaterialData(
    data: Byte = 0,
): MaterialData = MaterialData(this, data)

public fun MaterialData.toItemStack(
    amount: Int = 1,
    meta: ItemMeta.() -> Unit = {},
): ItemStack = toItemStack(amount).meta(meta)

/**
 * get head from base64
 */
inline val ItemStack.getHead: Player? get() = (this.itemMeta as SkullMeta).owningPlayer?.player
 inline val ItemStack.isPickaxe: Boolean get() = this.toNmsItem?.item is PickaxeItem
 inline val ItemStack.isSword: Boolean get() = this.toNmsItem?.item is SwordItem
 inline val ItemStack.isAxe: Boolean get() = this.toNmsItem?.item is AxeItem
 inline val ItemStack.isHoe: Boolean get() = this.toNmsItem?.item is HoeItem
 inline val ItemStack.isBow: Boolean get() = this.toNmsItem?.item is BowItem
 inline val ItemStack.isShield: Boolean get() = this.toNmsItem?.item is ShieldItem
 inline val ItemStack.isArmor: Boolean get() = this.toNmsItem?.item is ArmorItem
 inline val ItemStack.getArmorMaterial: ArmorItem? get() {
    if (this.isArmor){
        return (this.toNmsItem?.item as ArmorItem)
    }
    return null
}
public inline val ItemStack.toNmsItem: net.minecraft.world.item.ItemStack? get() = CraftItemStack.asNMSCopy(this)

