package nekiplay.meteorplus.features.modules.combat.killaura;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.features.modules.combat.killaura.modes.LiquidBounceAura;
import nekiplay.meteorplus.utils.algoritms.Smooth;
import net.minecraft.entity.EntityType;

import java.util.Set;

public class KillAuraPlus extends Module {
	public KillAuraPlus() {
		super(Categories.Combat, "kill-aura+", "Bypass killaura.");
	}

	public final SettingGroup sgGeneral = settings.getDefaultGroup();
	public final SettingGroup sgTargeting = settings.createGroup("Targeting");
	public final SettingGroup sgDelay = settings.createGroup("Delay");

	public final Setting<KillAuraPlusModes> mode = sgGeneral.add(new EnumSetting.Builder<KillAuraPlusModes>()
		.name("mode")
		.description("KillAura mode.")
		.defaultValue(KillAuraPlusModes.LiquidBounce)
		.onModuleActivated(modesSetting -> onModeChanged(modesSetting.get()))
		.onChanged(this::onModeChanged)
		.build()
	);
	public final Setting<KillAura.Weapon> weapon = sgGeneral.add(new EnumSetting.Builder<KillAura.Weapon>()
		.name("weapon")
		.description("Only attacks an entity when a specified weapon is in your hand.")
		.defaultValue(KillAura.Weapon.Both)
		.build()
	);
	public final Setting<Boolean> autoSwitch = sgGeneral.add(new BoolSetting.Builder()
		.name("auto-switch")
		.description("Switches to your selected weapon when attacking the target.")
		.defaultValue(false)
		.build()
	);

	public final Setting<KillAura.ShieldMode> shieldMode = sgGeneral.add(new EnumSetting.Builder<KillAura.ShieldMode>()
		.name("shield-mode")
		.description("Will try and use an axe to break target shields.")
		.defaultValue(KillAura.ShieldMode.Break)
		.visible(() -> autoSwitch.get() && weapon.get() != KillAura.Weapon.Axe)
		.build()
	);

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

	public final Setting<Boolean> randomTeleport = sgGeneral.add(new BoolSetting.Builder()
		.name("random-teleport")
		.description("Randomly teleport around the target")
		.defaultValue(false)
		.build()
	);

	public final Setting<Boolean> revertKnockback = sgGeneral.add(new BoolSetting.Builder()
		.name("revert-knockback")
		.description("Revert enemy knockback")
		.defaultValue(false)
		.build()
	);

	public final Setting<RotationMode> rotation = sgGeneral.add(new EnumSetting.Builder<RotationMode>()
		.name("rotate")
		.description("Determines when you should rotate towards the target.")
		.defaultValue(RotationMode.LiquidBounce)
		.build()
	);

	public final Setting<Boolean> clientLook = sgGeneral.add(new BoolSetting.Builder()
		.name("client-look")
		.description("Client rotation")
		.defaultValue(false)
		.build()
	);

	public final Setting<Smooth.SmoothType> rotationSmooth = sgGeneral.add(new EnumSetting.Builder<Smooth.SmoothType>()
		.name("rotate-smooth")
		.description("Determines when you should rotate towards the target.")
		.defaultValue(Smooth.SmoothType.None)
		.visible(() -> rotation.get() != RotationMode.Instant && rotation.get() != RotationMode.None)
		.build()
	);

	public final Setting<Integer> rotationShadySpeed = sgGeneral.add(new IntSetting.Builder()
		.name("rotation-speed")
		.description("Speed.")
		.defaultValue(4)
		.range(1, 5)
		.sliderRange(1, 5)
		.visible(() -> rotation.get() == RotationMode.Shady)
		.build()
	);

	public final Setting<RotationRandimize> rotationRandomize = sgGeneral.add(new EnumSetting.Builder<RotationRandimize>()
		.name("rotation-randomize")
		.description("Rotation randomize.")
		.defaultValue(RotationRandimize.None)
		.visible(() -> rotationSmooth.get() != Smooth.SmoothType.None && rotationSmooth.isVisible())
		.build()
	);

