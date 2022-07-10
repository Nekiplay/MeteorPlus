package olejka.meteorplus.hud;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import olejka.meteorplus.MeteorPlus;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.render.color.Color.WHITE;

public class CustomImage extends HudElement  {

	public static final HudElementInfo<CustomImage> INFO = new HudElementInfo<>(MeteorPlus.HUD_GROUP, "CustomImage", "Shows the MeteorPlus logo in the HUD.", CustomImage::new);

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<String> link = sgGeneral.add(new StringSetting.Builder()
		.name("Link")
		.description("Image link.")
		.defaultValue("https://i.ibb.co/khQw7B4/comhiclipartyaiob-removebg-preview.png")
		.onChanged(this::loadImage)
		.build()
	);

	private final Setting<Double> imgWidth = sgGeneral.add(new DoubleSetting.Builder()
		.name("width")
		.description("The scale of the image.")
		.defaultValue(100)
		.min(10)
		.onChanged((size) -> calculateSize())
		.sliderRange(70, 1000)
		.build()
	);

	private static final Identifier TEXID = new Identifier("plus", "logo2");

	private final Setting<Double> imgHeight = sgGeneral.add(new DoubleSetting.Builder()
		.name("height")
		.description("The scale of the image.")
		.defaultValue(100)
		.min(10)
		.onChanged((size) -> calculateSize())
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

	public CustomImage() {
		super(INFO);
		calculateSize();
	}

	public void calculateSize() {
		box.setSize(imgWidth.get(), imgHeight.get());
	}

	@Override
	public void render(HudRenderer renderer) {
		if (empty) {
			loadImage(link.get());
			return;
		}
		if ((onInventory.get() && mc != null && mc.currentScreen != null) || isInEditor()) {
			if (noChat.get() && !isInEditor() && mc.currentScreen instanceof ChatScreen) return;
			renderer.texture(TEXID, box.getRenderX(), box.getRenderY(), imgWidth.get(), imgHeight.get(), WHITE);
		}
		else if (!onInventory.get()) {
			renderer.texture(TEXID, box.getRenderX(), box.getRenderY(), imgWidth.get(), imgHeight.get(), WHITE);
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
