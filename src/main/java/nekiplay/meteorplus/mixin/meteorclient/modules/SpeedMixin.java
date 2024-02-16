package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.speed.Speed;
import meteordevelopment.meteorclient.systems.modules.movement.speed.SpeedMode;
import meteordevelopment.meteorclient.systems.modules.movement.speed.SpeedModes;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.MovementType;
import org.spongepowered.asm.mixin.*;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = Speed.class, remap = false)
public class SpeedMixin {
	@Unique
	private final Speed speed = (Speed)(Object) this;
	@Shadow
	private SpeedMode currentMode;


	/**
	 * @author Neki_play
	 * @reason Fix override timer if Timer value equals 1
	 */
	@EventHandler
	@Overwrite
	private void onPlayerMove(PlayerMoveEvent event) {
		Setting<SpeedModes> speedMode = (Setting<SpeedModes>) speed.settings.get("mode");
		Setting<Double> timer = (Setting<Double>) speed.settings.get("timer");
		Setting<Boolean> inLiquids = (Setting<Boolean>) speed.settings.get("in-liquids");
		Setting<Boolean> whenSneaking = (Setting<Boolean>) speed.settings.get("when-sneaking");
		Setting<Boolean> vanillaOnGround = (Setting<Boolean>) speed.settings.get("only-on-ground");



		if (event.type != MovementType.SELF || mc.player.isFallFlying() || mc.player.isClimbing() || mc.player.getVehicle() != null) return;
		if (!whenSneaking.get() && mc.player.isSneaking()) return;
		if (vanillaOnGround.get() && !mc.player.isOnGround() && speedMode.get() == SpeedModes.Vanilla) return;
		if (!inLiquids.get() && (mc.player.isTouchingWater() || mc.player.isInLava())) return;

		if (timer.get() != 1.0) {
			Modules.get().get(Timer.class).setOverride(PlayerUtils.isMoving() ? timer.get() : Timer.OFF);
		}

		currentMode.onMove(event);

	}
}