	public final Setting<Integer> rotationRandomizeMultiply = sgGeneral.add(new IntSetting.Builder()
		.name("rotation-randomize-multiply")
		.description("Speed.")
		.defaultValue(4)
		.range(0, 32)
		.sliderRange(0, 32)
		.visible(() -> rotationRandomize.get() != RotationRandimize.None && rotationSmooth.isVisible())
		.build()
	);


	public final Setting<RotationTickSmooth> rotationTickSmooth = sgGeneral.add(new EnumSetting.Builder<RotationTickSmooth>()
		.name("rotation-tick-smooth")
		.description("Rotation randomize.")
		.defaultValue(RotationTickSmooth.None)
		.visible(() -> rotationSmooth.get() != Smooth.SmoothType.None && rotationSmooth.isVisible())
		.build()
	);

	public final Setting<Integer> rotationTickSmoothMultiply = sgGeneral.add(new IntSetting.Builder()
		.name("rotation-tick-smooth-multiply")
		.description("Speed.")
		.defaultValue(2)
		.range(0, 32)
		.sliderRange(0, 32)
		.visible(() -> rotationTickSmooth.get() != RotationTickSmooth.None && rotationTickSmooth.get() != RotationTickSmooth.Random && rotationSmooth.isVisible())
		.build()
	);


	public final Setting<Double> maxRotationSpeed = sgGeneral.add(new DoubleSetting.Builder()
		.name("max-rotation-speed")
		.description("Maximum rotation speed.")
		.defaultValue(180)
		.range(0, 180)
		.sliderRange(0, 180)
		.visible(() -> rotation.get() != RotationMode.None && rotation.get() != RotationMode.Shady && rotation.get() != RotationMode.Instant && rotationSmooth.isVisible())
		.build()
	);

	public final Setting<Double> minRotationSpeed = sgGeneral.add(new DoubleSetting.Builder()
		.name("min-rotation-speed")
		.description("Minimum rotation speed.")
		.defaultValue(180)
		.range(0, 180)
		.sliderRange(0, 180)
		.visible(() -> rotation.get() != RotationMode.None && rotation.get() != RotationMode.Shady && rotation.get() != RotationMode.Instant && rotationSmooth.isVisible())
		.build()
	);

	public final Setting<Boolean> rayTraceRotate = sgGeneral.add(new BoolSetting.Builder()
		.name("raytrace-rotate")
		.description("Not rotate if you head see player")
		.visible(() -> rotation.get() != RotationMode.Instant && rotation.get() != RotationMode.None && rotationSmooth.isVisible())
		.defaultValue(false)
		.build()
	);

	public final Setting<Boolean> rayTraceAttack = sgGeneral.add(new BoolSetting.Builder()
		.name("raytrace-attack")
		.description("Not attack if you head don't see player")
		.visible(() -> rotation.get() != RotationMode.Instant && rotation.get() != RotationMode.None && rotationSmooth.isVisible())
		.defaultValue(false)
		.build()
	);

	public final Setting<Double> rayTraceRotateBoxStretch = sgGeneral.add(new DoubleSetting.Builder()
		.name("raytrace-rotate-box-stretch")
		.description("raytrace-rotate-box-stretch.")
		.defaultValue(0.7)
		.range(-1, 1)
		.sliderRange(-1, 1)
		.visible(() -> rayTraceRotate.isVisible())
		.build()
	);

	public final Setting<Double> rayTraceAttackBoxStretch = sgGeneral.add(new DoubleSetting.Builder()
		.name("raytrace-attack-box-stretch")
		.description("raytrace-attack-box-stretch.")
		.defaultValue(0.7)
		.range(-1, 1)
		.sliderRange(-1, 1)
		.visible(() -> rayTraceAttack.isVisible())
		.build()
	);

	public final Setting<Boolean> shieldBreaker = sgGeneral.add(new BoolSetting.Builder()
		.name("shield-breaker")
		.description("Break enemy shield by axe")
		.defaultValue(true)
		.build()
	);

