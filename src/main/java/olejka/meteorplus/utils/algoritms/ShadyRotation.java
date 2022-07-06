package olejka.meteorplus.utils.algoritms;

import meteordevelopment.meteorclient.Main;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.misc.Vec3;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ShadyRotation {
	private static float pitchDifference;
	public static float yawDifference;
	private static int ticks = -1;
	private static int tickCounter = 0;
	private static Runnable callback = null;

	public static boolean running = false;

	public static class Rotation {
		public float pitch;
		public float yaw;

		public Rotation(float pitch, float yaw) {
			this.pitch = pitch;
			this.yaw = yaw;
		}
	}


	private static double wrapAngleTo180(double angle) {
		return angle - Math.floor(angle / 360 + 0.5) * 360;
	}

	private static float wrapAngleTo180(float angle) {
		return (float) (angle - Math.floor(angle / 360 + 0.5) * 360);
	}

	public static Rotation getRotationToBlock(BlockPos block) {
		double diffX = block.getX() - mc.player.getPos().x + 0.5;
		double diffY = block.getY() - mc.player.getPos().y + 0.5 - mc.player.getEyeY();
		double diffZ = block.getZ() - mc.player.getPos().z + 0.5;
		double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float pitch = (float) -Math.atan2(dist, diffY);
		float yaw = (float) Math.atan2(diffZ, diffX);
		pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90)*-1);
		yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

		return new Rotation(pitch, yaw);
	}

	public static Rotation getRotationToEntity(Entity entity) {
		double diffX = entity.getPos().x - mc.player.getPos().x;
		double diffY = entity.getPos().y + entity.getEyePos().y - mc.player.getPos().y - mc.player.getEyeY();
		double diffZ = entity.getPos().z - mc.player.getPos().z;
		double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float pitch = (float) -Math.atan2(dist, diffY);
		float yaw = (float) Math.atan2(diffZ, diffX);
		pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90)*-1);
		yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

		return new Rotation(pitch, yaw);
	}

	public static Rotation vec3ToRotation(Vec3 vec) {
		double diffX = vec.x - mc.player.getPos().x;
		double diffY = vec.y - mc.player.getPos().y - mc.player.getEyeY();
		double diffZ = vec.z - mc.player.getPos().z;
		double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float pitch = (float) -Math.atan2(dist, diffY);
		float yaw = (float) Math.atan2(diffZ, diffX);
		pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90)*-1);
		yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

		return new Rotation(pitch, yaw);
	}
	public static void smoothLook(Rotation rotation, int ticks, Runnable callback) {
		if(ticks == 0) {
			look(rotation);
			callback.run();
			return;
		}

		ShadyRotation.callback = callback;

		pitchDifference = wrapAngleTo180(rotation.pitch - mc.player.getPitch());
		yawDifference = wrapAngleTo180(rotation.yaw - mc.player.getYaw());

		ShadyRotation.ticks = ticks * 20;
		ShadyRotation.tickCounter = 0;
	}

	public static void smartLook(Rotation rotation, int ticksPer180, Runnable callback) {
		float rotationDifference = Math.max(
			Math.abs(rotation.pitch - mc.player.getPitch()),
			Math.abs(rotation.yaw - mc.player.getYaw())
		);
		smoothLook(rotation, (int) (rotationDifference / 180 * ticksPer180), callback);
	}

	public static void look(Rotation rotation) {
		mc.player.setPitch(rotation.pitch);
		mc.player.setYaw(rotation.yaw);
	}

	public void Init() {
		MeteorClient.EVENT_BUS.subscribe(this);
	}

	@EventHandler
	public void onTick(TickEvent.Pre event) {
		if(mc.player == null) return;
		if(tickCounter < ticks) {
			running = true;
			Rotations.serverYaw = mc.player.getYaw() + yawDifference / ticks;
			Rotations.serverPitch = mc.player.getPitch() + pitchDifference / ticks;
			//c.player.setPitch(mc.player.getPitch() + pitchDifference / ticks);
			//mc.player.setYaw(mc.player.getYaw() + yawDifference / ticks);
			tickCounter++;
		} else if(callback != null) {
			running = false;
			callback.run();
			callback = null;
		}
	}
}
