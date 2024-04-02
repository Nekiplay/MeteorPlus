package nekiplay.meteorplus.features.modules.combat.killaura.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import nekiplay.meteorplus.features.modules.combat.killaura.KillAuraPlus;
import nekiplay.meteorplus.features.modules.combat.killaura.KillAuraPlusMode;
import nekiplay.meteorplus.features.modules.combat.killaura.KillAuraPlusModes;
import nekiplay.meteorplus.utils.Perlin2D;
import nekiplay.meteorplus.utils.RaycastUtils;
import nekiplay.meteorplus.utils.RotationUtils;
import nekiplay.meteorplus.utils.algoritms.RandomUtils;
import nekiplay.meteorplus.utils.algoritms.ShadyRotation;
import nekiplay.meteorplus.utils.algoritms.Smooth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class LiquidBounceAura extends KillAuraPlusMode {
	public LiquidBounceAura() {
		super(KillAuraPlusModes.LiquidBounce);
	}

	private final List<Entity> targets = new ArrayList<>();
	private PlayerEntity target;
	private int hitDelayTimer, switchTimer;

	@Override
	public void onDeactivate() {
		hitDelayTimer = 0;
		targets.clear();
	}

	@Override
	public void onTick(TickEvent.Post event) {
		if (mc.player != null && !mc.player.isAlive() || PlayerUtils.getGameMode() == GameMode.SPECTATOR) return;
		if (TargetUtils.isBadTarget(target, settings.range.get()))
			target = TargetUtils.getPlayerTarget(settings.range.get(), settings.priority.get());
		TargetUtils.getList(targets, this::entityCheck, settings.priority.get(), settings.maxTargets.get());

		if (targets.size() > 0) {
			Entity primary = targets.get(0);

			List<Entity> targets2 = targets;

			if(settings.fov.get() < 360.0)
				targets2 = targets.stream().filter(e -> RotationUtils.getAngleToLookVec(
						e.getBoundingBox().getCenter()) <= settings.fov.get() / 2.0)
					.toList();

			if (settings.rotation.get() != KillAuraPlus.RotationMode.None && settings.rotation.get() != KillAuraPlus.RotationMode.Instant)
				rotate(primary, null);
			else if (settings.rotation.get() == KillAuraPlus.RotationMode.Instant) {
				Rotations.rotate(Rotations.getYaw(primary), Rotations.getPitch(primary, Target.Body), null);
			}

			if (primary instanceof PlayerEntity primaryPlayer) {

				if (settings.autoSwitch.get()) {
					Predicate<ItemStack> predicate = switch (settings.weapon.get()) {
						case Axe -> stack -> stack.getItem() instanceof AxeItem;
						case Sword -> stack -> stack.getItem() instanceof SwordItem;
						case Both -> stack -> stack.getItem() instanceof AxeItem || stack.getItem() instanceof SwordItem;
						default -> o -> true;
					};
					FindItemResult weaponResult = InvUtils.findInHotbar(predicate);

					if (shouldShieldBreak()) {
						FindItemResult axeResult = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof AxeItem);
						if (axeResult.found()) weaponResult = axeResult;
					}

					InvUtils.swap(weaponResult.slot(), false);
				}

				if (settings.randomTeleport.get()) {
					mc.player.setPosition(primary.getX() + randomOffset(), primary.getY(), primary.getZ() + randomOffset());
				}
			}
			else {
				if (delayCheck()) targets2.forEach(this::attack);
			}
		}
	}
	private boolean shouldShieldBreak() {
		for (Entity target : targets) {
			if (target instanceof PlayerEntity player) {
				if (player.blockedByShield(mc.world.getDamageSources().playerAttack(mc.player)) && settings.shieldMode.get() == KillAura.ShieldMode.Break) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String getInfoString() {
		return super.getInfoString();
	}

	private double randomOffset() {
		return Math.random() * 4 - 2;
	}
	private boolean itemInHand(Item item) {
		assert mc.player != null;
		return mc.player.getMainHandStack().getItem() != item;
	}
	private boolean entityCheck(Entity entity) {
		if (entity.equals(mc.player) || entity.equals(mc.cameraEntity)) return false;
		if ((entity instanceof LivingEntity && ((LivingEntity) entity).isDead()) || !entity.isAlive()) return false;
		if (PlayerUtils.distanceTo(entity) > settings.range.get()) return false;
		if (!settings.entities.get().contains(entity.getType())) return false;
		if (!settings.nametagged.get() && entity.hasCustomName()) return false;
		if (!PlayerUtils.canSeeEntity(entity) && PlayerUtils.distanceTo(entity) > settings.wallsRange.get()) return false;
		if (entity instanceof PlayerEntity) {
			if (((PlayerEntity) entity).isCreative()) return false;
			if (!Friends.get().shouldAttack((PlayerEntity) entity)) return false;
		}
		return !(entity instanceof AnimalEntity) || settings.babies.get() || !((AnimalEntity) entity).isBaby();
	}

	private boolean delayCheck() {
		if (switchTimer > 0) {
			switchTimer--;
			return false;
		}

		if (mc.player != null && settings.smartDelay.get()) return mc.player.getAttackCooldownProgress(0.5f) >= 1;

		if (hitDelayTimer > 0) {
			hitDelayTimer--;
			return false;
		} else {
			hitDelayTimer = settings.hitDelay.get();
			if (settings.randomDelayEnabled.get()) hitDelayTimer += Math.round(Math.random() * settings.randomDelayMax.get());
			return true;
		}
	}

	private void attack(Entity target) {
		if (Math.random() > settings.hitChance.get() / 100) return;

		if (settings.rotation.get() == KillAuraPlus.RotationMode.OnHit) rotate(target, () -> hitEntity(target));
		else hitEntity(target);
	}

	private void hitEntity(Entity target) {
		assert mc.player != null;
		if(mc.interactionManager != null) {
			if (target instanceof LivingEntity livingEntity) {
				EntityHitResult result = RaycastUtils.raycastEntity(6, Rotations.serverYaw, Rotations.serverPitch, settings.rayTraceAttackBoxStretch.get());
				if (result != null && result.getEntity() != null && settings.rayTraceAttack.get() && settings.rayTraceAttack.isVisible()) {
					mc.interactionManager.attackEntity(mc.player, target);
					mc.player.swingHand(Hand.MAIN_HAND);
				}
				else if (!settings.rayTraceAttack.get() || !settings.rayTraceAttack.isVisible()) {
					mc.interactionManager.attackEntity(mc.player, target);
					mc.player.swingHand(Hand.MAIN_HAND);
				}
			}
		}
	}

	private RotationUtils.Rotation calculateSpeed(Entity target) {
		RotationUtils.Rotation server = new RotationUtils.Rotation(Rotations.serverYaw, Rotations.serverPitch);
		RotationUtils.Rotation targetRotation = new RotationUtils.Rotation(Rotations.getYaw(target), Rotations.getPitch(target, Target.Body));
		double diffAngle = RotationUtils.getRotationDifference(server, targetRotation);
		if (diffAngle < 0) diffAngle = -diffAngle;
		if (diffAngle > 180.0) diffAngle = 180.0;

		double speeds = 180;
		if (settings.rotationSmooth.get() != Smooth.SmoothType.None) {
			speeds = Smooth.getDouble(settings.rotationSmooth.get(), diffAngle, settings.minRotationSpeed.get(), settings.maxRotationSpeed.get());
			return RotationUtils.limitAngleChange(new RotationUtils.Rotation(Rotations.serverYaw, Rotations.serverPitch), new RotationUtils.Rotation(Rotations.getYaw(target), Rotations.getPitch(target, Target.Body)), (float) speeds);
		}
		else if (settings.rotation.get() == KillAuraPlus.RotationMode.LiquidBounce) {
			return RotationUtils.limitAngleChange(new RotationUtils.Rotation(Rotations.serverYaw, Rotations.serverPitch), new RotationUtils.Rotation(Rotations.getYaw(target), Rotations.getPitch(target, Target.Body)), (float) RandomUtils.nextDouble(settings.minRotationSpeed.get(), settings.maxRotationSpeed.get()));
		}
		else if (settings.rotation.get() == KillAuraPlus.RotationMode.Shady) {
			ShadyRotation.Rotation rot = ShadyRotation.getRotationToEntity(target);
			return new RotationUtils.Rotation(rot.yaw, rot.pitch);
		}
		else
		{
			return RotationUtils.limitAngleChange(new RotationUtils.Rotation(Rotations.serverYaw, Rotations.serverPitch), new RotationUtils.Rotation(Rotations.getYaw(target), Rotations.getPitch(target, Target.Body)), (float) speeds);
		}
	}

	private int noice(int multiply) {
		Perlin2D perlin = new Perlin2D(new Random().nextInt());
		float Phi = 0.70710678118f;
		float noice = perlin.Noise(25, 25) + perlin.Noise((25 - 25) * Phi, (25 + 25) * Phi) * -1;
		return (int) (noice * multiply);
	}

	private RotationUtils.Rotation getRotate(Entity target) {
		EntityHitResult result = RaycastUtils.raycastEntity(6, Rotations.serverYaw, Rotations.serverPitch, settings.rayTraceRotateBoxStretch.get());
		if ((result == null || result.getEntity() == null || result.getEntity() != target) && settings.rayTraceRotate.get()) {
			var yaw = calculateSpeed(target);
			if (settings.rotationRandomize.get() == KillAuraPlus.RotationRandimize.Perlin) {
				int yawNoice = noice(settings.rotationRandomizeMultiply.get());
				int pitchNoice = noice(settings.rotationRandomizeMultiply.get());
				yaw.setYaw(yaw.getYaw() + yawNoice);
				yaw.setPitch(yaw.getPitch() + pitchNoice);
			}
			else if (settings.rotationRandomize.get() == KillAuraPlus.RotationRandimize.Random) {
				yaw.setYaw(ThreadLocalRandom.current().nextFloat(yaw.getYaw() - settings.rotationRandomizeMultiply.get(), yaw.getYaw() + settings.rotationRandomizeMultiply.get()));;
				yaw.setPitch(ThreadLocalRandom.current().nextFloat(yaw.getPitch() - settings.rotationRandomizeMultiply.get(), yaw.getPitch() + settings.rotationRandomizeMultiply.get()));;
			}
			else if (settings.rotationRandomize.get() == KillAuraPlus.RotationRandimize.RandomPerlin) {
				float yawf = yaw.getYaw() + noice(settings.rotationRandomizeMultiply.get());
				float pitchf = yaw.getPitch() + noice(settings.rotationRandomizeMultiply.get());
				yaw.setYaw(ThreadLocalRandom.current().nextFloat(yawf - settings.rotationRandomizeMultiply.get(), yawf + settings.rotationRandomizeMultiply.get()));;
				yaw.setPitch(ThreadLocalRandom.current().nextFloat(pitchf - settings.rotationRandomizeMultiply.get(), pitchf + settings.rotationRandomizeMultiply.get()));;
			}
			return yaw;
		}
		else if (!settings.rayTraceRotate.get() || !settings.rayTraceRotate.isVisible()) {
			var yaw = calculateSpeed(target);
			if (settings.rotationRandomize.get() == KillAuraPlus.RotationRandimize.Perlin) {
				int yawNoice = noice(settings.rotationRandomizeMultiply.get());
				int pitchNoice = noice(settings.rotationRandomizeMultiply.get());
				yaw.setYaw(yaw.getYaw() + yawNoice);
				yaw.setPitch(yaw.getPitch() + pitchNoice);
			}
			else if (settings.rotationRandomize.get() == KillAuraPlus.RotationRandimize.Random) {
				yaw.setYaw(ThreadLocalRandom.current().nextFloat(yaw.getYaw() - settings.rotationRandomizeMultiply.get(), yaw.getYaw() + settings.rotationRandomizeMultiply.get()));;
				yaw.setPitch(ThreadLocalRandom.current().nextFloat(yaw.getPitch() - settings.rotationRandomizeMultiply.get(), yaw.getPitch() + settings.rotationRandomizeMultiply.get()));;
			}
			else if (settings.rotationRandomize.get() == KillAuraPlus.RotationRandimize.RandomPerlin) {
				float yawf = yaw.getYaw() + noice(settings.rotationRandomizeMultiply.get());
				float pitchf = yaw.getPitch() + noice(settings.rotationRandomizeMultiply.get());
				yaw.setYaw(ThreadLocalRandom.current().nextFloat(yawf - settings.rotationRandomizeMultiply.get(), yawf + settings.rotationRandomizeMultiply.get()));;
				yaw.setPitch(ThreadLocalRandom.current().nextFloat(pitchf - settings.rotationRandomizeMultiply.get(), pitchf + settings.rotationRandomizeMultiply.get()));;
			}
			return yaw;
		}
		return null;
	}
	private RotationUtils.Rotation lastRotate = null;
	private void rotate(Entity target, Runnable callback) {
		RotationUtils.Rotation rotation2 = getRotate(target);
		if (rotation2 != null) {
			if (settings.rotationTickSmooth.get() == KillAuraPlus.RotationTickSmooth.Perlin) {
				int noice = noice(settings.rotationTickSmoothMultiply.get());
				if (noice != 0) {
					lastRotate = rotation2;
					if (settings.rotation.get() == KillAuraPlus.RotationMode.Shady) {
						if (!ShadyRotation.running) {
							ShadyRotation.smoothLook(rotation2, settings.rotationShadySpeed.get(), settings.clientLook.get(), (() -> {}));
						}
					}
					else {
						Rotations.rotate(rotation2.getYaw(), rotation2.getPitch(), 0, settings.clientLook.get(), null);
					}
					if (settings.clientLook.get()) {
						mc.player.setPitch( rotation2.getPitch());
						mc.player.setYaw(rotation2.getYaw());
					}
				}
			}
			else if (settings.rotationTickSmooth.get() == KillAuraPlus.RotationTickSmooth.Random) {
				if (ThreadLocalRandom.current().nextBoolean()) {
					lastRotate = rotation2;
					if (settings.rotation.get() == KillAuraPlus.RotationMode.Shady) {
						if (!ShadyRotation.running) {
							ShadyRotation.smoothLook(rotation2, settings.rotationShadySpeed.get(), settings.clientLook.get(), (() -> {}));
						}
					}
					else {
						Rotations.rotate(rotation2.getYaw(), rotation2.getPitch(), 0, settings.clientLook.get(), null);
					}
					if (settings.clientLook.get()) {
						mc.player.setPitch( rotation2.getPitch());
						mc.player.setYaw(rotation2.getYaw());
					}
				}
			}
			else if (settings.rotationTickSmooth.get() == KillAuraPlus.RotationTickSmooth.RandomPerlin) {
				if (ThreadLocalRandom.current().nextBoolean() && noice(settings.rotationTickSmoothMultiply.get()) != 0) {
					lastRotate = rotation2;
					if (settings.rotation.get() == KillAuraPlus.RotationMode.Shady) {
						if (!ShadyRotation.running) {
							ShadyRotation.smoothLook(rotation2, settings.rotationShadySpeed.get(), settings.clientLook.get(), (() -> {}));
						}
					}
					else {
						Rotations.rotate(rotation2.getYaw(), rotation2.getPitch(), 0, settings.clientLook.get(), null);
					}
					if (settings.clientLook.get()) {
						mc.player.setPitch( rotation2.getPitch());
						mc.player.setYaw(rotation2.getYaw());
					}
				}
			}
			else {
				lastRotate = rotation2;
				if (settings.rotation.get() == KillAuraPlus.RotationMode.Shady) {
					if (!ShadyRotation.running) {
						ShadyRotation.smoothLook(rotation2, settings.rotationShadySpeed.get(), settings.clientLook.get(), (() -> {}));
					}
				}
				else {
					Rotations.rotate(rotation2.getYaw(), rotation2.getPitch(), 0, settings.clientLook.get(), null);
				}
				if (settings.clientLook.get()) {
					mc.player.setPitch( rotation2.getPitch());
					mc.player.setYaw(rotation2.getYaw());
				}
			}
		}
		else if (lastRotate != null) {
			if (settings.rotation.get() == KillAuraPlus.RotationMode.Shady) {
				ShadyRotation.smoothLook(lastRotate, settings.rotationShadySpeed.get(), settings.clientLook.get(), (() -> {}));
			}
			else {
				Rotations.rotate(lastRotate.getYaw(), lastRotate.getPitch(), 0, settings.clientLook.get(), null);
			}
			if (settings.clientLook.get()) {
				mc.player.setPitch( rotation2.getPitch());
				mc.player.setYaw(rotation2.getYaw());
			}
		}
	}

	public Entity getTarget() {
		if (!targets.isEmpty()) return targets.get(0);
		return null;
	}
}
