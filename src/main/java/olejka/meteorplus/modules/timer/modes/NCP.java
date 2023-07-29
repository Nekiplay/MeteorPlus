package olejka.meteorplus.modules.timer.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.modules.timer.TimerMode;
import olejka.meteorplus.modules.timer.TimerModes;

import static olejka.meteorplus.modules.timer.TimerPlus.*;

public class NCP extends TimerMode {
	private Timer timer;
	public NCP() {
		super(TimerModes.NCP);
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
				if (MeteorPlus.getInstance().timerPlus.isActive()) {
					if (MeteorPlus.getInstance().timerPlus.onlyInMove.get() && PlayerUtils.isMoving()) {
						workingTimer++;
						timer.setOverride(2);
					}
					else if (!MeteorPlus.getInstance().timerPlus.onlyInMove.get()) {
						workingTimer++;
						timer.setOverride(2);
					}
					else {
						timer.setOverride(Timer.OFF);
					}
				}
			}
		}
		else {
			rechargeTimer--;
			timer.setOverride(Timer.OFF);
		}
	}
}
