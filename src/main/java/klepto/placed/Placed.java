package klepto.placed;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import klepto.placed.handlers.event.UseBlockHandler;
import klepto.placed.registry.block.ModBlocks;
import klepto.placed.registry.item.ModItems;

public class Placed implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "placed";
	//public static final Identifier ID = Identifier.of(MOD_ID, "placed");
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		//ModSounds.registerModSounds();
		UseBlockCallback.EVENT.register(new UseBlockHandler());

		LOGGER.info(ModItems.registerModItems());
		LOGGER.info(ModBlocks.registerModBlocks());
		LOGGER.info(UseBlockHandler.registerHandler());
		
		//ModEffects.registerModEffects();

		LOGGER.info("Hello Fabric world!");
	}
}