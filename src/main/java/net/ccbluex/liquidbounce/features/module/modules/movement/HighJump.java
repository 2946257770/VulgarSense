/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.JumpEvent;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.block.BlockPane;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "HighJump", spacedName = "High Jump", description = "Allows you to jump higher.", category = ModuleCategory.MOVEMENT)
public class HighJump extends Module {

    private final FloatValue heightValue = new FloatValue("Height", 2F, 1.1F, 10F, "m");
    private final ListValue modeValue = new ListValue("Mode", new String[] {"Vanilla","Grim", "Damage", "AACv3", "DAC" ,"Mineplex", "MatrixWater"}, "Vanilla");
    private final BoolValue glassValue = new BoolValue("OnlyGlassPane", false);

    public int tick;

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if(glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        switch(modeValue.get().toLowerCase()) {
            case "damage":
                if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround)
                    mc.thePlayer.motionY += 0.42F * heightValue.get();
                break;
            case "aacv3":
                if (!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.059D;
                break;
            case "dac":
                if (!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.049999;
                break;
            case "mineplex":
                if (!mc.thePlayer.onGround) MovementUtils.strafe(0.35F);
                break;
            case "grim":
                if (!mc.thePlayer.onGround) mc.thePlayer.jump();
                break;
            case "matrixwater":
                if (mc.thePlayer.isInWater()) {
                    if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ)).getBlock() == Block.getBlockById(9)) {
                        mc.thePlayer.motionY = 0.18;
                    } else if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock() == Block.getBlockById(9)) {
                        mc.thePlayer.motionY = heightValue.get();
                        mc.thePlayer.onGround = true;
                    }
                }
                break;
        }
    }

    @EventTarget
    public void onMove(final MoveEvent event) {
        if (glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        if (!mc.thePlayer.onGround) {
            if ("mineplex".equals(modeValue.get().toLowerCase())) {
                mc.thePlayer.motionY += mc.thePlayer.fallDistance == 0 ? 0.0499D : 0.05D;
            }
        }

        if ("grim".equals(modeValue.get().toLowerCase())) {
            if (mc.thePlayer.isAirBorne) {
                mc.thePlayer.setPositionAndRotation(
                        mc.thePlayer.posX + 1000,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        mc.thePlayer.rotationYaw,
                        mc.thePlayer.rotationPitch
                );
            } else {
                mc.thePlayer.setPositionAndRotation(
                        mc.thePlayer.posX - 1000,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        mc.thePlayer.rotationYaw,
                        mc.thePlayer.rotationPitch
                );
            }
        }
    }

    @EventTarget
    public void onJump(final JumpEvent event) {
        if(glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        switch(modeValue.get().toLowerCase()) {
            case "vanilla":
                event.setMotion(event.getMotion() * heightValue.get());
                break;
            case "mineplex":
                event.setMotion(0.47F);
                break;
        }
    }


    @Override
    public String getTag() {
        return modeValue.get();
    }
}
