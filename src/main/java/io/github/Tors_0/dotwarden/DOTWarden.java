package io.github.Tors_0.dotwarden;

import io.github.Tors_0.dotwarden.extensions.PlayerExtensions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.entity_events.api.ServerPlayerEntityCopyCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.Tors_0.dotwarden.registry.ModItems;

public class DOTWarden implements ModInitializer {
	public static final String ID = "dotwarden";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Now initializing {} version {}", mod.metadata().name(), mod.metadata().version());

		ModItems.init();

        ServerPlayerEntityCopyCallback.EVENT.register((copy, original, wasDeath) -> {
            if (copy instanceof PlayerExtensions playerNew && original instanceof PlayerExtensions playerOld) {
                playerNew.dotwarden$setPowerLevel(playerOld.dotwarden$getPowerLevel());
                playerNew.dotwarden$setSacrifice(playerOld.dotwarden$hasSacrificed());
            }
        });
	}
}
