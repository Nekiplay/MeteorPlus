package olejka.meteorplus.modules.killaura;

import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import olejka.meteorplus.MeteorPlus;

public class KillAuraPlus extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public KillAuraPlus() {
		super(MeteorPlus.CATEGORY, "killaura-plus", "Bypass killaura");
		//onSpeedModeChanged(speedMode.get());
	}
}
