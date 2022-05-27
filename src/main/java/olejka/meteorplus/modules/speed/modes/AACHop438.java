package olejka.meteorplus.modules.speed.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import olejka.meteorplus.modules.speed.SpeedMode;
import olejka.meteorplus.modules.speed.SpeedModes;

public class AACHop438 extends SpeedMode {
	public AACHop438() {
		super(SpeedModes.AACHop438);
	}

	@Override
	public void onDeactivate() {
		Modules.get().get(Timer.class).setOverride(Timer.OFF);
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		if (!PlayerUtils.isMoving() || mc.player.isTouchingWater() || mc.player.isInLava() ||
			mc.player.isClimbing() || mc.player.isRiding()) return;

		if (mc.player.isOnGround())
			mc.player.jump();
		else {
			if (mc.player.fallDistance <= 0.1)
				Modules.get().get(Timer.class).setOverride(1.5);
			else if (mc.player.fallDistance < 1.3)
				Modules.get().get(Timer.class).setOverride(0.7);
			else
				Modules.get().get(Timer.class).setOverride(Timer.OFF);
		}
	}
}
