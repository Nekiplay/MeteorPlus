package olejka.meteorplus.modules.jesus.modes;

import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import olejka.meteorplus.modules.jesus.JesusMode;
import olejka.meteorplus.modules.jesus.JesusModes;

public class MatrixZoom2 extends JesusMode {
	public MatrixZoom2() {
		super(JesusModes.MatrixZoom2);
	}

	private final float range = 0.005f;
	private int tick = 0;

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		float yaw = mc.player.getYaw();
		Vec3d forward = Vec3d.fromPolar(0, yaw);
		Vec3d right = Vec3d.fromPolar(0, yaw + 90);

		double velX = 0;
		double velZ = 0;
		double s = 0.5;
		double speedValue = settings.speed.get();

		if (mc.options.forwardKey.isPressed()) {
			velX += forward.x * s * speedValue;
			velZ += forward.z * s * speedValue;
		}
		if (mc.options.backKey.isPressed()) {
			velX -= forward.x * s * speedValue;
			velZ -= forward.z * s * speedValue;
		}

		if (mc.options.rightKey.isPressed()) {
			velX += right.x * s * speedValue;
			velZ += right.z * s * speedValue;
		}
		if (mc.options.leftKey.isPressed()) {
			velX -= right.x * s * speedValue;
			velZ -= right.z * s * speedValue;
		}
		if (mc.world.getBlockState(new BlockPos((int) mc.player.getPos().x, (int) (mc.player.getPos().y + range), (int) mc.player.getPos().z)).getBlock() == Blocks.WATER && !mc.player.horizontalCollision) {
			if (tick == 0) {
				((IVec3d) mc.player.getVelocity()).set(velX, 0.030091, velZ);
			}
			else if (tick == 1) {
				((IVec3d) mc.player.getVelocity()).set(velX, -0.030091, velZ);
			}
		}
	}
}
