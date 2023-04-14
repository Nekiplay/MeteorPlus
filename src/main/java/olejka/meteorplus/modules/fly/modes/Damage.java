package olejka.meteorplus.modules.fly.modes;

import meteordevelopment.meteorclient.events.entity.DamageEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import olejka.meteorplus.modules.fly.FlyMode;
import olejka.meteorplus.modules.fly.FlyModes;

public class Damage extends FlyMode {
	public Damage() {
		super(FlyModes.Damage);
	}

	@Override
	public void onActivate() {
		damaged = false;
		ticks = 0;
	}

	@Override
	public void onDeactivate() {

	}

	private int ticks = 0;
	private boolean damaged = false;

	public void onTickEventPre(TickEvent.Pre event) {
		if (damaged && ticks != settings.speedDamageTicks.get()) {
			float yaw = mc.player.getYaw();
			Vec3d forward = Vec3d.fromPolar(0, yaw);
			Vec3d right = Vec3d.fromPolar(0, yaw + 90);

			double velX = 0;
			double velZ = 0;
			double s = settings.speedDamage.get();
			double speedValue = 0.01;
			if (mc.options.forwardKey.isPressed()) {
				velX += forward.x * s;
				velZ += forward.z * s;
			}
			if (mc.options.backKey.isPressed()) {
				velX -= forward.x * s;
				velZ -= forward.z * s;
			}

			if (mc.options.rightKey.isPressed()) {
				velX += right.x * s;
				velZ += right.z * s;
			}
			if (mc.options.leftKey.isPressed()) {
				velX -= right.x * s;
				velZ -= right.z * s;
			}

			((IVec3d) mc.player.getVelocity()).set(velX, settings.speedDamageY.get(), velZ);
			ticks++;
		}
		else if (damaged) {
			damaged = false;
			ticks = 0;
		}
	}

	@Override
	public void onDamage(DamageEvent event) {
		if (event.entity == mc.player) {
			if (event.source.getType() != mc.world.getDamageSources().fall().getType()) {
				damaged = true;
				ticks = 0;

			}
		}
	}
}
