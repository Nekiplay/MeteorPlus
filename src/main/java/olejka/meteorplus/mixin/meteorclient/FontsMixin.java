package olejka.meteorplus.mixin.meteorclient;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.CustomFontChangedEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.systems.config.Config;
import olejka.meteorplus.mixinclasses.CustomTextRendererV2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.renderer.Fonts.DEFAULT_FONT;
import static meteordevelopment.meteorclient.renderer.Fonts.RENDERER;

@Mixin(Fonts.class)
public class FontsMixin {
	@Inject(method = "load", at = @At(value = "HEAD"), remap = false)
	private static void load(FontFace fontFace, CallbackInfo ci) {
		if (RENDERER != null && RENDERER.fontFace.equals(fontFace)) return;

		try {
			RENDERER = new CustomTextRendererV2(fontFace);
			MeteorClient.EVENT_BUS.post(CustomFontChangedEvent.get());
		}
		catch (Exception e) {
			if (fontFace.equals(DEFAULT_FONT)) {
				throw new RuntimeException("Failed to load default font: " + fontFace, e);
			}

			MeteorClient.LOG.error("Failed to load font: " + fontFace, e);
			load(DEFAULT_FONT, ci);
		}

		if (mc.currentScreen instanceof WidgetScreen && Config.get().customFont.get()) {
			((WidgetScreen) mc.currentScreen).invalidate();
		}
	}
}
