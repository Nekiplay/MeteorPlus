package nekiplay.meteorplus.features.modules.timer.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import nekiplay.meteorplus.features.modules.timer.TimerMode;
import nekiplay.meteorplus.features.modules.timer.TimerModes;
import nekiplay.meteorplus.features.modules.timer.TimerPlus;

import static nekiplay.meteorplus.features.modules.timer.TimerPlus.*;

public class Intave extends TimerMode {
	private Timer timer;
	public Intave() {
		super(TimerModes.Intave);
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
			if (workingTimer > workingDelay) {
				rechargeTimer = rechargeDelay;
				workingTimer = 0;
				timer.setOverride(Timer.OFF);
			}
			else {
				if (settings.isActive()) {
					if (settings.onlyInMove.get() && PlayerUtils.isMoving()) {
						workingTimer++;
						timer.setOverride(timerMultiplier);
					}
					else if (!settings.onlyInMove.get()) {
						workingTimer++;
						timer.setOverride(timerMultiplier);
					}
					else {
						timer.setOverride(timerMultiplierOnRecharge);
					}
				}
			}
		}
		else {
			rechargeTimer--;
			if (settings.isActive()) {
				timer.setOverride(timerMultiplierOnRecharge);
			}
			else {
				timer.setOverride(Timer.OFF);
			}
		}
	}
}
