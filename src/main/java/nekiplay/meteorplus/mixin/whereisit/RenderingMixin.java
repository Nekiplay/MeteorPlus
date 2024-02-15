package nekiplay.meteorplus.mixin.whereisit;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
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
		var width = MinecraftClient.getInstance().textRenderer.getWidth(name);
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
		MinecraftClient.getInstance().textRenderer.draw(name, x, 0, whereIsIt.notvisible_text_color.get().getPacked(), false,
			matrix4f, consumers, TextRenderer.TextLayerType.SEE_THROUGH, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		MinecraftClient.getInstance().textRenderer.draw(name, x, 0, whereIsIt.visible_text_color.get().getPacked(), false,
			matrix4f, consumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();


		pose.pop();

		ci.cancel();
	}
}
