package nekiplay.meteorplus.features.modules.combat.killaura;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.features.modules.combat.killaura.modes.VanilaPlus;
import net.minecraft.entity.EntityType;

import java.util.ArrayList;
import java.util.Set;

public class KillAuraPlus extends Module {
	public KillAuraPlus() {
		super(Categories.Combat, "kill-aura+", "Better killaura.");
	}

	public final SettingGroup sgGeneral = settings.getDefaultGroup();

	public final Setting<KillAuraPlusModes> mode = sgGeneral.add(new EnumSetting.Builder<KillAuraPlusModes>()
		.name("mode")
		.description("KillAura mode.")
		.defaultValue(KillAuraPlusModes.LiquidBounce)
		.onModuleActivated(modesSetting -> onModeChanged(modesSetting.get()))
		.onChanged(this::onModeChanged)
		.build()
	);

	private final SettingGroup sgTargeting = settings.createGroup("Targeting");
	private final SettingGroup sgTiming = settings.createGroup("Timing");

	// General

	private final Setting<KillAura.Weapon> weapon = sgGeneral.add(new EnumSetting.Builder<KillAura.Weapon>()
		.name("weapon")
		.description("Only attacks an entity when a specified weapon is in your hand.")
		.defaultValue(KillAura.Weapon.Both)
		.build()
	);

	private final Setting<KillAura.RotationMode> rotation = sgGeneral.add(new EnumSetting.Builder<KillAura.RotationMode>()
		.name("rotate")
		.description("Determines when you should rotate towards the target.")
		.defaultValue(KillAura.RotationMode.Always)
		.build()
	);

	private final Setting<Boolean> autoSwitch = sgGeneral.add(new BoolSetting.Builder()
		.name("auto-switch")
		.description("Switches to your selected weapon when attacking the target.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> onlyOnClick = sgGeneral.add(new BoolSetting.Builder()
		.name("only-on-click")
		.description("Only attacks when holding left click.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> onlyOnLook = sgGeneral.add(new BoolSetting.Builder()
		.name("only-on-look")
		.description("Only attacks when looking at an entity.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> pauseOnCombat = sgGeneral.add(new BoolSetting.Builder()
		.name("pause-baritone")
		.description("Freezes Baritone temporarily until you are finished attacking the entity.")
		.defaultValue(true)
		.build()
	);

	private final Setting<KillAura.ShieldMode> shieldMode = sgGeneral.add(new EnumSetting.Builder<KillAura.ShieldMode>()
		.name("shield-mode")
		.description("Will try and use an axe to break target shields.")
		.defaultValue(KillAura.ShieldMode.Break)
		.visible(() -> autoSwitch.get() && weapon.get() != KillAura.Weapon.Axe)
		.build()
	);

	// Targeting

	private final Setting<Set<EntityType<?>>> entities = sgTargeting.add(new EntityTypeListSetting.Builder()
		.name("entities")
		.description("Entities to attack.")
		.onlyAttackable()
		.defaultValue(EntityType.PLAYER)
		.build()
	);

	private final Setting<SortPriority> priority = sgTargeting.add(new EnumSetting.Builder<SortPriority>()
		.name("priority")
		.description("How to filter targets within range.")
		.defaultValue(SortPriority.ClosestAngle)
		.build()
	);

	private final Setting<Integer> maxTargets = sgTargeting.add(new IntSetting.Builder()
		.name("max-targets")
		.description("How many entities to target at once.")
		.defaultValue(1)
		.min(1)
		.sliderRange(1, 5)
		.visible(() -> !onlyOnLook.get())
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

	private final Setting<KillAura.EntityAge> mobAgeFilter = sgTargeting.add(new EnumSetting.Builder<KillAura.EntityAge>()
		.name("mob-age-filter")
		.description("Determines the age of the mobs to target (baby, adult, or both).")
		.defaultValue(KillAura.EntityAge.Adult)
		.build()
	);

	private final Setting<Boolean> ignoreNamed = sgTargeting.add(new BoolSetting.Builder()
		.name("ignore-named")
		.description("Whether or not to attack mobs with a name.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> ignorePassive = sgTargeting.add(new BoolSetting.Builder()
		.name("ignore-passive")
		.description("Will only attack sometimes passive mobs if they are targeting you.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> ignoreTamed = sgTargeting.add(new BoolSetting.Builder()
		.name("ignore-tamed")
		.description("Will avoid attacking mobs you tamed.")
		.defaultValue(false)
		.build()
	);

	// Timing

	private final Setting<Boolean> pauseOnLag = sgTiming.add(new BoolSetting.Builder()
		.name("pause-on-lag")
		.description("Pauses if the server is lagging.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> pauseOnUse = sgTiming.add(new BoolSetting.Builder()
		.name("pause-on-use")
		.description("Does not attack while using an item.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> pauseOnCA = sgTiming.add(new BoolSetting.Builder()
		.name("pause-on-CA")
		.description("Does not attack while CA is placing.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> tpsSync = sgTiming.add(new BoolSetting.Builder()
		.name("TPS-sync")
		.description("Tries to sync attack delay with the server's TPS.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> customDelay = sgTiming.add(new BoolSetting.Builder()
		.name("custom-delay")
		.description("Use a custom delay instead of the vanilla cooldown.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Integer> hitDelay = sgTiming.add(new IntSetting.Builder()
		.name("hit-delay")
		.description("How fast you hit the entity in ticks.")
		.defaultValue(11)
		.min(0)
		.sliderMax(60)
		.visible(customDelay::get)
		.build()
	);

	private final Setting<Integer> switchDelay = sgTiming.add(new IntSetting.Builder()
		.name("switch-delay")
		.description("How many ticks to wait before hitting an entity after switching hotbar slots.")
		.defaultValue(0)
		.min(0)
		.sliderMax(10)
		.build()
	);




	private KillAuraPlusMode currentMode;

	private void onModeChanged(KillAuraPlusModes mode) {
		switch (mode) {
			case VanilaPlus -> currentMode = new VanilaPlus();
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
	public void onDeactivate() {
		currentMode.onDeactivate();
	}

	@Override
	public void onActivate() {
		currentMode.onActivate();
	}

	@Override
	public String getInfoString() {
		return currentMode.getInfoString();
	}
}
