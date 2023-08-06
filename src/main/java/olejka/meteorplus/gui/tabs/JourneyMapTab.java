package olejka.meteorplus.gui.tabs;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import olejka.meteorplus.MixinPlugin;

public class JourneyMapTab extends Tab {
	private static Settings settings;
	public JourneyMapTab() {
		super("Journey Map");
	}

	public static Settings getSettings() {
		if (settings != null) return settings;

		settings = new Settings();

		SettingGroup fullMap = settings.createGroup("Full map");
		fullMap.add(new BoolSetting.Builder()
			.name("Baritone goto in context menu")
			.defaultValue(true)
			.build()
		);
		fullMap.add(new BoolSetting.Builder()
			.name("Baritone goto in waypoints menu")
			.defaultValue(true)
			.build()
		);
		return settings;
	}

	@Override
	public TabScreen createScreen(GuiTheme theme) {
		return new Screen(theme, this);
	}

	@Override
	public boolean isScreen(net.minecraft.client.gui.screen.Screen screen) {
		return screen instanceof Screen;
	}

	public static class Screen extends WindowTabScreen {
		public Screen(GuiTheme theme, Tab tab) {
			super(theme, tab);
		}

		@Override
		public void initWidgets() {
			WTextBox filter = add(theme.textBox("")).minWidth(400).expandX().widget();
			filter.setFocused(true);
			filter.action = () -> {
				clear();
				add(filter);
				add(theme.settings(getSettings(), filter.get().trim())).expandX();
			};

			add(theme.settings(getSettings(), filter.get().trim())).expandX();
		}
	}
}
