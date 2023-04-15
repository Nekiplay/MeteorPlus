package olejka.meteorplus.modules.speed.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import olejka.meteorplus.modules.speed.SpeedMode;
import olejka.meteorplus.modules.speed.SpeedModes;

public class Matrix extends SpeedMode {
	public Matrix() {
		super(SpeedModes.Matrix);
	}

	@Override
	public void onDeactivate() {
		Modules.get().get(Timer.class).setOverride(Timer.OFF);
		if (mc.player != null) {
			mc.player.getAbilities().setFlySpeed(0.02f);
		}
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		Timer timer = Modules.get().get(Timer.class);
		timer.setOverride(Timer.OFF);
		if (mc.player.isTouchingWater() || mc.player.isInLava() ||
			mc.player.isClimbing() || mc.player.isRiding()) return;
		if (PlayerUtils.isMoving()) {
			if (mc.player.isOnGround()) {
				mc.player.jump();
				mc.player.getAbilities().setFlySpeed(0.02098f);
				timer.setOverride(1.055f);
			}
		}
		else {
			timer.setOverride(1);
		}
	}
}
