package nekiplay.meteorplus.mixin.meteorclient;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Tracers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import nekiplay.meteorplus.features.modules.AntiBotPlus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Tracers.class)
public class TracersMixin {
	@Inject(method = "shouldBeIgnored", at = @At("RETURN"), cancellable = true)
	protected void shouldBeIgnored(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		AntiBotPlus antiBotPlus = Modules.get().get(AntiBotPlus.class);
		if (antiBotPlus != null && antiBotPlus.isActive() && !cir.getReturnValue() && entity instanceof PlayerEntity) {
			cir.setReturnValue(antiBotPlus.isBot(entity));
		}
	}
}
