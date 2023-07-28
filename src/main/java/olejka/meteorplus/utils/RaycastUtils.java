package olejka.meteorplus.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.function.Predicate;

import static meteordevelopment.meteorclient.MeteorClient.mc;


public class RaycastUtils {
	public static Vec3d getRotationVector(float pitch, float yaw) {
		float f = pitch * ((float)Math.PI / 180);
		float g = -yaw * ((float)Math.PI / 180);
		float h = MathHelper.cos(g);
		float i = MathHelper.sin(g);
		float j = MathHelper.cos(f);
		float k = MathHelper.sin(f);
		return new Vec3d(i * j, -k, h * j);
	}

	public static HitResult raycast(Vec3d camera, Vec3d rotation, double maxDistance, float tickDelta, boolean includeFluids) {
		Vec3d vec3d = camera;
		Vec3d vec3d2 = rotation;
		Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
		return mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, mc.player));
	}
}
