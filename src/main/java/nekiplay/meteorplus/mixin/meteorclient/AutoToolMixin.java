package nekiplay.meteorplus.mixin.meteorclient;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.systems.modules.player.AutoTool;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = AutoTool.class, remap = false)
public class AutoToolMixin
{
	@Inject(method = "onStartBreakingBlock", at = @At("HEAD"), cancellable = true)
	private void onStartBreakingBlock(StartBreakingBlockEvent event, CallbackInfo ci) {
		if (PlayerUtils.getGameMode() == GameMode.CREATIVE || PlayerUtils.getGameMode() == GameMode.SPECTATOR)
		{
			if (ci.isCancellable()) {
				ci.cancel();
			}
		}
	}
}
