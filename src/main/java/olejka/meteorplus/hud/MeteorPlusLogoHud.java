package olejka.meteorplus.hud;

import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.modules.HudElement;
import net.minecraft.util.Identifier;

import static meteordevelopment.meteorclient.utils.Utils.WHITE;


public class MeteorPlusLogoHud extends HudElement {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
		.name("scale")
		.description("The scale of the logo.")
		.defaultValue(3)
		.min(0.1)
		.sliderRange(0.1, 10)
		.build()
	);

	private final Identifier TEXTURE = new Identifier("plus", "logo.png");

	public MeteorPlusLogoHud(HUD hud) {
		super(hud, "MeteorPlusLogo", "Shows the Meteor Plus logo in the HUD.");
	}

	@Override
	public void update(HudRenderer renderer) {
		box.setSize(64 * scale.get(), 50 * scale.get());
	}

	@Override
	public void render(HudRenderer renderer) {
		GL.bindTexture(TEXTURE);
		Renderer2D.TEXTURE.begin();
		Renderer2D.TEXTURE.texQuad(box.getX(), box.getY(), box.width, box.height, WHITE);
		Renderer2D.TEXTURE.render(null);
	}
}
