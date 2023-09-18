/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.utils.ClientUtils

import net.minecraft.util.ResourceLocation
import java.io.File

@ModuleInfo(name = "Cape", description = "LiquidBounce+ capes.", category = ModuleCategory.RENDER)
class Cape : Module() {

    val styleValue = ListValue("Style", arrayOf(            "Dark",
        "Astolfo",
        "Azrael",
        "Light",
        "Special1",
        "Special2",
        "BiliBili",
        "JiaRan",
        "Paimon",
        "lunar",
        "NekoCat",
        "NekoCat2",
        "NekoCat3",
        "NekoCat4",
        "Arona",
        "Mika1",
        "Mika2",
        "Planetarium",
        "Rise6",
        "Novoline",
        "MiaSakura"), "Dark")

    val movingModeValue = ListValue("MovingMode", arrayOf("Smooth", "Vanilla"), "Smooth")

    private val capeCache = hashMapOf<String, CapeStyle>()

    fun getCapeLocation(value: String): ResourceLocation {
        if (capeCache[value.toUpperCase()] == null) {
            try {
                capeCache[value.toUpperCase()] = CapeStyle.valueOf(value.toUpperCase())
            } catch (e: Exception) {
                capeCache[value.toUpperCase()] = CapeStyle.ASTOLFO
            }
        }
        return capeCache[value.toUpperCase()]!!.location
    }

    enum class CapeStyle(val location: ResourceLocation) {
        DARK(ResourceLocation("vulgarsense/capes/dark.png")),
        ASTOLFO(ResourceLocation("vulgarsense/capes/astolfo.png")),
        LIGHT(ResourceLocation("vulgarsense/capes/light.png")),
        AZRAEL(ResourceLocation("vulgarsense/capes/azrael.png")),
        SPECIAL1(ResourceLocation("vulgarsense/capes/special1.png")),
        SPECIAL2(ResourceLocation("vulgarsense/capes/special2.png")),
        BILIBILI(ResourceLocation("vulgarsense/capes/bilibili.png")),
        JIARAN(ResourceLocation("vulgarsense/capes/jiaran.png")),
        PAIMON(ResourceLocation("vulgarsense/capes/paimon.png")),
        LUNAR(ResourceLocation("vulgarsense/capes/lunar.png")),
        NEKOCAT(ResourceLocation("vulgarsense/capes/nekocat.png")),
        NEKOCAT2(ResourceLocation("vulgarsense/capes/nekocat2.png")),
        NEKOCAT3(ResourceLocation("vulgarsense/capes/nekocat3.png")),
        NEKOCAT4(ResourceLocation("vulgarsense/capes/nekocat4.png")),
        RISE6(ResourceLocation("vulgarsense/capes/rise6.png")),
        MIASAKURA(ResourceLocation("vulgarsense/capes/miasakurajima.png")),
        NOVOLINE(ResourceLocation("vulgarsense/capes/novoline.png")),
        ARONA(ResourceLocation("vulgarsense/capes/arona.png")),
        MIKA1(ResourceLocation("vulgarsense/capes/mika1.png")),
        MIKA2(ResourceLocation("vulgarsense/capes/mika2.png")),
        PLANETARIUM(ResourceLocation("vulgarsense/capes/planetarium.png")),
    }

    override val tag: String
        get() = styleValue.get()

}