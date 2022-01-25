package me.cael.ancientthaumaturgy.mixin;

import me.cael.ancientthaumaturgy.common.item.lexicon.ClientTickHandler;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow
    public abstract boolean isPaused();

    @Shadow
    private float pausedTickDelta;

    @Shadow
    public abstract float getTickDelta();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V"))
    private void onFrameStart(boolean tick, CallbackInfo ci) {
        ClientTickHandler.Companion.renderTick(isPaused() ? pausedTickDelta : getTickDelta());
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", shift = At.Shift.AFTER))
    private void onFrameEnd(boolean tick, CallbackInfo ci) {
        ClientTickHandler.Companion.calcDelta();
    }
}