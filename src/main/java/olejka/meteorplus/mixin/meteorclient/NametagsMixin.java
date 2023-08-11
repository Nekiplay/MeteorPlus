package olejka.meteorplus.mixin.meteorclient;

import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.render.Nametags;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import meteordevelopment.meteorclient.systems.modules.Module;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Nametags.class, remap = false)
public class NametagsMixin extends Module {
	@Shadow
	@Final
	private final Vector3d pos = new Vector3d();
	@Shadow
	@Final
	private final SettingGroup sgRender = settings.createGroup("Render");
	@Shadow
	@Final
	private final Setting<SettingColor> nameColor = sgRender.add(new ColorSetting.Builder()
		.name("name-color")
		.description("The color of the nametag names.")
		.defaultValue(new SettingColor())
		.build()
	);

	public NametagsMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Shadow
	private void drawBg(double x, double y, double width, double height) {

	}

	private final Color WHITE = new Color(255, 255, 255);
	private final Color RED = new Color(255, 25, 25);
	private final Color AMBER = new Color(255, 105, 25);
	private final Color GREEN = new Color(25, 252, 25);
	private final Color GOLD = new Color(232, 185, 35);
	@Inject(method = "renderGenericNametag", at = @At("HEAD"), remap = false)

	private void renderGenericNametag(LivingEntity entity, boolean shadow, CallbackInfo ci) {
		TextRenderer text = TextRenderer.get();
		NametagUtils.begin(pos);


		//Name
		String nameText = entity.getType().getName().getString();
		nameText += " ";

		//Health
		float absorption = entity.getAbsorptionAmount();
		int health = Math.round(entity.getHealth() + absorption);
		double healthPercentage = health / (entity.getMaxHealth() + absorption);

		// Can breed
		String breedText = "";

		if (entity.getType() == EntityType.DONKEY || entity.getType() == EntityType.LLAMA || entity.getType() == EntityType.HORSE) {
			AbstractHorseEntity donkeyEntity = (AbstractHorseEntity)entity;
			if (donkeyEntity.canBreed()) {
				breedText = "[Bread]";
			}
		}

		String healthText = String.valueOf(health);
		Color healthColor;

		if (healthPercentage <= 0.333) healthColor = RED;
		else if (healthPercentage <= 0.666) healthColor = AMBER;
		else healthColor = GREEN;

		double nameWidth = text.getWidth(nameText, shadow);
		double healthWidth = text.getWidth(healthText, shadow);
		double breadWidth = text.getWidth(breedText, shadow);
		double heightDown = text.getHeight(shadow);

		double width = nameWidth + healthWidth + breadWidth;
		double widthHalf = width / 2;

		drawBg(-widthHalf, -heightDown, width, heightDown);

		text.beginBig();
		double hX = -widthHalf;
		double hY = -heightDown;

		hX = text.render(nameText, hX, hY, nameColor.get(), shadow);
		hX = text.render(healthText, hX, hY, healthColor, shadow);
		text.render(breedText, hX, hY, healthColor, shadow);
		text.end();

		NametagUtils.end();
	}

}
