package raeeeee.dotwarden;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.entity_events.api.ServerPlayerEntityCopyCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raeeeee.dotwarden.extensions.PlayerExtensions;
import raeeeee.dotwarden.registry.ModItems;

import java.net.http.WebSocket;
import java.util.EventListener;
import java.util.HashMap;

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
                ((PlayerExtensions) copy).dotwarden$setPowerLevel(((PlayerExtensions) original).dotwarden$getPowerLevel());
            }
        });
	}
}
