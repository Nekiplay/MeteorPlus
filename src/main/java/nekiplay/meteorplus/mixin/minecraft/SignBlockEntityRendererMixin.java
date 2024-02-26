package nekiplay.meteorplus.mixin.minecraft;

import meteordevelopment.meteorclient.MeteorClient;
import nekiplay.meteorplus.events.RenderSignTextEvent;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(SignBlockEntityRenderer.class)
public class SignBlockEntityRendererMixin {
	@Inject(method = "renderText", at = @At("HEAD"))
	void renderTextSign(BlockPos pos, SignText signText, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int lineHeight, int lineWidth, boolean front, CallbackInfo ci) {
		if (signText.hasText(mc.player)) {
			final RenderSignTextEvent renderSignTextEvent = RenderSignTextEvent.get(pos, signText, matrices, vertexConsumers, light, lineHeight, lineWidth, front);
			MeteorClient.EVENT_BUS.post(renderSignTextEvent);
		}
	}
}
