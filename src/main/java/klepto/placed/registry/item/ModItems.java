package klepto.placed.registry.item;

import klepto.placed.Placed;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {



   


    //TODO Do something with "chunk of sponge"


    private static Item registerItem(String name, Item item){
        Identifier id = Identifier.of(Placed.MOD_ID, name);

        //ItemGroupEvents.modifyEntriesEvent(ModItemGroup.SOAP).register(content -> {content.add(item);});
        return Registry.register(Registries.ITEM, id, item);
	}

    public static String registerModItems(){
        return "Registering ITEMS for " + Placed.MOD_ID;
    }   

}
