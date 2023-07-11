package olejka.meteorplus.mixin.meteorclient;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import net.minecraft.entity.Entity;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.MeteorPlusModules;
import olejka.meteorplus.modules.AntiBotPlus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ESP.class)
public class ESPMixin {
	private final ESP esp = (ESP)(Object) this;

	@Inject(method = "shouldSkip", at = @At("RETURN"), cancellable = true)
	protected void shouldSkip(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		AntiBotPlus antiBotPlus = Modules.get().get(AntiBotPlus.class);
		if (antiBotPlus != null && antiBotPlus.isActive()) {
			cir.setReturnValue(antiBotPlus.isBot(entity));
		}
	}
}
