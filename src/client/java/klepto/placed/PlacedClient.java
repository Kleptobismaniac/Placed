package klepto.placed;

import klepto.placed.registry.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;

public class PlacedClient implements ClientModInitializer {
	@SuppressWarnings("deprecation")
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GLOWSTONE_WIRE, RenderLayer.getCutout());
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0xFFFF00, ModBlocks.GLOWSTONE_WIRE);

		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUGAR_WIRE, RenderLayer.getCutout());
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0xFFFFFF, ModBlocks.SUGAR_WIRE);

		//BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GUNPOWDER_WIRE, RenderLayer.getCutout());

	}
}