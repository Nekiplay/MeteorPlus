package olejka.meteorplus.modules;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.GameMode;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.utils.Perlin2D;
import olejka.meteorplus.utils.RaycastUtils;
import olejka.meteorplus.utils.RotationUtils;
import olejka.meteorplus.utils.algoritms.RandomUtils;
import olejka.meteorplus.utils.algoritms.ShadyRotation;
import olejka.meteorplus.utils.algoritms.Smooth;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class KillAuraBetter extends Module {
	public KillAuraBetter() {
		super(MeteorPlus.CATEGORY, "killaura-better", "Better killaura on player.");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgTargeting = settings.createGroup("Targeting");
	private final SettingGroup sgDelay = settings.createGroup("Delay");

	public enum RotationTickSmooth
	{
		None,
		Perlin,
		Random,
		RandomPerlin,
	}

	public enum RotationRandimize
	{
		None,
		Perlin,
		Random,
		RandomPerlin,
	}

	public enum RotationMode
	{
		None,
		OnHit,
		Instant,
		LiquidBounce,
		SmoothCenter,
		Shady,
	}

	// General

	private final Setting<Boolean> randomTeleport = sgGeneral.add(new BoolSetting.Builder()
		.name("random-teleport")
		.description("Randomly teleport around the target")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> revertKnockback = sgGeneral.add(new BoolSetting.Builder()
		.name("revert-knockback")
		.description("Revert enemy knockback")
		.defaultValue(false)
		.build()
	);

	private final Setting<RotationMode> rotation = sgGeneral.add(new EnumSetting.Builder<RotationMode>()
		.name("rotate")
		.description("Determines when you should rotate towards the target.")
		.defaultValue(RotationMode.LiquidBounce)
		.build()
	);

	private final Setting<Boolean> clientLook = sgGeneral.add(new BoolSetting.Builder()
		.name("client-look")
		.description("Client rotation")
		.defaultValue(false)
		.build()
	);

	private final Setting<Smooth.SmoothType> rotationSmooth = sgGeneral.add(new EnumSetting.Builder<Smooth.SmoothType>()
		.name("rotate-smooth")
		.description("Determines when you should rotate towards the target.")
		.defaultValue(Smooth.SmoothType.None)
		.visible(() -> rotation.get() != RotationMode.Instant && rotation.get() != RotationMode.None)
		.build()
	);

	private final Setting<Integer> rotationShadySpeed = sgGeneral.add(new IntSetting.Builder()
		.name("rotation-speed")
		.description("Speed.")
		.defaultValue(4)
		.range(1, 5)
		.sliderRange(1, 5)
		.visible(() -> rotation.get() == RotationMode.Shady)
		.build()
	);

	private final Setting<RotationRandimize> rotationRandomize = sgGeneral.add(new EnumSetting.Builder<RotationRandimize>()
		.name("rotation-randomize")
		.description("Rotation randomize.")
		.defaultValue(RotationRandimize.None)
		.visible(() -> rotationSmooth.get() != Smooth.SmoothType.None && rotationSmooth.isVisible())
		.build()
	);

	private final Setting<Integer> rotationRandomizeMultiply = sgGeneral.add(new IntSetting.Builder()
		.name("rotation-randomize-multiply")
		.description("Speed.")
		.defaultValue(4)
		.range(0, 32)
		.sliderRange(0, 32)
		.visible(() -> rotationRandomize.get() != RotationRandimize.None && rotationSmooth.isVisible())
		.build()
	);


	private final Setting<RotationTickSmooth> rotationTickSmooth = sgGeneral.add(new EnumSetting.Builder<RotationTickSmooth>()
		.name("rotation-tick-smooth")
		.description("Rotation randomize.")
		.defaultValue(RotationTickSmooth.None)
		.visible(() -> rotationSmooth.get() != Smooth.SmoothType.None && rotationSmooth.isVisible())
		.build()
	);

	private final Setting<Integer> rotationTickSmoothMultiply = sgGeneral.add(new IntSetting.Builder()
		.name("rotation-tick-smooth-multiply")
		.description("Speed.")
		.defaultValue(2)
		.range(0, 32)
		.sliderRange(0, 32)
		.visible(() -> rotationTickSmooth.get() != RotationTickSmooth.None && rotationTickSmooth.get() != RotationTickSmooth.Random && rotationSmooth.isVisible())
		.build()
	);


	private final Setting<Double> maxRotationSpeed = sgGeneral.add(new DoubleSetting.Builder()
		.name("max-rotation-speed")
		.description("Maximum rotation speed.")
		.defaultValue(180)
		.range(0, 180)
		.sliderRange(0, 180)
		.visible(() -> rotation.get() != RotationMode.None && rotation.get() != RotationMode.Shady && rotation.get() != RotationMode.Instant && rotationSmooth.isVisible())
		.build()
	);

	private final Setting<Double> minRotationSpeed = sgGeneral.add(new DoubleSetting.Builder()
		.name("min-rotation-speed")
		.description("Minimum rotation speed.")
		.defaultValue(180)
		.range(0, 180)
		.sliderRange(0, 180)
		.visible(() -> rotation.get() != RotationMode.None && rotation.get() != RotationMode.Shady && rotation.get() != RotationMode.Instant && rotationSmooth.isVisible())
		.build()
	);

	private final Setting<Boolean> rayTraceRotate = sgGeneral.add(new BoolSetting.Builder()
		.name("raytrace-rotate")
		.description("Not rotate if you head see player")
		.visible(() -> rotation.get() != RotationMode.Instant && rotation.get() != RotationMode.None && rotationSmooth.isVisible())
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> rayTraceAttack = sgGeneral.add(new BoolSetting.Builder()
		.name("raytrace-attack")
		.description("Not attack if you head don't see player")
		.visible(() -> rotation.get() != RotationMode.Instant && rotation.get() != RotationMode.None && rotationSmooth.isVisible())
		.defaultValue(false)
		.build()
	);

	private final Setting<Double> rayTraceRotateBoxStretch = sgGeneral.add(new DoubleSetting.Builder()
		.name("raytrace-rotate-box-stretch")
		.description("raytrace-rotate-box-stretch.")
		.defaultValue(0.7)
		.range(-1, 1)
		.sliderRange(-1, 1)
		.visible(() -> rayTraceRotate.isVisible())
		.build()
	);

	private final Setting<Double> rayTraceAttackBoxStretch = sgGeneral.add(new DoubleSetting.Builder()
		.name("raytrace-attack-box-stretch")
		.description("raytrace-attack-box-stretch.")
		.defaultValue(0.7)
		.range(-1, 1)
		.sliderRange(-1, 1)
		.visible(() -> rayTraceAttack.isVisible())
		.build()
	);

	private final Setting<Boolean> shieldBreaker = sgGeneral.add(new BoolSetting.Builder()
		.name("shield-breaker")
		.description("Break enemy shield by axe")
		.defaultValue(true)
		.build()
	);

	private final Setting<Double> hitChance = sgGeneral.add(new DoubleSetting.Builder()
		.name("hit-chance")
		.description("The probability of your hits landing.")
		.defaultValue(100)
		.range(1, 100)
		.sliderRange(1, 100)
		.build()
	);

	// Targeting
	private final Setting<Object2BooleanMap<EntityType<?>>> entities = sgTargeting.add(new EntityTypeListSetting.Builder()
		.name("entities")
		.description("Entities to attack.")
		.onlyAttackable()
		.build()
	);

	private final Setting<Double> fov = sgTargeting.add(new DoubleSetting.Builder()
		.name("Fov")
		.description("The fov the entity can be to attack it.")
		.defaultValue(360)
		.min(30)
		.max(360)
		.sliderMax(360)
		.sliderMin(30)
		.sliderRange(30, 360)
		.build()
	);

	private final Setting<Double> range = sgTargeting.add(new DoubleSetting.Builder()
		.name("range")
		.description("The maximum range the entity can be to attack it.")
		.defaultValue(4.5)
		.min(0)
		.sliderMax(6)
		.build()
	);

	private final Setting<Double> wallsRange = sgTargeting.add(new DoubleSetting.Builder()
		.name("walls-range")
		.description("The maximum range the entity can be attacked through walls.")
		.defaultValue(3.5)
		.min(0)
		.sliderMax(6)
		.build()
	);

	private final Setting<SortPriority> priority = sgTargeting.add(new EnumSetting.Builder<SortPriority>()
		.name("priority")
		.description("How to filter targets within range.")
		.defaultValue(SortPriority.LowestHealth)
		.build()
	);

	private final Setting<Integer> maxTargets = sgTargeting.add(new IntSetting.Builder()
		.name("max-targets")
		.description("How many entities to target at once.")
		.defaultValue(1)
		.min(1)
		.sliderRange(1, 5)
		.build()
	);

	private final Setting<Boolean> babies = sgTargeting.add(new BoolSetting.Builder()
		.name("babies")
		.description("Whether or not to attack baby variants of the entity.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> nametagged = sgTargeting.add(new BoolSetting.Builder()
		.name("nametagged")
		.description("Whether or not to attack mobs with a name tag.")
		.defaultValue(false)
		.build()
	);

	// Delay

	private final Setting<Boolean> smartDelay = sgDelay.add(new BoolSetting.Builder()
		.name("smart-delay")
		.description("Uses the vanilla cooldown to attack entities.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Integer> hitDelay = sgDelay.add(new IntSetting.Builder()
		.name("hit-delay")
		.description("How fast you hit the entity in ticks.")
		.defaultValue(0)
		.min(0)
		.sliderMax(60)
		.visible(() -> !smartDelay.get())
		.build()
	);

	private final Setting<Boolean> randomDelayEnabled = sgDelay.add(new BoolSetting.Builder()
		.name("random-delay-enabled")
		.description("Adds a random delay between hits to attempt to bypass anti-cheats.")
		.defaultValue(false)
		.visible(() -> !smartDelay.get())
		.build()
	);

	private final Setting<Integer> randomDelayMax = sgDelay.add(new IntSetting.Builder()
		.name("random-delay-max")
		.description("The maximum value for random delay.")
		.defaultValue(4)
		.min(0)
		.sliderMax(20)
		.visible(() -> randomDelayEnabled.get() && !smartDelay.get())
		.build()
	);

	private final Setting<Integer> switchDelay = sgDelay.add(new IntSetting.Builder()
		.name("switch-delay")
		.description("How many ticks to wait before hitting an entity after switching hotbar slots.")
		.defaultValue(0)
		.min(0)
		.build()
	);

	private final List<Entity> targets = new ArrayList<>();
	private int hitDelayTimer, switchTimer;

	@Override
	public void onDeactivate() {
		hitDelayTimer = 0;
		targets.clear();
	}

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		if (mc.player != null && !mc.player.isAlive() || PlayerUtils.getGameMode() == GameMode.SPECTATOR) return;
		TargetUtils.getList(targets, this::entityCheck, priority.get(), maxTargets.get());

		if (targets.size() > 0) {
			Entity primary = targets.get(0);

			List<Entity> targets2 = targets;

			if(fov.get() < 360.0)
				targets2 = targets.stream().filter(e -> RotationUtils.getAngleToLookVec(
					e.getBoundingBox().getCenter()) <= fov.get() / 2.0)
					.toList();

			if (rotation.get() != RotationMode.None && rotation.get() != RotationMode.Instant)
				rotate(primary, null);
			else if (rotation.get() == RotationMode.Instant) {
				Rotations.rotate(Rotations.getYaw(primary), Rotations.getPitch(primary, Target.Body), null);
			}

			if (primary instanceof PlayerEntity primaryPlayer) {
				if (primaryPlayer.isBlocking() && shieldBreaker.get()) {
					FindItemResult axeResult = InvUtils.findInHotbar(itemStack -> {
						Item item = itemStack.getItem();
						return item instanceof AxeItem;
					});
					if (axeResult.found()) {
						InvUtils.swap(axeResult.slot(), false);
						if (itemInHand(mc.player.getInventory().getStack(axeResult.slot()).getItem())) return;
					}
					targets2.forEach(this::attack);
				} else {
					FindItemResult swordResult = InvUtils.findInHotbar(itemStack -> {
						Item item = itemStack.getItem();
						return item instanceof SwordItem;
					});
					if (swordResult.found()) {
						InvUtils.swap(swordResult.slot(), false);
						if (itemInHand(mc.player.getInventory().getStack(swordResult.slot()).getItem())) return;
					}
					if (delayCheck()) targets2.forEach(this::attack);

					if (randomTeleport.get()) {
						mc.player.setPosition(primary.getX() + randomOffset(), primary.getY(), primary.getZ() + randomOffset());
					}
				}
			}
			else {
				if (delayCheck()) targets2.forEach(this::attack);
			}
		}
	}

	private boolean itemInHand(Item item) {
		assert mc.player != null;
		return mc.player.getMainHandStack().getItem() != item;
	}

	@EventHandler
	private void onSendPacket(PacketEvent.Send event) {
		if (event.packet instanceof UpdateSelectedSlotC2SPacket) {
			switchTimer = switchDelay.get();
		}
		else if (event.packet instanceof IPlayerInteractEntityC2SPacket packet) {
			if (mc.player != null && revertKnockback.get()) {
				Entity entity = packet.getEntity();
				double yaw = Rotations.getYaw(entity) - 180;
				double pitch = Rotations.getPitch(entity, Target.Body);
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) yaw, (float) pitch, mc.player.isOnGround()));
			}
		}
	}
	private double randomOffset() {
		return Math.random() * 4 - 2;
	}

	private boolean entityCheck(Entity entity) {
		if (entity.equals(mc.player) || entity.equals(mc.cameraEntity)) return false;
		if ((entity instanceof LivingEntity && ((LivingEntity) entity).isDead()) || !entity.isAlive()) return false;
		if (PlayerUtils.distanceTo(entity) > range.get()) return false;
		if (!entities.get().getBoolean(entity.getType())) return false;
		if (!nametagged.get() && entity.hasCustomName()) return false;
		if (!PlayerUtils.canSeeEntity(entity) && PlayerUtils.distanceTo(entity) > wallsRange.get()) return false;
		if (entity instanceof PlayerEntity) {
			if (((PlayerEntity) entity).isCreative()) return false;
			if (!Friends.get().shouldAttack((PlayerEntity) entity)) return false;
		}
		if (Modules.get().get(AntiBotPlus.class).isBot(entity))
			return false;
		return !(entity instanceof AnimalEntity) || babies.get() || !((AnimalEntity) entity).isBaby();
	}

	private boolean delayCheck() {
		if (switchTimer > 0) {
			switchTimer--;
			return false;
		}

		if (mc.player != null && smartDelay.get()) return mc.player.getAttackCooldownProgress(0.5f) >= 1;

		if (hitDelayTimer > 0) {
			hitDelayTimer--;
			return false;
		} else {
			hitDelayTimer = hitDelay.get();
			if (randomDelayEnabled.get()) hitDelayTimer += Math.round(Math.random() * randomDelayMax.get());
			return true;
		}
	}

	private void attack(Entity target) {
		if (Math.random() > hitChance.get() / 100) return;

		if (rotation.get() == RotationMode.OnHit) rotate(target, () -> hitEntity(target));
		else hitEntity(target);
	}

	private void hitEntity(Entity target) {
		assert mc.player != null;
		if(mc.interactionManager != null) {
			if (target instanceof LivingEntity livingEntity) {
				EntityHitResult result = RaycastUtils.raycastEntity(6, Rotations.serverYaw, Rotations.serverPitch, rayTraceAttackBoxStretch.get());
				if (result != null && result.getEntity() != null && rayTraceAttack.get() && rayTraceAttack.isVisible()) {
					mc.interactionManager.attackEntity(mc.player, target);
					mc.player.swingHand(Hand.MAIN_HAND);
				}
				else if (!rayTraceAttack.get() || !rayTraceAttack.isVisible()) {
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
		if (rotationSmooth.get() != Smooth.SmoothType.None) {
			speeds = Smooth.getDouble(rotationSmooth.get(), diffAngle, minRotationSpeed.get(), maxRotationSpeed.get());
			return RotationUtils.limitAngleChange(new RotationUtils.Rotation(Rotations.serverYaw, Rotations.serverPitch), new RotationUtils.Rotation(Rotations.getYaw(target), Rotations.getPitch(target, Target.Body)), (float) speeds);
		}
		else if (rotation.get() == RotationMode.LiquidBounce) {
			return RotationUtils.limitAngleChange(new RotationUtils.Rotation(Rotations.serverYaw, Rotations.serverPitch), new RotationUtils.Rotation(Rotations.getYaw(target), Rotations.getPitch(target, Target.Body)), (float) RandomUtils.nextDouble(minRotationSpeed.get(), maxRotationSpeed.get()));
		}
		else if (rotation.get() == RotationMode.Shady) {
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
		EntityHitResult result = RaycastUtils.raycastEntity(6, Rotations.serverYaw, Rotations.serverPitch, rayTraceRotateBoxStretch.get());
		if ((result == null || result.getEntity() == null || result.getEntity() != target) && rayTraceRotate.get()) {
			var yaw = calculateSpeed(target);
			if (rotationRandomize.get() == RotationRandimize.Perlin) {
				int yawNoice = noice(rotationRandomizeMultiply.get());
				int pitchNoice = noice(rotationRandomizeMultiply.get());
				yaw.setYaw(yaw.getYaw() + yawNoice);
				yaw.setPitch(yaw.getPitch() + pitchNoice);
			}
			else if (rotationRandomize.get() == RotationRandimize.Random) {
				yaw.setYaw(ThreadLocalRandom.current().nextFloat(yaw.getYaw() - rotationRandomizeMultiply.get(), yaw.getYaw() + rotationRandomizeMultiply.get()));;
				yaw.setPitch(ThreadLocalRandom.current().nextFloat(yaw.getPitch() - rotationRandomizeMultiply.get(), yaw.getPitch() + rotationRandomizeMultiply.get()));;
			}
			else if (rotationRandomize.get() == RotationRandimize.RandomPerlin) {
				float yawf = yaw.getYaw() + noice(rotationRandomizeMultiply.get());
				float pitchf = yaw.getPitch() + noice(rotationRandomizeMultiply.get());
				yaw.setYaw(ThreadLocalRandom.current().nextFloat(yawf - rotationRandomizeMultiply.get(), yawf + rotationRandomizeMultiply.get()));;
				yaw.setPitch(ThreadLocalRandom.current().nextFloat(pitchf - rotationRandomizeMultiply.get(), pitchf + rotationRandomizeMultiply.get()));;
			}
			return yaw;
		}
		else if (!rayTraceRotate.get() || !rayTraceRotate.isVisible()) {
			var yaw = calculateSpeed(target);
			if (rotationRandomize.get() == RotationRandimize.Perlin) {
				int yawNoice = noice(rotationRandomizeMultiply.get());
				int pitchNoice = noice(rotationRandomizeMultiply.get());
				yaw.setYaw(yaw.getYaw() + yawNoice);
				yaw.setPitch(yaw.getPitch() + pitchNoice);
			}
			else if (rotationRandomize.get() == RotationRandimize.Random) {
				yaw.setYaw(ThreadLocalRandom.current().nextFloat(yaw.getYaw() - rotationRandomizeMultiply.get(), yaw.getYaw() + rotationRandomizeMultiply.get()));;
				yaw.setPitch(ThreadLocalRandom.current().nextFloat(yaw.getPitch() - rotationRandomizeMultiply.get(), yaw.getPitch() + rotationRandomizeMultiply.get()));;
			}
			else if (rotationRandomize.get() == RotationRandimize.RandomPerlin) {
				float yawf = yaw.getYaw() + noice(rotationRandomizeMultiply.get());
				float pitchf = yaw.getPitch() + noice(rotationRandomizeMultiply.get());
				yaw.setYaw(ThreadLocalRandom.current().nextFloat(yawf - rotationRandomizeMultiply.get(), yawf + rotationRandomizeMultiply.get()));;
				yaw.setPitch(ThreadLocalRandom.current().nextFloat(pitchf - rotationRandomizeMultiply.get(), pitchf + rotationRandomizeMultiply.get()));;
			}
			return yaw;
		}
		return null;
	}
	private RotationUtils.Rotation lastRotate = null;
	private void rotate(Entity target, Runnable callback) {
		RotationUtils.Rotation rotation2 = getRotate(target);
		if (rotation2 != null) {
			if (rotationTickSmooth.get() == RotationTickSmooth.Perlin) {
				int noice = noice(rotationTickSmoothMultiply.get());
				if (noice != 0) {
					lastRotate = rotation2;
					if (rotation.get() == RotationMode.Shady) {
						if (!ShadyRotation.running) {
							ShadyRotation.smoothLook(rotation2, rotationShadySpeed.get(), clientLook.get(), (() -> {}));
						}
					}
					else {
						Rotations.rotate(rotation2.getYaw(), rotation2.getPitch(), 0, clientLook.get(), null);
					}
					if (clientLook.get()) {
						mc.player.setPitch( rotation2.getPitch());
						mc.player.setYaw(rotation2.getYaw());
					}
				}
			}
			else if (rotationTickSmooth.get() == RotationTickSmooth.Random) {
				if (ThreadLocalRandom.current().nextBoolean()) {
					lastRotate = rotation2;
					if (rotation.get() == RotationMode.Shady) {
						if (!ShadyRotation.running) {
							ShadyRotation.smoothLook(rotation2, rotationShadySpeed.get(), clientLook.get(), (() -> {}));
						}
					}
					else {
						Rotations.rotate(rotation2.getYaw(), rotation2.getPitch(), 0, clientLook.get(), null);
					}
					if (clientLook.get()) {
						mc.player.setPitch( rotation2.getPitch());
						mc.player.setYaw(rotation2.getYaw());
					}
				}
			}
			else if (rotationTickSmooth.get() == RotationTickSmooth.RandomPerlin) {
				if (ThreadLocalRandom.current().nextBoolean() && noice(rotationTickSmoothMultiply.get()) != 0) {
					lastRotate = rotation2;
					if (rotation.get() == RotationMode.Shady) {
						if (!ShadyRotation.running) {
							ShadyRotation.smoothLook(rotation2, rotationShadySpeed.get(), clientLook.get(), (() -> {}));
						}
					}
					else {
						Rotations.rotate(rotation2.getYaw(), rotation2.getPitch(), 0, clientLook.get(), null);
					}
					if (clientLook.get()) {
						mc.player.setPitch( rotation2.getPitch());
						mc.player.setYaw(rotation2.getYaw());
					}
				}
			}
			else {
				lastRotate = rotation2;
				if (rotation.get() == RotationMode.Shady) {
					if (!ShadyRotation.running) {
						ShadyRotation.smoothLook(rotation2, rotationShadySpeed.get(), clientLook.get(), (() -> {}));
					}
				}
				else {
					Rotations.rotate(rotation2.getYaw(), rotation2.getPitch(), 0, clientLook.get(), null);
				}
				if (clientLook.get()) {
					mc.player.setPitch( rotation2.getPitch());
					mc.player.setYaw(rotation2.getYaw());
				}
			}
		}
		else if (lastRotate != null) {
			if (rotation.get() == RotationMode.Shady) {
				ShadyRotation.smoothLook(lastRotate, rotationShadySpeed.get(), clientLook.get(), (() -> {}));
			}
			else {
				Rotations.rotate(lastRotate.getYaw(), lastRotate.getPitch(), 0, clientLook.get(), null);
			}
			if (clientLook.get()) {
				mc.player.setPitch( rotation2.getPitch());
				mc.player.setYaw(rotation2.getYaw());
			}
		}
	}

	public Entity getTarget() {
		if (!targets.isEmpty()) return targets.get(0);
		return null;
	}

	@Override
	public String getInfoString() {
		if (!targets.isEmpty()) EntityUtils.getName(getTarget());
		return null;
	}
}