	public final Setting<Double> hitChance = sgGeneral.add(new DoubleSetting.Builder()
		.name("hit-chance")
		.description("The probability of your hits landing.")
		.defaultValue(100)
		.range(1, 100)
		.sliderRange(1, 100)
		.build()
	);

	// Targeting
	public final Setting<Set<EntityType<?>>> entities = sgTargeting.add(new EntityTypeListSetting.Builder()
		.name("entities")
		.description("Entities to attack.")
		.onlyAttackable()
		.build()
	);

	public final Setting<Double> fov = sgTargeting.add(new DoubleSetting.Builder()
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

	public final Setting<Double> range = sgTargeting.add(new DoubleSetting.Builder()
		.name("range")
		.description("The maximum range the entity can be to attack it.")
		.defaultValue(4.5)
		.min(0)
		.sliderMax(6)
		.build()
	);

	public final Setting<Double> wallsRange = sgTargeting.add(new DoubleSetting.Builder()
		.name("walls-range")
		.description("The maximum range the entity can be attacked through walls.")
		.defaultValue(3.5)
		.min(0)
		.sliderMax(6)
		.build()
	);

	public final Setting<SortPriority> priority = sgTargeting.add(new EnumSetting.Builder<SortPriority>()
		.name("priority")
		.description("How to filter targets within range.")
		.defaultValue(SortPriority.LowestHealth)
		.build()
	);

	public final Setting<Integer> maxTargets = sgTargeting.add(new IntSetting.Builder()
		.name("max-targets")
		.description("How many entities to target at once.")
		.defaultValue(1)
		.min(1)
		.sliderRange(1, 5)
		.build()
	);

	public final Setting<Boolean> babies = sgTargeting.add(new BoolSetting.Builder()
		.name("babies")
		.description("Whether or not to attack baby variants of the entity.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> nametagged = sgTargeting.add(new BoolSetting.Builder()
		.name("nametagged")
		.description("Whether or not to attack mobs with a name tag.")
		.defaultValue(false)
		.build()
	);

	// Delay

	public final Setting<Boolean> smartDelay = sgDelay.add(new BoolSetting.Builder()
		.name("smart-delay")
		.description("Uses the vanilla cooldown to attack entities.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Integer> hitDelay = sgDelay.add(new IntSetting.Builder()
		.name("hit-delay")
		.description("How fast you hit the entity in ticks.")
		.defaultValue(0)
		.min(0)
		.sliderMax(60)
		.visible(() -> !smartDelay.get())
		.build()
	);

	public final Setting<Boolean> randomDelayEnabled = sgDelay.add(new BoolSetting.Builder()
		.name("random-delay-enabled")
		.description("Adds a random delay between hits to attempt to bypass anti-cheats.")
		.defaultValue(false)
		.visible(() -> !smartDelay.get())
		.build()
	);

	public final Setting<Integer> randomDelayMax = sgDelay.add(new IntSetting.Builder()
		.name("random-delay-max")
		.description("The maximum value for random delay.")
		.defaultValue(4)
		.min(0)
		.sliderMax(20)
		.visible(() -> randomDelayEnabled.get() && !smartDelay.get())
		.build()
	);

	public final Setting<Integer> switchDelay = sgDelay.add(new IntSetting.Builder()
		.name("switch-delay")
		.description("How many ticks to wait before hitting an entity after switching hotbar slots.")
		.defaultValue(0)
		.min(0)
		.build()
	);


	private KillAuraPlusMode currentMode;

	private void onModeChanged(KillAuraPlusModes mode) {
		switch (mode) {
			case LiquidBounce -> {
				currentMode = new LiquidBounceAura();
			}
		}
	}
	@EventHandler
	private void onTick(TickEvent.Post event) {
		currentMode.onTick(event);
	}
	@EventHandler
	private void onSendPacket(PacketEvent.Send event) {
		currentMode.onSendPacket(event);
	}

	@Override
	public String getInfoString() {
		return currentMode.getInfoString();
	}
}
