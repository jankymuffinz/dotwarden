package raeeeee.dotwarden.extensions;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

public interface PlayerExtensions {
    int dotwarden$getPowerLevel();

    void dotwarden$setPowerLevel(int value);
}
