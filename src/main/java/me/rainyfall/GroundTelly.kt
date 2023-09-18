package me.rainyfall

import net.ccbluex.liquidbounce.LiquidBounce.moduleManager
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.TickEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import java.util.*
import kotlin.jvm.internal.Intrinsics

@ModuleInfo(name = "GroundTelly", description = "GroundTelly", category = ModuleCategory.WORLD)
class GroundTelly : Module() {
    private val scaffoldModule =
        ListValue("ScaffoldModule", arrayOf("Scaffold"), "Scaffold")
    private val autoJumpValue = BoolValue("AutoJump", false)
    private val autoJumpHelper =
        ListValue("JumpHelper", arrayOf("Parkour"), "Parkour") { autoJumpValue.get() }
    private val autoJumpMode = ListValue(
        "AutoJumpMode", arrayOf(
            "MCInstanceJump",
            "MCInstance2Jump",
            "ClientMotionY"
        ), "MCInstanceJump"
    ) { autoJumpValue.get() }
    private val eventTargetSelector = ListValue(
        "EventSelect", arrayOf(
            "onUpdate",
            "onTick"
        ), "onUpdate"
    )

    private val noBobValue = BoolValue("NoBob", false)

    private val autoPitchValue = BoolValue("setBestPitch", false)
    private val alwaysPitchValue = BoolValue("setPitch-onUpdate", false) { autoPitchValue.get() }
    private val customPitchValue = FloatValue("CustomPitch",26.5F,0F,90F)
    private val autoYawValue = ListValue("setYawMode", arrayOf("None", "onEnable", "onUpdate"), "None")
    private val disableAllOnEnable = BoolValue("Enable-DisableAll", false)
    private val disableAllOnDisable = BoolValue("Disable-DisableAll", false)

    override fun onEnable() {
        if (autoPitchValue.get()) {
            mc.thePlayer!!.rotationPitch = customPitchValue.get()
        }
        if (autoYawValue.get().equals("onEnable")) setYaw()

        if (disableAllOnEnable.get()) disableAll()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer!!

        if (autoPitchValue.get() && alwaysPitchValue.get()) {
            mc.thePlayer!!.rotationPitch = customPitchValue.get()
        }
        if (autoYawValue.get().equals("onUpdate")) setYaw()
        if (noBobValue.get()) mc.thePlayer!!.distanceWalkedModified = 0f
        if (!thePlayer.isSneaking) {
            val thePlayer2 = mc.thePlayer
            if (thePlayer2 == null) {
                Intrinsics.throwNpe()
            }
            if (thePlayer2!!.onGround) {
                scaffoldChange(false)
            } else {
                scaffoldChange(true)
            }
        }
        if (autoJumpValue.get() && eventTargetSelector.get().equals("onUpdate", true)) tryJump()
    }

    private fun jump() {
        when (autoJumpMode.get().lowercase(Locale.getDefault())) {
            "mcinstancejump" -> mc.thePlayer!!.jump()
            "mcinstance2jump" -> mc.thePlayer!!.jump()
            "clientmotiony" -> mc.thePlayer!!.motionY = 0.42
        }
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        if (autoJumpValue.get() && eventTargetSelector.get().equals("onTick", true)) tryJump()
    }

    @EventTarget
    override fun onDisable() {
        scaffoldChange(false)
        if (disableAllOnDisable.get()) disableAll()
    }

    private fun scaffoldChange(state: Boolean) {
        when (scaffoldModule.get().lowercase(Locale.getDefault())) {
            "scaffold" -> moduleManager.getModule(Scaffold::class.java)!!.state = state
        }
    }

    private fun tryJump() {
        val thePlayer = mc.thePlayer!!
        when (autoJumpHelper.get().lowercase(Locale.getDefault())) {
            "parkour" -> if (MovementUtils.isMoving() && thePlayer.onGround && !thePlayer.isSneaking && !mc.gameSettings.keyBindSneak.isKeyDown && !mc.gameSettings.keyBindJump.isKeyDown &&
                mc.theWorld!!.getCollidingBoundingBoxes(
                    thePlayer, thePlayer.entityBoundingBox
                        .offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)
                ).isEmpty()
            ) {
                jump()
            }


            "test" -> {
                if (thePlayer.onGround && MovementUtils.isMoving() && thePlayer.isSprinting) {
                    jump()
                }
            }
        }
    }

    private fun disableAll() {
        moduleManager.getModule(Scaffold::class.java)!!.state = false
    }

    private fun setYaw() {
        val thePlayer = mc.thePlayer!!
        if (autoYawValue.get().lowercase(Locale.getDefault()).equals("none")) return
        val x = java.lang.Double.valueOf(thePlayer.motionX)
        val y = java.lang.Double.valueOf(thePlayer.motionZ)
        if (mc.gameSettings.keyBindForward.isKeyDown) {
            if (y != null &&
                y.toDouble() > 0.1
            ) {
                thePlayer.rotationYaw = 0.0f
            }
            if (y != null &&
                y.toDouble() < -0.1
            ) {
                thePlayer.rotationYaw = 180.0f
            }
            if (x != null &&
                x.toDouble() > 0.1
            ) {
                thePlayer.rotationYaw = -90.0f
            }
            if (x != null &&
                x.toDouble() < -0.1
            ) {
                thePlayer.rotationYaw = 90.0f
            }
        }

    }
}
