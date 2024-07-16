package klepto.placed.registry.block;

import klepto.placed.Placed;
import klepto.placed.registry.block.placed_blocks.GlowstoneWire;
import klepto.placed.registry.block.placed_blocks.SugarWire;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {


    public static final Block GLOWSTONE_WIRE = registerBlock("glowstone_wire", new GlowstoneWire(AbstractBlock.Settings.copy(Blocks.REDSTONE_WIRE).nonOpaque().luminance((state) -> 12)), false);

    public static final Block SUGAR_WIRE = registerBlock("sugar_wire", new SugarWire(AbstractBlock.Settings.copy(Blocks.REDSTONE_WIRE).nonOpaque()), false);




    
    private static Block registerBlock(String name, Block block, boolean registerBlockItem){
        Identifier id = Identifier.of(Placed.MOD_ID, name);
        //ItemGroupEvents.modifyEntriesEvent(ModItemGroup.SOAP).register(content -> {content.add(block);});
        if(registerBlockItem){
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
			Registry.register(Registries.ITEM, id, blockItem);
        }
		return Registry.register(Registries.BLOCK, id, block);
	}

    public static String registerModBlocks(){
        return "Registering BLOCKS for " + Placed.MOD_ID;
    }
}
