package nekiplay.meteorplus.settings;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import nekiplay.meteorplus.mixinclasses.SpoofMode;

public class ConfigModifier {
	private static ConfigModifier INSTANCE;

	public final SettingGroup sgMeteorPlus = Config.get().settings.createGroup("Meteor+");
	public final Setting<Boolean> positionProtection = sgMeteorPlus.add(new BoolSetting.Builder()
		.name("position-protection")
		.description("Set fake position in F3 and in mods.")
		.defaultValue(false)
		.build()
	);

	public final Setting<SpoofMode> spoofMode = sgMeteorPlus.add(new EnumSetting.Builder<SpoofMode>()
		.name("protection-mode")
		.defaultValue(SpoofMode.Sensor)
		.visible(positionProtection::get)
		.build()
	);

	public final Setting<Integer> x_spoof = sgMeteorPlus.add(new IntSetting.Builder()
		.name("x-add")
		.defaultValue(100000)
		.visible(() -> positionProtection.get() && spoofMode.get() == SpoofMode.Fake)
		.build()
	);

	public final Setting<Integer> z_spoof = sgMeteorPlus.add(new IntSetting.Builder()
		.name("z-add")
		.defaultValue(100000)
		.visible(() -> positionProtection.get() && spoofMode.get() == SpoofMode.Fake)
		.build()
	);

	public static ConfigModifier get() {
		if (INSTANCE == null) INSTANCE = new ConfigModifier();
		return INSTANCE;
	}
}
