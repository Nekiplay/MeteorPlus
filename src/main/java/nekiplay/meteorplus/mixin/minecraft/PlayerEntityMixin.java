package nekiplay.meteorplus.mixin.minecraft;

import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.meteorplus.features.modules.KeepSprint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntity.class)
public class PlayerEntityMixin {
	private PlayerEntity player = (PlayerEntity) (Object) this;

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
	private void onAttack(Entity target, CallbackInfo ci) {
		KeepSprint keepSprint = Modules.get().get(KeepSprint.class);
		if (keepSprint.isActive()) {
			player.setSprinting(true);
		}
	}
}
