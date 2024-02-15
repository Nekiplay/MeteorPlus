package nekiplay.meteorplus.features.modules.timer.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import nekiplay.meteorplus.features.modules.timer.TimerMode;
import nekiplay.meteorplus.features.modules.timer.TimerModes;
import nekiplay.meteorplus.utils.algoritms.RandomUtils;

import static nekiplay.meteorplus.features.modules.timer.TimerPlus.*;
import static nekiplay.meteorplus.features.modules.timer.TimerPlus.timerMultiplierOnRecharge;

public class Vulcan extends TimerMode  {
	private Timer timer;
	public Vulcan() {
		super(TimerModes.Vulcan);
		timer = Modules.get().get(Timer.class);
	}

	@Override
	public void onDeactivate() {
		timer.setOverride(Timer.OFF);
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		if (mc.player == null) return;
		if (rechargeTimer == 0) {
			if (workingTimer > RandomUtils.nextInt(8, 10)) {
				int delay = RandomUtils.nextInt(410, 720);
				rechargeTimer = delay;
				rechargeDelay = delay;
				workingTimer = 0;
				timer.setOverride(Timer.OFF);
			}
			else {
				if (settings.isActive()) {
					if (settings.onlyInMove.get() && PlayerUtils.isMoving()) {
						workingTimer++;
						timer.setOverride(1.35);
					}
					else if (!settings.onlyInMove.get()) {
						workingTimer++;
						timer.setOverride(1.35);
					}
					else {
						timer.setOverride(Timer.OFF);
					}
				}
			}
		}
		else {
			rechargeTimer--;
			if (settings.isActive()) {
				timer.setOverride(Timer.OFF);
			}
			else {
				timer.setOverride(Timer.OFF);
			}
		}
	}
}
