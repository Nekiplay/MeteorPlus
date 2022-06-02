package olejka.meteorplus.hud;

import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.modules.HudElement;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import static meteordevelopment.meteorclient.utils.render.color.Color.WHITE;

public class CustomImageHud extends HudElement {
	public CustomImageHud(HUD hud) {
		super(hud, "Custom image", "Custom image hud.");
		loadImage(link.get());
	}

	private static final Identifier TEXID = new Identifier("plus", "logo3");
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<String> link = sgGeneral.add(new StringSetting.Builder()
		.name("Link")
		.description("Image link.")
		.defaultValue("https://i.ibb.co/khQw7B4/comhiclipartyaiob-removebg-preview.png")
		.onChanged((a) -> loadImage(a))
		.build()
	);
	private final Setting<Double> imgWidth = sgGeneral.add(new DoubleSetting.Builder()
		.name("width")
		.description("The scale of the image.")
		.defaultValue(100)
		.min(10)
		.sliderRange(70, 1000)
		.build()
	);

	private final Setting<Double> imgHeight = sgGeneral.add(new DoubleSetting.Builder()
		.name("height")
		.description("The scale of the image.")
		.defaultValue(100)
		.min(10)
		.sliderRange(70, 1000)
		.build()
	);

	private final Setting<Boolean> onInventory = sgGeneral.add(new BoolSetting.Builder()
		.name("Only-inventory")
		.description("Work in inventory.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> noChat = sgGeneral.add(new BoolSetting.Builder()
		.name("No-chat")
		.description("Not work in chat.")
		.defaultValue(true)
		.visible(onInventory::get)
		.build()
	);

	@Override
	public void update(HudRenderer renderer) {
		box.setSize(imgWidth.get(), imgHeight.get());
	}

	@Override
	public void render(HudRenderer renderer) {
		if (empty) {
			loadImage(link.get());
			return;
		}
		if ((onInventory.get() && mc != null && mc.currentScreen != null) || isInEditor()) {
			if (noChat.get() && !isInEditor()) {
				assert mc != null;
				if (mc.currentScreen instanceof ChatScreen) return;
			}
			GL.bindTexture(TEXID);
			Renderer2D.TEXTURE.begin();
			Renderer2D.TEXTURE.texQuad(box.getX(), box.getY(), imgWidth.get(), imgHeight.get(), WHITE);
			Renderer2D.TEXTURE.render(null);
		}
		else if (!onInventory.get()) {
			GL.bindTexture(TEXID);
			Renderer2D.TEXTURE.begin();
			Renderer2D.TEXTURE.texQuad(box.getX(), box.getY(), imgWidth.get(), imgHeight.get(), WHITE);
			Renderer2D.TEXTURE.render(null);
		}
	}

	private boolean locked = false;
	private boolean empty = true;
	private void loadImage(String url) {
		if (locked) {
			return;
		}
		new Thread(() -> {
			try {
				locked = true;
				var img = NativeImage.read(Http.get(url).sendInputStream());
				mc.getTextureManager().registerTexture(TEXID, new NativeImageBackedTexture(img));
				empty = false;
			} catch (Exception ignored) {
				empty = true;
			} finally {
				locked = false;
			}
		}).start();
	}
}
