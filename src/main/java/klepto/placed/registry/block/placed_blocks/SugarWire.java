package klepto.placed.registry.block.placed_blocks;

import klepto.placed.handlers.wire_util.WireHandler;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class SugarWire extends WireHandler{

    public SugarWire(Settings settings) {
        super(settings);
        this.emitsParticles = false;
		this.color = Vec3d.unpackRgb(0xFFFF00).toVector3f();
        this.stackToBeDropped = Items.SUGAR.getDefaultStack();

    }

    
}
