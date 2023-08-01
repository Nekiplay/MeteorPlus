package olejka.meteorplus.gui.tabs;


import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.screen.Screen;
import olejka.meteorplus.mixininterface.meteorclient.IModule;

public class HiddenModulesTab extends Tab {
	public HiddenModulesTab() {
		super("Hidden modules");
	}

	@Override
	public TabScreen createScreen(GuiTheme theme) {
		return new HiddenModulesScreen(theme, this);
	}

	@Override
	public boolean isScreen(Screen screen) {
		return screen instanceof HiddenModulesScreen;
	}



	public static class HiddenModulesScreen extends WindowTabScreen {
		public HiddenModulesScreen(GuiTheme theme, Tab tab) {
			super(theme, tab);
		}

		@Override
		public void initWidgets() {
			for (Category category : Modules.loopCategories()) {
				for (Module module : Modules.get().getGroup(category)) {
					boolean isVisible = !((IModule) module).isHidden();
					if (isVisible) continue;

					WButton moduleButton = theme.button(module.title);
					moduleButton.action = () -> {
						((IModule) module).setHidden(false);
						reload();
					};

					add(moduleButton).expandX();
				}
			}
		}
	}
}
