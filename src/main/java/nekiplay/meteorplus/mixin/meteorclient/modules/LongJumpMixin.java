package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.LongJump;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.*;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = LongJump.class, remap = false)
public class LongJumpMixin {

	@Unique
	private final LongJump longJump = (LongJump)(Object) this;

	@Shadow
	@Final
	private final SettingGroup sgGeneral = longJump.settings.getDefaultGroup();;

	@Shadow
	@Final
	public final Setting<LongJump.JumpMode> jumpMode = (Setting<LongJump.JumpMode>) longJump.settings.get("mode");

	@Shadow
	@Final
	private final Setting<Double> vanillaBoostFactor = (Setting<Double>) longJump.settings.get("vanilla-boost-factor");

	@Shadow
	@Final
	private final Setting<Double> burstInitialSpeed = (Setting<Double>) longJump.settings.get("burst-initial-speed");

	@Shadow
	@Final
	private final Setting<Double> burstBoostFactor = (Setting<Double>) longJump.settings.get("burst-boost-factor");

	@Shadow
	@Final
	private final Setting<Boolean> onlyOnGround = (Setting<Boolean>) longJump.settings.get("only-on-ground");

	@Shadow
	@Final
	private final Setting<Boolean> onJump = (Setting<Boolean>) longJump.settings.get("on-jump");

	@Shadow
	@Final
	public final Setting<Double> timer =  (Setting<Double>) longJump.settings.get("timer");

	@Shadow
	@Final
	private final Setting<Boolean> autoDisable = (Setting<Boolean>) longJump.settings.get("auto-disable");;

	@Shadow
	private int stage;
	@Shadow
	private double moveSpeed;
	@Shadow
	private boolean jumping = false;
	@Shadow
	private int airTicks;
	@Shadow
	private int groundTicks;
	@Shadow
	private boolean jumped = false;

	/**
	 * @author Neki_play
	 * @reason Fix override timer if Timer value equals 1
	 */
	@EventHandler
	@Overwrite
	private void onPlayerMove(PlayerMoveEvent event) {
		if (timer.get() != 1.0) {
			Modules.get().get(Timer.class).setOverride(PlayerUtils.isMoving() ? timer.get() : Timer.OFF);
		}
		switch (jumpMode.get()) {
			case Vanilla -> {
				if (PlayerUtils.isMoving() && mc.options.jumpKey.isPressed()) {
					double dir = getDir();

					double xDir = Math.cos(Math.toRadians(dir + 90));
					double zDir = Math.sin(Math.toRadians(dir + 90));

					if (!mc.world.isSpaceEmpty(mc.player.getBoundingBox().offset(0.0, mc.player.getVelocity().y, 0.0)) || mc.player.verticalCollision) {
						((IVec3d) event.movement).setXZ(xDir * 0.29F, zDir * 0.29F);
					}
					if ((event.movement.getY() == .33319999363422365)) {
						((IVec3d) event.movement).setXZ(xDir * vanillaBoostFactor.get(), zDir * vanillaBoostFactor.get());
					}
				}
			}
			case Burst -> {
				if (stage != 0 && !mc.player.isOnGround() && autoDisable.get()) jumping = true;
				if (jumping && (mc.player.getY() - (int) mc.player.getY() < 0.01)) {
					jumping = false;
					longJump.toggle();;
					longJump.info("Disabling after jump.");
				}

				if (onlyOnGround.get() && !mc.player.isOnGround() && stage == 0) return;

				double xDist = mc.player.getX() - mc.player.prevX;
				double zDist = mc.player.getZ() - mc.player.prevZ;
				double lastDist = Math.sqrt((xDist * xDist) + (zDist * zDist));

				if (PlayerUtils.isMoving() && (!onJump.get() || mc.options.jumpKey.isPressed()) && !mc.player.isInLava() && !mc.player.isTouchingWater()) {
					if (stage == 0) moveSpeed = getMoveSpeed() * burstInitialSpeed.get();
					else if (stage == 1) {
						((IVec3d) event.movement).setY(0.42);
						moveSpeed *= burstBoostFactor.get();
					}
					else if (stage == 2) {
						final double difference = lastDist - getMoveSpeed();
						moveSpeed = lastDist - difference;
					}
					else moveSpeed = lastDist - lastDist / 159;

					setMoveSpeed(event, moveSpeed = Math.max(getMoveSpeed(), moveSpeed));
					if (!mc.player.verticalCollision && !mc.world.isSpaceEmpty(mc.player.getBoundingBox().offset(0.0, mc.player.getVelocity().y, 0.0)) && !mc.world.isSpaceEmpty(mc.player.getBoundingBox().offset(0.0, -0.4, 0.0))) {
						((IVec3d) event.movement).setY(-0.001);
					}

					stage++;
				}
			}
		}
	}

	@Shadow
	private double getDir() {
		double dir = 0;

		if (Utils.canUpdate()) {
			dir = mc.player.getYaw() + ((mc.player.forwardSpeed < 0) ? 180 : 0);

			if (mc.player.sidewaysSpeed > 0) {
				dir += -90F * ((mc.player.forwardSpeed < 0) ? -0.5F : ((mc.player.forwardSpeed > 0) ? 0.5F : 1F));
			} else if (mc.player.sidewaysSpeed < 0) {
				dir += 90F * ((mc.player.forwardSpeed < 0) ? -0.5F : ((mc.player.forwardSpeed > 0) ? 0.5F : 1F));
			}
		}
		return dir;
	}
	@Shadow
	private double getMoveSpeed() {
		double base = 0.2873;
		if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
			base *= 1.0 + 0.2 * (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1);
		}
		return base;
	}

	@Shadow
	private void setMoveSpeed(PlayerMoveEvent event, double speed) {
		double forward = mc.player.forwardSpeed;
		double strafe = mc.player.sidewaysSpeed;
		float yaw = mc.player.getYaw();

		if (!PlayerUtils.isMoving()) {
			((IVec3d) event.movement).setXZ(0, 0);
		}
		else {
			if (forward != 0) {
				if (strafe > 0) yaw += ((forward > 0) ? -45 : 45);
				else if (strafe < 0) yaw += ((forward > 0) ? 45 : -45);
			}
			strafe = 0;
			if (forward > 0) forward = 1;
			else if (forward < 0) forward = -1;
		}

		double cos = Math.cos(Math.toRadians(yaw + 90));
		double sin = Math.sin(Math.toRadians(yaw + 90));
		((IVec3d) event.movement).setXZ((forward * speed * cos) + (strafe * speed * sin), (forward * speed * sin) + (strafe * speed * cos));
	}
}
