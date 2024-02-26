package nekiplay.meteorplus.mixin.minecraft;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.meteorplus.events.PlayerUseMultiplierEvent;
import nekiplay.meteorplus.features.modules.movement.NoSlowPlus;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientPlayerEntity.class, priority = 1001)
public class ClientPlayerEntityMixin {
	@Shadow
	public Input input;
	@Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", ordinal = 0))
	private void hookCustomMultiplier(CallbackInfo ci) {
		final PlayerUseMultiplierEvent playerUseMultiplier = new PlayerUseMultiplierEvent(0.2f, 0.2f);
		MeteorClient.EVENT_BUS.post(playerUseMultiplier);
		if (playerUseMultiplier.getForward() == 0.2f && playerUseMultiplier.getSideways() == 0.2f) {
			return;
		}

		final Input input = this.input;
		// reverse
		input.movementForward /= 0.2f;
		input.movementSideways /= 0.2f;

		// then
		input.movementForward *= playerUseMultiplier.getForward();
		input.movementSideways *= playerUseMultiplier.getSideways();
	}

	/**
	 * Hook sprint effect from NoSlow module
	 */
	@Inject(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), cancellable = true)
	private void hookSprintAffectStart(CallbackInfoReturnable<Boolean> cir) {
		if (Modules.get().get(NoSlowPlus.class).isActive()) {
			cir.setReturnValue(true);
		}
	}
}
