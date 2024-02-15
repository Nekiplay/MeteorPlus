package nekiplay.meteorplus.features.modules;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlus;
import nekiplay.meteorplus.events.PlayerUseMultiplier;

public class NoSlowPlus extends Module {
	public NoSlowPlus() {
		super(MeteorPlus.CATEGORY, "no-slow-plus", "Remove jump delay.");
	}

	public SettingGroup defaultGroup = settings.getDefaultGroup();

	private final Setting<Double> forward = defaultGroup.add(new DoubleSetting.Builder()
		.name("Forward multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.build()
	);

	private final Setting<Double> sideways = defaultGroup.add(new DoubleSetting.Builder()
		.name("Sideways multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.build()
	);

	@EventHandler
	private void onUse(PlayerUseMultiplier event) {
		event.setForward(forward.get().floatValue());
		event.setSideways(sideways.get().floatValue());
	}
}
