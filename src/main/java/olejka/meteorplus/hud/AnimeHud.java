package olejka.meteorplus.hud;

import meteordevelopment.meteorclient.MeteorClient;
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

public class AnimeHud extends HudElement  {
	public AnimeHud(HUD hud) {
		super(hud, "Anime", "Shows anime.");
		//loadImage(String.valueOf(getLinkByImage(image.get())));
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public enum Image {
		Astolfo1,
		Astolfo2,
		Neko,
		Custom,
	}

	public final Setting<Image> image = sgGeneral.add(new EnumSetting.Builder<Image>()
		.name("image")
		.description("Image.")
		.defaultValue(Image.Astolfo1)
		.onModuleActivated((a) -> loadImage(getLinkByImage(a.get())))
		.onChanged((a) -> {
			loadImage(getLinkByImage(a));
		})

		.build()
	);

	private final Setting<String> customLink = sgGeneral.add(new StringSetting.Builder()
		.name("Custom link")
		.description("Custom link.")
		.defaultValue("")
		.visible(() ->  image.get() == Image.Custom)
		.onChanged(this::loadImage)
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
		else if (image == Image.Custom) {
			return customLink.get();
		}
		return "";
	}

	private final Setting<Double> imgWidth = sgGeneral.add(new DoubleSetting.Builder()
		.name("width")
		.description("The scale of the image.")
		.defaultValue(100)
		.min(10)
		.sliderRange(70, 1000)
		.build()
	);

	private static final Identifier TEXID = new Identifier("plus", "logo2");

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
			loadImage(String.valueOf(getLinkByImage(image.get())));
			return;
		}
		if ((onInventory.get() && mc != null && mc.currentScreen != null) || isInEditor()) {
			if (noChat.get() && !isInEditor() && mc.currentScreen instanceof ChatScreen) return;
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
		if (locked)
			return;
		new Thread(() -> {
			try {
				locked = true;
				var img = NativeImage.read(Http.get(url).sendInputStream());
				mc.getTextureManager().registerTexture(TEXID, new NativeImageBackedTexture(img));
				empty = false;
			} catch (Exception e) {

			} finally {
				locked = false;
			}
		}).start();
	}

}
