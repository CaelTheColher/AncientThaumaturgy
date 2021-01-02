/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package me.cael.ancientthaumaturgy.mixin;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HeldItemRenderer.class)
public interface AccessorFirstPersonRenderer {
    @Invoker("applyEquipOffset")
    void ancientthaumaturgy_equipOffset(MatrixStack ms, Arm side, float equip);

    @Invoker("applySwingOffset")
    void ancientthaumaturgy_swingOffset(MatrixStack ms, Arm side, float swing);
}