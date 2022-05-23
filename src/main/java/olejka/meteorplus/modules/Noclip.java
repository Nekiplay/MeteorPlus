package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import olejka.meteorplus.MeteorPlus;

public class Noclip extends Module {
	public Noclip() {
		super(MeteorPlus.CATEGORY, "Noclip", "Noclip.");
	}
	private double startY = 0;
	@Override
	public void onActivate() {
		startY = mc.player.getY();
	}

	@EventHandler
	private void onCollision(CollisionShapeEvent event) {
		if (event.type != CollisionShapeEvent.CollisionType.BLOCK || mc.player == null) return;
		if (event.pos.getY() >= mc.player.getPos().y ) {
			event.shape = VoxelShapes.empty();
		}
	}
}
