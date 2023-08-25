package nekiplay.meteorplus.features.modules.autoobsidianmine;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlus;
import nekiplay.meteorplus.features.modules.autoobsidianmine.modes.Cauldrons;
import nekiplay.meteorplus.features.modules.autoobsidianmine.modes.Portals;
import net.minecraft.util.math.BlockPos;

public class AutoObsidianFarm extends Module {
	public AutoObsidianFarm() {
		super(MeteorPlus.CATEGORY, "Auto-obsidian-mine", "Automatically mine obsidian.");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public final Setting<AutoObsidianFarmModes> workingMode = sgGeneral.add(new EnumSetting.Builder<AutoObsidianFarmModes>()
		.name("mode")
		.description("Working mode.")
		.defaultValue(AutoObsidianFarmModes.Portals_Vanila)
		.onModuleActivated(modesSetting -> onModeChanged(modesSetting.get()))
		.onChanged(this::onModeChanged)
		.build()
	);

	public final Setting<BlockPos> mainPortalPosition = sgGeneral.add(new BlockPosSetting.Builder()
		.name("portal location 1")
		.description("the position of the portal to hell")
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portals_Vanila)
		.build()
	);

	public final Setting<BlockPos> twoPortalPosition = sgGeneral.add(new BlockPosSetting.Builder()
		.name("portal location 2")
		.description("portal position in hell for new portal generations")
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portals_Vanila)
		.build()
	);

	public final Setting<BlockPos> lavaPlaceLocation = sgGeneral.add(new BlockPosSetting.Builder()
		.name("lava-place-location")
		.description("the position placing lava")
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Cauldrons)
		.build()
	);

	public final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
		.name("command")
		.description("Send command.")
		.defaultValue("/home")
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portal_Homes)
		.build()
	);

	public final Setting<Integer> delayCommand = sgGeneral.add(new IntSetting.Builder()
		.name("command-delay")
		.description("Ticks delay.")
		.defaultValue(700)
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portal_Homes)
		.build()
	);

	public final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
		.name("Mining-delay")
		.description("Mining delay.")
		.defaultValue(4)
		.build()
	);

	public final Setting<Integer> collectDelay = sgGeneral.add(new IntSetting.Builder()
		.name("Collect-delay")
		.description("Cauldron collecting lava delay.")
		.defaultValue(4)
		.build()
	);

	public final Setting<Boolean> noBaritoneBreaking = sgGeneral.add(new BoolSetting.Builder()
		.name("disable-baritone-breaking-if-not-mine-portal")
		.description("No break blocks if is not mining portal.")
		.defaultValue(true)
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portals_Vanila)
		.build()
	);

	public final Setting<Boolean> noBaritonePlacing = sgGeneral.add(new BoolSetting.Builder()
		.name("disable-baritone-place")
		.description("No place blocks.")
		.defaultValue(true)
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portals_Vanila)
		.build()
	);

	public final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
		.name("rotate")
		.description("Rotate to breaking block.")
		.defaultValue(false)
		.visible(() -> workingMode.get() != AutoObsidianFarmModes.Cauldrons)
		.build()
	);

	public final Setting<Boolean> swingHand = sgGeneral.add(new BoolSetting.Builder()
		.name("swing-hand")
		.description("Swing hand client side.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
		.name("range")
		.description("Cauldron range's.")
		.defaultValue(4)
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Cauldrons)
		.build()
	);

	private AutoObsidianFarmMode currentMode;

	private void onModeChanged(AutoObsidianFarmModes mode) {
		switch (mode) {
			case Portal_Homes, Portals_Vanila -> {
				currentMode = new Portals();
			}
			case Cauldrons ->  {
				currentMode = new Cauldrons();
			}
		}
	}

	@Override
	public void onActivate() {
		currentMode.onActivate();
	}

	@Override
	public void onDeactivate() {
		currentMode.onDeactivate();
	}

	@EventHandler
	private void onPreTick(TickEvent.Pre event) {
		currentMode.onTickEventPre(event);
	}
}
