package raeeeee.dotwarden.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import raeeeee.dotwarden.extensions.PlayerExtensions;

@Mixin(PlayerEntity.class)
public class PlayerMixin implements PlayerExtensions {
    @Unique
    private int dotwarden$powerLevel = 0;
    @Override
    public int dotwarden$getPowerLevel() {
        return dotwarden$powerLevel;
    }
    @Override
    public void dotwarden$setPowerLevel(int value) {
        this.dotwarden$powerLevel = value;
    }
    @Inject(method="writeCustomDataToNbt", at = @At("TAIL"))
    public void dotwarden$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("powerlevel",this.dotwarden$powerLevel);
    }
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void dotwarden$readCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.dotwarden$powerLevel = nbt.getInt("powerlevel");
    }
}
