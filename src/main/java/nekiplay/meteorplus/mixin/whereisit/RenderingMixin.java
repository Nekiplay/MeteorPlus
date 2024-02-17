package nekiplay.meteorplus.mixin.whereisit;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import nekiplay.meteorplus.features.modules.integrations.WhereIsIt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.whereisit.client.render.Rendering;
import red.jackf.whereisit.config.WhereIsItConfig;

@Mixin(Rendering.class)
public class RenderingMixin {
	@Inject(method = "renderLabel", at = @At("HEAD"), cancellable = true)
	private static void renderLabel(Vec3d pos, Text name, MatrixStack pose, Camera camera, VertexConsumerProvider consumers, CallbackInfo ci) {
		pose.push();

		pos = pos.subtract(camera.getPos());

		WhereIsIt whereIsIt = Modules.get().get(WhereIsIt.class);

		pose.translate(pos.x, pos.y, pos.z);
		pose.multiply(camera.getRotation());
		var factor = 0.025f * WhereIsItConfig.INSTANCE.instance().getClient().containerNameLabelScale;
		pose.scale(-factor, -factor, factor);
		var matrix4f = pose.peek().getPositionMatrix();

		String text2 = name.getString();
		if (whereIsIt.suport_color_symbols.get()) {
			text2 = text2.replaceAll("&4", "");
			text2 = text2.replaceAll("&6", "");
			text2 = text2.replaceAll("&e", "");
			text2 = text2.replaceAll("&2", "");
			text2 = text2.replaceAll("&a", "");
			text2 = text2.replaceAll("&b", "");
			text2 = text2.replaceAll("&3", "");
			text2 = text2.replaceAll("&1", "");
			text2 = text2.replaceAll("&9", "");
			text2 = text2.replaceAll("&d", "");
			text2 = text2.replaceAll("&5", "");
			text2 = text2.replaceAll("&f", "");
			text2 = text2.replaceAll("&7", "");
			text2 = text2.replaceAll("&8", "");
			text2 = text2.replaceAll("&0", "");
		}

		var width = MinecraftClient.getInstance().textRenderer.getWidth(text2);
		float x = (float) -width / 2;

		if (whereIsIt.background.get()) {
			var bgBuffer = consumers.getBuffer(RenderLayer.getTextBackgroundSeeThrough());
			var bgColour = ((int) (MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F) * 255F)) << 24;
			bgBuffer.vertex(matrix4f, x - 1, -1f, 0).color(bgColour).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();
			bgBuffer.vertex(matrix4f, x - 1, 10f, 0).color(bgColour).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();
			bgBuffer.vertex(matrix4f, x + width, 10f, 0).color(bgColour).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();
			bgBuffer.vertex(matrix4f, x + width, -1f, 0).color(bgColour).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();
		}

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.depthFunc(GL11.GL_ALWAYS);
		RenderSystem.enableBlend();
		RenderSystem.depthMask(true);

		String text = name.getString();
		if (!whereIsIt.suport_color_symbols.get()) {
			MinecraftClient.getInstance().textRenderer.draw(text, x, 0, whereIsIt.notvisible_text_color.get().getPacked(), false,
				matrix4f, consumers, TextRenderer.TextLayerType.SEE_THROUGH, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
			MinecraftClient.getInstance().textRenderer.draw(text, x, 0, whereIsIt.visible_text_color.get().getPacked(), false,
				matrix4f, consumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		}
		else {
			int color = Color.WHITE.getPacked();
			if (text.startsWith("&4")) {
				color = 11141120;
			}
			else if (text.startsWith("&c")) {
				color = 16733525;
			}
			else if (text.startsWith("&6")) {
				color = 16755200;
			}
			else if (text.startsWith("&e")) {
				color = 16777045;
			}
			else if (text.startsWith("&2")) {
				color = 43520;
			}
			else if (text.startsWith("&a")) {
				color = 5635925;
			}
			else if (text.startsWith("&b")) {
				color = 5636095;
			}
			else if (text.startsWith("&3")) {
				color = 43690;
			}
			else if (text.startsWith("&1")) {
				color = 170;
			}
			else if (text.startsWith("&9")) {
				color = 5592575;
			}
			else if (text.startsWith("&d")) {
				color = 16733695;
			}
			else if (text.startsWith("&5")) {
				color = 11141290;
			}
			else if (text.startsWith("&f")) {
				color = 16777215;
			}
			else if (text.startsWith("&7")) {
				color = 11184810;
			}
			else if (text.startsWith("&8")) {
				color = 5592405;
			}
			else if (text.startsWith("&0")) {
				color = 0;
			}

			text = text.replaceAll("&4", "");
			text = text.replaceAll("&6", "");
			text = text.replaceAll("&e", "");
			text = text.replaceAll("&2", "");
			text = text.replaceAll("&a", "");
			text = text.replaceAll("&b", "");
			text = text.replaceAll("&3", "");
			text = text.replaceAll("&1", "");
			text = text.replaceAll("&9", "");
			text = text.replaceAll("&d", "");
			text = text.replaceAll("&5", "");
			text = text.replaceAll("&f", "");
			text = text.replaceAll("&7", "");
			text = text.replaceAll("&8", "");
			text = text.replaceAll("&0", "");

			MinecraftClient.getInstance().textRenderer.draw(text, x, 0,  color, false,
				matrix4f, consumers, TextRenderer.TextLayerType.SEE_THROUGH, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
			MinecraftClient.getInstance().textRenderer.draw(text, x, 0, color, false,
				matrix4f, consumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);

		}
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();


		pose.pop();

		ci.cancel();
	}
}
