package io.github.Tors_0.dotwarden.extensions;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

public interface PlayerExtensions {
    int dotwarden$getPowerLevel();
    void dotwarden$setPowerLevel(int value);
    boolean dotwarden$hasSacrificed();
    void dotwarden$setSacrifice(boolean state);
}
