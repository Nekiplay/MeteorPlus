package nekiplay.meteorplus.mixin.minecraft.hud;

import meteordevelopment.meteorclient.MeteorClient;
import nekiplay.meteorplus.events.hud.DebugDrawTextEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = DebugHud.class, priority = 1001)
public class DebugHudMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(
		method = "drawLeftText",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/DebugHud;drawText(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Z)V",
			shift = At.Shift.BEFORE
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void modifyDrawLeftText(DrawContext ignored, CallbackInfo ci, List<String> lines) {
		DebugDrawTextEvent debugDrawTextEvent = DebugDrawTextEvent.get(lines, true);
		MeteorClient.EVENT_BUS.post(debugDrawTextEvent);
	}

	@Inject(
		method = "drawRightText",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/DebugHud;drawText(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Z)V",
			shift = At.Shift.BEFORE
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void modifyDrawRightText(DrawContext ignored, CallbackInfo ci, List<String> lines) {
		DebugDrawTextEvent debugDrawTextEvent = DebugDrawTextEvent.get(lines, false);
		MeteorClient.EVENT_BUS.post(debugDrawTextEvent);
	}
}
