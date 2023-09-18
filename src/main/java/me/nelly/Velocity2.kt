package me.nelly

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.block.BlockSlab
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.EnumFacing
import net.minecraft.util.BlockPos

@ModuleInfo(name = "Velocity2", description = "By nelly", category = ModuleCategory.COMBAT)
class Velocity2 : Module() {
    private val OnlyMove = BoolValue("OnlyMove", false)
    private val OnlyGround = BoolValue("OnlyGround", false)
    private var packets = 0

    private fun isPlayerOnSlab(player: EntityPlayer): Boolean {
        val playerPos = BlockPos(player.posX, player.posY, player.posZ)

        val block = player.entityWorld.getBlockState(playerPos).block
        val boundingBox = player.entityBoundingBox

        return block is BlockSlab && player.posY - playerPos.y <= boundingBox.minY + 0.1
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if ((OnlyMove.get() && !MovementUtils.isMoving()) || (OnlyGround.get() && !mc.thePlayer!!.onGround)) {
            return
        }

        val packet = event.packet

        if (packets > 0) {
            packets--
            return
        }

        if (packet is S08PacketPlayerPosLook) {
            packets = 10
        }

        if (packet is S12PacketEntityVelocity && mc.thePlayer!!.hurtTime > 0) {
            event.cancelEvent()
            ClientUtils.displayChatMessage("Velocity2-Cancel S12Packet")
        }
    }
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if ((OnlyMove.get() && !MovementUtils.isMoving()) || (OnlyGround.get() && !mc.thePlayer!!.onGround)) {
            return
        }
        if (event.eventState == EventState.PRE && !mc.playerController.isHittingBlock && mc.thePlayer.hurtTime > 0 && !isPlayerOnSlab(mc.thePlayer)) {
            val blockPos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
            mc.netHandler.networkManager!!.sendPacket(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    blockPos,
                    EnumFacing.NORTH
                )
            )
            ClientUtils.displayChatMessage("Velocity2-Send C07Packet")
        }
    }

    override val tag: String?
        get() = "Hyt"
}