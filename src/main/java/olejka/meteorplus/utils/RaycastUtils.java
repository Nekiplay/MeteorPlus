package olejka.meteorplus.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

import static meteordevelopment.meteorclient.MeteorClient.mc;


public class RaycastUtils {
	public static EntityHitResult raycastEntity(final double range, final float yaw, final float pitch, double boxexpand) {
		Entity camera = mc.getCameraEntity();
		Vec3d cameraVec = camera.getCameraPosVec(1f);

		final float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		final float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		final float pitchCos = -MathHelper.cos(-pitch * 0.017453292F);
		final float pitchSin = MathHelper.sin(-pitch * 0.017453292F);

		final Vec3d rotation = new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);

		Vec3d vec3d3 = cameraVec.add(rotation.x * range, rotation.y * range, rotation.z * range);
		Box box = camera.getBoundingBox().stretch(rotation.multiply(range)).expand(boxexpand, boxexpand, boxexpand);

		return ProjectileUtil.raycast(camera, cameraVec, vec3d3, box, new Predicate<Entity>() {
			@Override
			public boolean test(Entity entity) {
				return !entity.isSpectator() && entity.isCollidable();
			}
		}, 0);
	}

	public static Vec3d getNearestPoint(Vec3d eyes, Box box) {
		double x = eyes.z;
		double y = eyes.z;
		double z = eyes.z;
		for (int i = 0; i < 3; i++) {
			if (eyes.x > box.maxX) {
				x = box.maxX;
			}
			if (eyes.z > box.maxZ) {
				z = box.maxZ;
			}
			if (eyes.y > box.maxY) {
				y = box.maxY;
			}

			// Min
			if (eyes.x < box.minX) {
				x = box.minX;
			}
			if (eyes.z < box.minZ) {
				z = box.minZ;
			}
			if (eyes.y < box.minY) {
				y = box.minY;
			}
		}
		return new Vec3d(x, y, z);
	}
}
