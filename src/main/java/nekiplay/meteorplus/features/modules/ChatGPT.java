package nekiplay.meteorplus.features.modules;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import nekiplay.meteorplus.MeteorPlus;

public class ChatGPT extends Module {
	public ChatGPT() {
		super(MeteorPlus.CATEGORY, "Chat GPT", "Use chat gpt in minecraft");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public final Setting<String> token = sgGeneral.add(new StringSetting.Builder()
		.name("Token")
		.description("Token from NovaAI.")
		.defaultValue("")
		.build()
	);
}
