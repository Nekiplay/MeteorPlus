package olejka.meteorplus.modules.speed.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import olejka.meteorplus.modules.speed.SpeedMode;
import olejka.meteorplus.modules.speed.SpeedModes;

public class NCPHop extends SpeedMode {
	public NCPHop() {
		super(SpeedModes.NCPHop);
	}

	@Override
	public void onActivate() {
		Modules.get().get(Timer.class).setOverride(1.0865f);
	}

	@Override
	public void onDeactivate() {
		Modules.get().get(Timer.class).setOverride(Timer.OFF);
		mc.player.getAbilities().setFlySpeed(0.02f);
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		if (mc.player.isTouchingWater() || mc.player.isInLava() ||
			mc.player.isClimbing() || mc.player.isRiding()) return;
		Timer timer = Modules.get().get(Timer.class);
		if (PlayerUtils.isMoving() && mc.player.isOnGround()) {
			mc.player.jump();
			mc.player.getAbilities().setFlySpeed(0.0223f);
		}
		else {
			timer.setOverride(1);
		}
	}
}
