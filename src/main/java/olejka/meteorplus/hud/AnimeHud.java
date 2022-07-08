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

public class AnimeHud extends HudElement  {

	public static final HudElementInfo<AnimeHud> INFO = new HudElementInfo<>(MeteorPlus.HUD_GROUP, "AnimeHud", "Shows the MeteorPlus logo in the HUD.", AnimeHud::new);

	private final SettingGroup sgGeneral = settings.getDefaultGroup();



	public enum Image {
		Astolfo1,
		Astolfo2,
		Neko,
	}

	private final Setting<Image> image = sgGeneral.add(new EnumSetting.Builder<Image>()
		.name("image")
		.description("Image.")
		.defaultValue(Image.Astolfo1)
		.onModuleActivated((a) -> loadImage(getLinkByImage(a.get())))
		.onChanged((a) -> loadImage(getLinkByImage(a)))
		.build()
	);


	public String getLinkByImage(Image image) {
		if (image == Image.Astolfo1) {
			return "https://i.ibb.co/gv31zjS/l-Og-TN5x-removebg-preview.png";
		}
		else if (image == Image.Astolfo2) {
			return "https://i.ibb.co/6br3pcc/1619388386-30-pibig-info-p-astolfo-tyan-anime-krasivo-34-removebg-preview.png";
		}
		else if (image == Image.Neko) {
			return "https://i.ibb.co/Zmh2mnF/da5a1f3816dd4e17935e303361152456-removebg-preview.png";
		}
		return "";
	}

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

	public AnimeHud() {
		super(INFO);
		calculateSize();
	}

	public void calculateSize() {
		box.setSize(imgWidth.get(), imgHeight.get());
	}

	@Override
	public void render(HudRenderer renderer) {
		if (empty) {
			loadImage(String.valueOf(getLinkByImage(image.get())));
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
		if (locked)
			return;
		new Thread(() -> {
			try {
				locked = true;
				var img = NativeImage.read(Http.get(url).sendInputStream());
				mc.getTextureManager().registerTexture(TEXID, new NativeImageBackedTexture(img));
				empty = false;
			} catch (Exception ignored) {

			} finally {
				locked = false;
			}
		}).start();
	}

}
