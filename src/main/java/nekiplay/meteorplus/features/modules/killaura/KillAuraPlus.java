package nekiplay.meteorplus.features.modules.killaura;

import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import nekiplay.meteorplus.MeteorPlus;
import nekiplay.meteorplus.features.modules.killaura.modes.FDPAura;
import nekiplay.meteorplus.features.modules.velocity.VelocityMode;
import nekiplay.meteorplus.features.modules.velocity.VelocityModes;
import nekiplay.meteorplus.features.modules.velocity.modes.Grim;

public class KillAuraPlus extends Module {
	public KillAuraPlus() {
		super(MeteorPlus.CATEGORY, "killaura-plus", "Bypass killaura.");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<KillAuraPlusModes> mode = sgGeneral.add(new EnumSetting.Builder<KillAuraPlusModes>()
		.name("mode")
		.description("KillAura mode.")
		.defaultValue(KillAuraPlusModes.FDP)
		.onModuleActivated(modesSetting -> onModeChanged(modesSetting.get()))
		.onChanged(this::onModeChanged)
		.build()
	);

	public final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
		.name("range")
		.defaultValue(3.7)
		.max(8)
		.build()
	);

	public final Setting<Double> thoughtWallsRange = sgGeneral.add(new DoubleSetting.Builder()
		.name("through-walls-range")
		.defaultValue(1.5)
		.max(8)
		.build()
	);


	private KillAuraPlusMode currentMode;

	private void onModeChanged(KillAuraPlusModes mode) {
		switch (mode) {
			case FDP -> {
				currentMode = new FDPAura();
			}
		}
	}
}
