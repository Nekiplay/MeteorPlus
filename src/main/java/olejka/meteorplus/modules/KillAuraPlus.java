package olejka.meteorplus.modules;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
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
import net.minecraft.world.GameMode;
import olejka.meteorplus.MeteorPlus;


import java.util.ArrayList;
import java.util.List;

public class KillAuraPlus extends Module {
	public KillAuraPlus() {
		super(MeteorPlus.CATEGORY, "killaura-plus", "Better killaura on player.");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgTargeting = settings.createGroup("Targeting");
	private final SettingGroup sgDelay = settings.createGroup("Delay");

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
		.defaultValue(RotationMode.Always)
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
	private boolean wasPathing;

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

			if (rotation.get() == RotationMode.Always) rotate(primary, null);

			if (primary instanceof PlayerEntity primaryPlayer) {
				if (primaryPlayer.isBlocking()) {
					FindItemResult axeResult = InvUtils.findInHotbar(itemStack -> {
						Item item = itemStack.getItem();
						return item instanceof AxeItem;
					});
					if (axeResult.found()) {
						InvUtils.swap(axeResult.slot(), false);
						if (itemInHand(mc.player.getInventory().getStack(axeResult.slot()).getItem())) return;
					}
					targets.forEach(this::attack);
				} else {
					FindItemResult swordResult = InvUtils.findInHotbar(itemStack -> {
						Item item = itemStack.getItem();
						return item instanceof SwordItem;
					});
					if (swordResult.found()) {
						InvUtils.swap(swordResult.slot(), false);
						if (itemInHand(mc.player.getInventory().getStack(swordResult.slot()).getItem())) return;
					}
					if (delayCheck()) targets.forEach(this::attack);

					if (randomTeleport.get()) {
						mc.player.setPosition(primary.getX() + randomOffset(), primary.getY(), primary.getZ() + randomOffset());
					}
				}
			}
			else {
				if (delayCheck()) targets.forEach(this::attack);
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
		float yaw = mc.player.getYaw();
		float pitch = mc.player.getPitch();
		if (revertKnockback.get()) {
			Rotations.rotate(Rotations.getYaw(target) - 180, Rotations.getPitch(target, Target.Body), null);
		}
		if(mc.interactionManager != null) {
			mc.interactionManager.attackEntity(mc.player, target);
			mc.player.swingHand(Hand.MAIN_HAND);
		}
		if (revertKnockback.get()) {
			Rotations.rotate(yaw, pitch, null);
		}
	}

	private void rotate(Entity target, Runnable callback) {
		Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target, Target.Body), callback);
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

	public enum RotationMode {
		Always,
		OnHit,
		None
	}
}
