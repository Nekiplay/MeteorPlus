package nekiplay.meteorplus.gui.tabs;


import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.screen.Screen;
import nekiplay.meteorplus.mixinclasses.IModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

		public class GuiCategory
		{
			public boolean hasHidenModules;
			public Category category;
		}

		private List<GuiCategory> categoryList = new ArrayList<>();

		@Override
		public void initWidgets() {
			categoryList.clear();
			for (Category category : Modules.loopCategories()) {
				GuiCategory guiCategory = new GuiCategory();
				guiCategory.category = category;
				for (Module module : Modules.get().getGroup(category)) {
					boolean isVisible = !((IModule) module).isHidden();
					if (isVisible) continue;
					guiCategory.hasHidenModules = true;
					categoryList.add(guiCategory);
					break;
				}
			}
			for (GuiCategory guiCategory : categoryList) {
				WSection list = theme.section(guiCategory.category.name, false);

				Cell<WSection> modulesCell = add(list).expandX();

				WTable table = list.add(theme.table()).expandX().widget();

				for (Module module : Modules.get().getGroup(guiCategory.category)) {
					boolean isVisible = !((IModule) module).isHidden();
					if (isVisible) continue;

					WButton moduleButton = theme.button(module.title);
					moduleButton.action = () -> {
						((IModule) module).setHidden(false);
						reload();
					};
					table.add(moduleButton).expandCellX().center().widget();
					table.row();
				}
			}
		}
	}
}
