package org.gang.debugcat.item

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import java.lang.StringBuilder


class RecipeBuilder {
    private val items = mutableListOf<String>()
    fun ArrayBuilder(): RecipeArrayBuilder {
        return RecipeArrayBuilder(items)
    }
}
class RecipeArrayBuilder(private val items : MutableList<String>){

    fun A(): RecipeArrayBuilder {
        items.add("A")
        return this
    }
    fun B(): RecipeArrayBuilder {
        items.add("B")
        return this
    }
    fun C(): RecipeArrayBuilder {
        items.add("C")
        return this
    }
    fun D(): RecipeArrayBuilder {
        items.add("D")
        return this
    }
    fun E(): RecipeArrayBuilder {
        items.add("E")
        return this
    }
    fun F(): RecipeArrayBuilder {
        items.add("F")
        return this
    }
    fun G(): RecipeArrayBuilder {
        items.add("G")
        return this
    }
    fun H(): RecipeArrayBuilder {
        items.add("H")
        return this
    }
    fun I(): RecipeArrayBuilder {
        items.add("I")
        return this
    }
    fun Empty(): RecipeArrayBuilder {
        items.add(" ")
        return this
    }
    fun materialBuilder(): RecipeMaterialBuilder {
        return RecipeMaterialBuilder(items)
    }
}
class RecipeMaterialBuilder(private val items: MutableList<String>){
    private val materials = mutableMapOf<String, Material?>()

    fun A(material: Material): RecipeMaterialBuilder {
        materials["A"] = material
        return this
    }

    fun B(material: Material): RecipeMaterialBuilder {
        materials["B"] = material
        return this
    }

    fun C(material: Material): RecipeMaterialBuilder {
        materials["C"] = material
        return this
    }

    fun D(material: Material): RecipeMaterialBuilder {
        materials["D"] = material
        return this
    }

    fun E(material: Material): RecipeMaterialBuilder {
        materials["E"] = material
        return this
    }

    fun F(material: Material): RecipeMaterialBuilder {
        materials["F"] = material
        return this
    }

    fun G(material: Material): RecipeMaterialBuilder {
        materials["G"] = material
        return this
    }

    fun H(material: Material): RecipeMaterialBuilder {
        materials["H"] = material
        return this
    }

    fun I(material: Material): RecipeMaterialBuilder {
        materials["I"] = material
        return this
    }
    fun getRecipe(key:String,result: ItemStack):ShapedRecipe{
        val customRecipe = ShapedRecipe(NamespacedKey.minecraft(key), result)
        val list = mutableListOf("","","")
        repeat(3){x->
            val sb = StringBuilder()
            repeat(3){y->
                sb.append(items[y+(x*3)])
            }
            list[x]= sb.toString()
        }
        customRecipe.shape(list[0],list[1],list[2])
        materials.forEach { s, material ->
            material?.let {
                customRecipe.setIngredient(s.toCharArray()[0],it)
            }
        }
        return customRecipe
    }
}


