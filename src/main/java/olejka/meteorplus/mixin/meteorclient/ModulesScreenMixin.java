package olejka.meteorplus.mixin.meteorclient;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.ModulesScreen;
import meteordevelopment.meteorclient.gui.themes.meteor.widgets.WMeteorModule;
import meteordevelopment.meteorclient.gui.themes.meteor.widgets.WMeteorWindow;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import olejka.meteorplus.mixininterface.meteorclient.IModule;
import olejka.meteorplus.mixininterface.meteorclient.IWMeteorModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ModulesScreen.class)
public abstract class ModulesScreenMixin {
	@Shadow(remap = false)
	protected abstract void createSearchW(WContainer w, String text);

	private GuiTheme _theme;

	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	private void ModulesScreen(GuiTheme theme, CallbackInfo ci) {
		_theme = theme;
	}

	@Inject(method = "initWidgets", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
	private void initWidgets(CallbackInfo ci, WVerticalList help) {
		help.add(_theme.label("Middle click - Hide module"));
	}

	@Inject(method = "createCategory", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), remap = false, locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void createCategory(WContainer c, Category category, CallbackInfoReturnable<WWindow> cir, WWindow w) {
		for (Module module : Modules.get().getGroup(category)) {
			boolean isVisible = !((IModule) module).isHidden();
			if (isVisible) {
				w.add(_theme.module(module)).expandX();
			}
		}
		cir.setReturnValue(w);
	}

	@Inject(method = "createSearch", at = @At(value = "FIELD", target = "Lmeteordevelopment/meteorclient/gui/widgets/input/WTextBox;action:Ljava/lang/Runnable;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
	private void createSerach(WContainer c, CallbackInfoReturnable<WWindow> cir, WWindow w, WVerticalList l, WTextBox text) {
		text.action = () -> {
			l.clear();
			createSearchW(l, text.get());
			for (Cell<?> screenCell : c.cells) {
				WWidget wwindow = screenCell.widget();
				if (wwindow instanceof WMeteorWindow) {
					for (Cell<?> windowCell : ((WMeteorWindow) wwindow).view.cells) {
						WWidget wmodule = windowCell.widget();
						if (wmodule instanceof WMeteorModule) {
							Module module = ((IWMeteorModule) wmodule).getModule();
							Color textColor = wmodule.theme.textColor().copy().a(50);
							if (module.title.toLowerCase().contains(text.get().toLowerCase())) {
								textColor = wmodule.theme.textColor();
							}
							((IWMeteorModule) wmodule).setColor(textColor);
						}
					}
				}
			}
		};
	}
}
