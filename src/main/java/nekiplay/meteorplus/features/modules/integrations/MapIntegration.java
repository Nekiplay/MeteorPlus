package nekiplay.meteorplus.features.modules.integrations;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import nekiplay.meteorplus.MeteorPlus;
import nekiplay.meteorplus.MixinPlugin;

public class MapIntegration extends Module {
	public MapIntegration() {
		super(MeteorPlus.CATEGORYMODS, "Mini-map", "Improvements for mods on mini-maps.");
	}

	private final SettingGroup baritoneIntegration = settings.createGroup("Baritone");

	public final Setting<Boolean> baritoneGoto = baritoneIntegration.add(new BoolSetting.Builder()
		.name("Baritone goto")
		.description("Moving in baritone at the selected location.")
		.defaultValue(true)
		.build()
	);

	private final SettingGroup fullMap = settings.createGroup("Full map");

	public final Setting<Boolean> showBlock = fullMap.add(new BoolSetting.Builder()
		.name("Show block")
		.description("Shows the name of the block in the clicked position.")
		.visible(() -> MixinPlugin.isXaeroWorldMapresent)
		.defaultValue(true)
		.build()
	);
}
