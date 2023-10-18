package me.cael.ancientthaumaturgy.mixin;

import me.cael.ancientthaumaturgy.common.item.lexicon.ClientTickHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Shadow
    private boolean paused;

    @Shadow
    private float pausedTickDelta;

    @Final
    @Shadow
    private RenderTickCounter renderTickCounter;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V"))
    private void onFrameStart(boolean tick, CallbackInfo ci) {
        ClientTickHandler.Companion.renderTick(paused ? pausedTickDelta : renderTickCounter.tickDelta);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", shift = At.Shift.AFTER))
    private void onFrameEnd(boolean tick, CallbackInfo ci) {
        ClientTickHandler.Companion.calcDelta();
    }
}