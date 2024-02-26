package nekiplay.meteorplus.features.modules.movement;

import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.events.PlayerUseMultiplierEvent;

public class NoSlowPlus extends Module {
	public NoSlowPlus() {
		super(Categories.Movement, "no-slow+", "Remove or increase slowness.");
	}


	public SettingGroup usingItemGroup = settings.createGroup("Using item");
	public SettingGroup sneakGroup = settings.createGroup("Sneak");
	public SettingGroup defaultGroup = settings.getDefaultGroup();

	private final Setting<Double> usingForward = usingItemGroup.add(new DoubleSetting.Builder()
		.name("forward-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.build()
	);

	private final Setting<Double> usingSideways = usingItemGroup.add(new DoubleSetting.Builder()
		.name("sideways-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.build()
	);

	private final Setting<Double> sneakForward = sneakGroup.add(new DoubleSetting.Builder()
		.name("forward-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.build()
	);

	private final Setting<Double> sneakSideways = sneakGroup.add(new DoubleSetting.Builder()
		.name("sideways-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.build()
	);

	private final Setting<Double> otherForward = defaultGroup.add(new DoubleSetting.Builder()
		.name("forward-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.build()
	);

	private final Setting<Double> otherSideways = defaultGroup.add(new DoubleSetting.Builder()
		.name("sideways-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.build()
	);

	@EventHandler
	private void onUse(PlayerUseMultiplierEvent event) {
		if (mc.player.isUsingItem()) {
			event.setForward(usingForward.get().floatValue());
			event.setSideways(usingSideways.get().floatValue());
		}
		if (mc.player.isSneaking()) {
			event.setForward(sneakForward.get().floatValue());
			event.setSideways(sneakSideways.get().floatValue());
		}
		else {
			event.setForward(otherForward.get().floatValue());
			event.setSideways(otherSideways.get().floatValue());
		}
	}
}
