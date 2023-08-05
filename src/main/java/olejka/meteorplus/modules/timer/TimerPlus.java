package olejka.meteorplus.modules.timer;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.orbit.EventHandler;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.modules.jesus.JesusModes;
import olejka.meteorplus.modules.timer.modes.NCP;

public class TimerPlus extends Module {
	private static TimerMode oldMode = null;
	public static int workingDelay = 27;
	public static int workingTimer = 0;
	public static int rechargeTimer = 0; // Reset timer
	public static int rechargeDelay = 352; // Recharge delay
	public static double timerMultiplier = 2; // Timer multiplier
	public static double timerMultiplierOnRecharge = 1; // Timer multiplier on recharge

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

	private final Setting<Boolean> rechargeOnDisable = sgGeneral.add(new BoolSetting.Builder()
		.name("recharge-on-disable")
		.description("Recharge timer delay on disable.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Integer> rechargeDelaySetting = sgGeneral.add(new IntSetting.Builder()
		.name("recharge-delay")
		.description("Recharge timer delay.")
		.defaultValue(352)
		.visible(() -> mode.get() == TimerModes.Custom)
		.onChanged((a) ->  {
			rechargeDelay = a;
			rechargeTimer = 0;
		})
		.build()
	);

	private final Setting<Integer> boostDelaySetting = sgGeneral.add(new IntSetting.Builder()
		.name("boost-delay")
		.description("Working timer delay.")
		.defaultValue(27)
		.visible(() -> mode.get() == TimerModes.Custom)
		.onChanged((a) -> {
			workingDelay = a;
			workingTimer = 0;
		})
		.build()
	);

	private final Setting<Double> boostMultiplier = sgGeneral.add(new DoubleSetting.Builder()
		.name("multiplier")
		.description("Timer multiplier.")
		.defaultValue(2)
		.visible(() -> mode.get() == TimerModes.Custom)
		.onChanged((a) -> {
			timerMultiplier = a;
		})
		.build()
	);

	private final Setting<Double> boostMultiplierOnRecharge = sgGeneral.add(new DoubleSetting.Builder()
		.name("multiplier-on-recharge")
		.description("Timer multiplier on recharge.")
		.defaultValue(1)
		.visible(() -> mode.get() == TimerModes.Custom)
		.onChanged((a) -> {
			timerMultiplierOnRecharge = a;
		})
		.build()
	);

	private TimerMode currentMode;

	private void onTimerModeChanged(TimerModes mode) {
		switch (mode) {
			case NCP -> {
				currentMode = new NCP();
				workingDelay = 27;
				rechargeDelay = 352;
				timerMultiplier = 2;
				timerMultiplierOnRecharge = Timer.OFF;
			}
			case Custom -> {
				currentMode = new NCP();
				workingDelay = boostDelaySetting.get();
				rechargeDelay = rechargeDelaySetting.get();
				timerMultiplier = boostMultiplier.get();
				timerMultiplierOnRecharge = boostMultiplierOnRecharge.get();
			}
		}
	}

	@Override
	public void onActivate() {
		currentMode.onActivate();
	}

	@Override
	public void onDeactivate() {
		if (rechargeOnDisable.get()) {
			workingTimer = 0;
			rechargeTimer = 0;
		}
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

