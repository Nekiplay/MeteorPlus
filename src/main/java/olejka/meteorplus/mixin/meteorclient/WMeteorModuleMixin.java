package olejka.meteorplus.mixin.meteorclient;

import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.themes.meteor.widgets.WMeteorModule;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import olejka.meteorplus.mixininterface.meteorclient.IModule;
import olejka.meteorplus.mixininterface.meteorclient.IWMeteorModule;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(WMeteorModule.class)
public class WMeteorModuleMixin implements IWMeteorModule {
	@Shadow(remap = false)
	@Final
	private Module module;

	WMeteorModule _this = (WMeteorModule) (Object) this;
	Color textColor;

	@Inject(method = "onPressed", at = @At("TAIL"), remap = false)
	private void onPressed(int button, CallbackInfo ci) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
			if (module.isActive()) module.toggle();

			((IModule) module).setHidden(true);
			Tabs.get().get(0).openScreen(GuiThemes.get()); // Hacky way to refresh the screen
		}
	}

	@ModifyArgs(method = "onRender", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/gui/renderer/GuiRenderer;text(Ljava/lang/String;DDLmeteordevelopment/meteorclient/utils/render/color/Color;Z)V"), remap = false)
	private void onTextRender(Args args) {
		if (textColor != null) {
			args.set(3, textColor);
		}
	}

	@Override
	public Module getModule() {
		return module;
	}

	@Override
	public void setColor(Color color) {
		textColor = color;
	}
}
