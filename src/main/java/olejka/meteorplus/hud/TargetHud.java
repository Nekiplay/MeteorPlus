package olejka.meteorplus.hud;

import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.modules.HudElement;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class TargetHud extends HudElement {
	public TargetHud(HUD hud) {
		super(hud, "TargetHud", "Shows the Meteor Plus logo in the HUD.");
	}
	private PlayerEntity playerEntity;

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	@Override
	public void update(HudRenderer renderer) {
		box.setSize(90 + getSize(),90 + getSize());
	}

	private int getSize() {
		return (int) (radius.get() * 2);
	}

	private final Setting<Double> radius = sgGeneral.add(new DoubleSetting.Builder()
		.name("scale")
		.description("The scale.")
		.defaultValue(100)
		.min(10)
		.sliderRange(70, 1000)
		.build()
	);

	private final Setting<Integer> sides = sgGeneral.add(new IntSetting.Builder()
		.name("sides")
		.description("The sides.")
		.defaultValue(90)
		.min(0)
		.build()
	);

	public static final double TWICE_PI = Math.PI*2;
	@Override
	public void render(HudRenderer renderer) {
		renderer.addPostTask(() -> {

			GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
			GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);

			double y = box.getY() + box.height / 2;
			double x = box.getX() + box.width / 2;

			Renderer2D.COLOR.begin();
			for(int i = 0; i <= sides.get() ;i++)
			{
				double angle = (TWICE_PI * i / sides.get()) + Math.toRadians(180);
				Renderer2D.COLOR.quad(x + Math.sin(angle) * radius.get(), y + Math.cos(angle) * radius.get(), 1, 1, Color.BLUE);
			}
			Renderer2D.COLOR.render(null);
			GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		});
	}
}
