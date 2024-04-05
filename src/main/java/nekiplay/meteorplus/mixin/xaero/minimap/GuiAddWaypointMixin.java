package nekiplay.meteorplus.mixin.xaero.minimap;

import nekiplay.meteorplus.settings.ConfigModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.gui.GuiAddWaypoint;
import xaero.common.misc.OptimizedMath;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = GuiAddWaypoint.class, remap = false, priority = 1001)
public class GuiAddWaypointMixin {
	@Shadow
	private boolean hasForcedPlayerPos;
	@Shadow
	private int forcedPlayerX;
	@Shadow
	private int forcedPlayerZ;
	@Shadow
	private double forcedPlayerScale;

	@Inject(method = "init", at = @At("RETURN"))
	private void init(CallbackInfo ci) {
		if (ConfigModifier.get().positionProtection.get()) {
			forcedPlayerX = (int) (mc.player.getPos().x + ConfigModifier.get().x_spoof.get());
			forcedPlayerZ = (int) (mc.player.getPos().z + ConfigModifier.get().z_spoof.get());
		}
	}

	@Inject(method = "getAutomaticZ", at = @At("RETURN"), cancellable = true)
	private void getAutimaticZ(double waypointDimScale, CallbackInfoReturnable<Integer> cir) {
		if (ConfigModifier.get().positionProtection.get()) {
			int playerZ = this.hasForcedPlayerPos ? this.forcedPlayerZ : OptimizedMath.myFloor(mc.cameraEntity.getZ());
			cir.setReturnValue(OptimizedMath.myFloor(((double) playerZ + ConfigModifier.get().z_spoof.get()) * this.getDimDiv(waypointDimScale)));
		}
	}

	@Inject(method = "getAutomaticX", at = @At("RETURN"), cancellable = true)
	private void getAutimaticX(double waypointDimScale, CallbackInfoReturnable<Integer> cir) {
		if (ConfigModifier.get().positionProtection.get()) {
			int playerX = this.hasForcedPlayerPos ? this.forcedPlayerX : OptimizedMath.myFloor(mc.cameraEntity.getX());
			cir.setReturnValue(OptimizedMath.myFloor(((double) playerX + ConfigModifier.get().x_spoof.get()) * this.getDimDiv(waypointDimScale)));
		}
	}

	@Shadow
	private double getDimDiv(double waypointDimScale) {
		double playerDimScale = this.hasForcedPlayerPos ? this.forcedPlayerScale : mc.cameraEntity.getWorld().getDimension().coordinateScale();
		return playerDimScale / waypointDimScale;
	}
}
