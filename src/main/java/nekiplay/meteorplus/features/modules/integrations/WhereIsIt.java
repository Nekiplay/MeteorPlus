package nekiplay.meteorplus.features.modules.integrations;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import nekiplay.meteorplus.MeteorPlus;

public class WhereIsIt extends Module {
	public WhereIsIt() {
		super(MeteorPlus.CATEGORYMODS, "where-is-it", "ChestTracker modification.");
	}

	public SettingGroup defaultGroup = settings.getDefaultGroup();

	public final Setting<Boolean> background = defaultGroup.add(new BoolSetting.Builder()
		.name("draw-background")
		.build()
	);

	public final Setting<SettingColor> visible_text_color = defaultGroup.add(new ColorSetting.Builder()
		.name("Visible-text-color")
		.build()
	);

	public final Setting<SettingColor> notvisible_text_color = defaultGroup.add(new ColorSetting.Builder()
		.name("not-visible-text-color")
		.build()
	);
}
