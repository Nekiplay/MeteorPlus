package nekiplay.meteorplus.mixin.numbyhack;

import cqb13.NumbyHack.NumbyHack;
import meteordevelopment.meteorclient.MeteorClient;
import nekiplay.main.events.hud.DebugDrawTextEvent;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = NumbyHack.class)
public class MainMixin {
	@Inject(
		method = "onInitialize",
		at = @At(
			value = "INVOKE",
			target = "Lmeteordevelopment/meteorclient/systems/modules/Modules;add(Lmeteordevelopment/meteorclient/systems/modules/Module;)V"
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void modifyAddModules(CallbackInfo ci) {

	}
}
