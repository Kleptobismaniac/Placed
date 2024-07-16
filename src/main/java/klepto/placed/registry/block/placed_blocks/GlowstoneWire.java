package klepto.placed.registry.block.placed_blocks;


import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import klepto.placed.handlers.wire_util.WireHandler;

public class GlowstoneWire extends WireHandler {
	public GlowstoneWire(Settings settings) {
		super(settings);
		this.emitsParticles = false;
		this.color = Vec3d.unpackRgb(0xFFFF00).toVector3f();
		this.stackToBeDropped = Items.GLOWSTONE_DUST.getDefaultStack();
	}

}