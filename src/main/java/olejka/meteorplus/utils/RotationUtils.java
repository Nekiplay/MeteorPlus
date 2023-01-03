package olejka.meteorplus.utils;

import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RotationUtils {
	public static Vec3d getEyesPos()
	{
		ClientPlayerEntity player = mc.player;

		return new Vec3d(player.getX(),
			player.getY() + player.getEyeHeight(player.getPose()),
			player.getZ());
	}

	public static float getAngleDifference(final float a, final float b) {
		return ((((a - b) % 360F) + 540F) % 360F) - 180F;
	}

	public static double getRotationDifference(final Rotation a, final Rotation b) {
		return Math.hypot(getAngleDifference(a.yaw, b.yaw), a.getPitch() - b.getPitch());
	}

	public static Rotation toRotation(final Vec3d vec, final boolean predict) {
		final Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getBoundingBox().minY +
			mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());

		if(predict) {
			if(mc.player.isOnGround()) {
				eyesPos.add(mc.player.getVelocity().x, 0.0, mc.player.getVelocity().z);
			}else eyesPos.add(mc.player.getVelocity().x, mc.player.getVelocity().y, mc.player.getVelocity().z);
		}

		final double diffX = vec.x - eyesPos.x;
		final double diffY = vec.y - eyesPos.y;
		final double diffZ = vec.z - eyesPos.z;

		return new Rotation(
			(float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F,
			(float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)))));
	}

	public static Vec3d getCenter(final Box bb) {
		return new Vec3d(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
	}

	public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation, final float turnSpeed) {
		final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
		final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

		return new Rotation(
			currentRotation.getYaw() + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
			currentRotation.getPitch() + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)
			));
	}

	public static Rotation getNeededRotations(Vec3d vec)
	{
		Vec3d eyesPos = getEyesPos();

		double diffX = vec.x - eyesPos.x;
		double diffY = vec.y - eyesPos.y;
		double diffZ = vec.z - eyesPos.z;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

		return new Rotation(yaw, pitch);
	}


	public static double getAngleToLookVec(Vec3d vec)
	{
		Rotation needed = getNeededRotations(vec);

		ClientPlayerEntity player = mc.player;
		float currentYaw = MathHelper.wrapDegrees(player.getYaw());
		float currentPitch = MathHelper.wrapDegrees(player.getPitch());

		float diffYaw = currentYaw - needed.yaw;
		float diffPitch = currentPitch - needed.pitch;

		return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
	}

	public static final class Rotation
	{
		private float yaw;
		private float pitch;

		public Rotation(float yaw, float pitch)
		{
			this.yaw = MathHelper.wrapDegrees(yaw);
			this.pitch = MathHelper.wrapDegrees(pitch);
		}

		public Rotation(double yaw, double pitch)
		{
			this.yaw = MathHelper.wrapDegrees((float)yaw);
			this.pitch = MathHelper.wrapDegrees((float)pitch);
		}

		public float getYaw()
		{
			return yaw;
		}

		public float getPitch()
		{
			return pitch;
		}

		public void setYaw(float yaw) {
			this.yaw = yaw;
		}

		public void setPitch(float pitch) {
			this.pitch = pitch;
		}
	}
}
