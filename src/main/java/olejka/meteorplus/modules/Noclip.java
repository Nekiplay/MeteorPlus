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
	private void playerMoveEvent(PlayerMoveEvent event) {
		double s = event.movement.getY() - startY;
		event.movement.add(new Vec3d(0, s, 0));
	}
	@EventHandler
	private void onCollision(CollisionShapeEvent event) {
		if (event.type != CollisionShapeEvent.CollisionType.BLOCK || mc.player == null) return;
		BlockPos under = mc.player.getBlockPos().add(0, -1, 0);
		if (event.pos.equals(under)) return;
		event.shape = VoxelShapes.empty();
	}
}
