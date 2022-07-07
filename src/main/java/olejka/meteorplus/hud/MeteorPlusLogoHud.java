package olejka.meteorplus.hud;

import olejka.meteorplus.MeteorPlus;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import net.minecraft.util.Identifier;

import static meteordevelopment.meteorclient.utils.Utils.WHITE;


public class MeteorPlusLogoHud extends HudElement {

	public static final HudElementInfo<MeteorPlusLogoHud> INFO = new HudElementInfo<>(MeteorPlus.HUD_GROUP, "MeteorPlusLogo", "Shows the Meteor Plus logo in the HUD.", MeteorPlusLogoHud::new);
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
		.name("scale")
		.description("The scale of the logo.")
		.defaultValue(3)
		.min(0.1)
		.sliderRange(0.1, 10)
		.build()
	);

	private final Setting<Boolean> invert = sgGeneral.add(new BoolSetting.Builder()
		.name("invert")
		.description("Invert the logo.")
		.defaultValue(false)
		.build()
	);

	private final Identifier TEXTURE = new Identifier("plus", "logo.png");


	public MeteorPlusLogoHud() {
		super(INFO);
		calculateSize();
	}

	public void calculateSize() {
		box.setSize(64 * scale.get(), 50 * scale.get());
	}

	@Override
	public void render(HudRenderer renderer) {
		GL.bindTexture(TEXTURE);
		Renderer2D.TEXTURE.begin();
		if (!invert.get()) {
			Renderer2D.TEXTURE.texQuad(box.x, box.y, 64 * scale.get(), 50 * scale.get(), WHITE);
		} else {
			Renderer2D.TEXTURE.texQuad(box.x+(64 * scale.get()), box.y, -(64 * scale.get()), 50 * scale.get(), WHITE);
		}
		Renderer2D.TEXTURE.render(null);
	}
}
