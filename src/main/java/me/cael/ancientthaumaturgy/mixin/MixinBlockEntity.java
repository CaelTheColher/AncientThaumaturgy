package me.cael.ancientthaumaturgy.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.cael.ancientthaumaturgy.mixed.MixedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public class MixinBlockEntity implements MixedBlockEntity {

    @Shadow @Final protected BlockPos pos;
    @Unique
    public int ancientthaumaturgy_portableHoleTimer = 0;

    @Inject(method = "readNbt", at = @At("HEAD"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        ancientthaumaturgy_portableHoleTimer = nbt.getInt("PortableHoleTimer");
    }

    @ModifyReturnValue(method = "createNbt", at = @At("RETURN"))
    public NbtCompound createNbt(NbtCompound original) {
        original.putInt("PortableHoleTimer", ancientthaumaturgy_portableHoleTimer);
        return original;
    }

    @Override
    public int ancientThaumaturgy_getPortableHoleTimer() {
        return ancientthaumaturgy_portableHoleTimer;
    }

    @Override
    public void ancientThaumaturgy_setPortableHoleTimer(int portableHoleTimer) {
        this.ancientthaumaturgy_portableHoleTimer = portableHoleTimer;
    }
}
