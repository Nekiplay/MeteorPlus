package olejka.meteorplus.modules.timer;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.modules.jesus.JesusModes;
import olejka.meteorplus.modules.timer.modes.NCP;

public class TimerPlus extends Module {
	private static TimerMode oldMode = null;
	public static int workingDelay = 27;
	public static int workingTimer = 0;
	public static int rechargeTimer = 0; // Reset timer
	public static int rechargeDelay = 352; // Recharge Delay

	private final SettingGroup settingsGroup = settings.getDefaultGroup();

	public final Setting<Boolean> onlyInMove = settingsGroup.add(new BoolSetting.Builder()
		.name("work-only-in-move")
		.description("Prevent false un charge.")
		.defaultValue(true)
		.build()
	);
//
	public TimerPlus() {
		super(MeteorPlus.CATEGORY, "timer-plus", "Bypass timer.");
		autoSubscribe = false;
		MeteorClient.EVENT_BUS.subscribe(this);
	}
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<TimerModes> mode = sgGeneral.add(new EnumSetting.Builder<TimerModes>()
		.name("mode")
		.description("Timer mode.")
		.defaultValue(TimerModes.NCP)
		.onModuleActivated(timerModesSetting -> onTimerModeChanged(timerModesSetting.get()))
		.onChanged(this::onTimerModeChanged)
		.build()
	);

	private final Setting<Integer> rechargeDelaySetting = sgGeneral.add(new IntSetting.Builder()
		.name("recharge-delay")
		.description("Recharge timer delay.")
		.defaultValue(352)
		.visible(() -> mode.get() == TimerModes.Custom)
		.onChanged((a) -> rechargeDelay = a)
		.build()
	);

	private final Setting<Integer> boostDelaySetting = sgGeneral.add(new IntSetting.Builder()
		.name("boost-delay")
		.description("Working timer delay.")
		.defaultValue(27)
		.visible(() -> mode.get() == TimerModes.Custom)
		.onChanged((a) -> workingDelay = a)
		.build()
	);

	private TimerMode currentMode;

	private void onTimerModeChanged(TimerModes mode) {
		switch (mode) {
			case NCP: {
				currentMode = new NCP();
				workingDelay = 27;
				rechargeDelay = 352;
				break;
			}
			case Custom: {
				currentMode = new NCP();
				workingDelay = boostDelaySetting.get();
				rechargeDelay = rechargeDelaySetting.get();
				break;
			}
		}
		if (oldMode != currentMode) {
			oldMode = currentMode;
			workingTimer = 0;
			rechargeTimer = 0;
		}
	}

	@Override
	public void onActivate() {
		if (oldMode != currentMode) {
			oldMode = currentMode;
			workingTimer = 0;
			rechargeTimer = 0;
		}
		currentMode.onActivate();
	}

	@Override
	public void onDeactivate() {
		currentMode.onDeactivate();
	}

	@EventHandler
	private void onPreTick(TickEvent.Pre event) {
		currentMode.onTickEventPre(event);
	}

	@EventHandler
	private void onPostTick(TickEvent.Post event) {
		currentMode.onTickEventPost(event);
	}
	@EventHandler
	public void onSendPacket(PacketEvent.Send event) {
		currentMode.onSendPacket(event);
	}
	@EventHandler
	public void onSentPacket(PacketEvent.Sent event) {
		currentMode.onSentPacket(event);
	}
}

