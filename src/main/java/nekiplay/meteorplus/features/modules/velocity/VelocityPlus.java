package nekiplay.meteorplus.features.modules.velocity;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlus;
import nekiplay.meteorplus.features.modules.timer.TimerMode;
import nekiplay.meteorplus.features.modules.timer.TimerModes;
import nekiplay.meteorplus.features.modules.timer.TimerPlus;
import nekiplay.meteorplus.features.modules.timer.modes.NCP;
import nekiplay.meteorplus.features.modules.velocity.modes.Grim;
import net.minecraft.client.MinecraftClient;

public class VelocityPlus extends Module {
	public VelocityPlus() {
		super(MeteorPlus.CATEGORY, "velocity+", "Bypass velocity.");
	}
	private final SettingGroup settingsGroup = settings.getDefaultGroup();

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<VelocityModes> mode = sgGeneral.add(new EnumSetting.Builder<VelocityModes>()
		.name("mode")
		.description("Velocity mode.")
		.defaultValue(VelocityModes.Grim)
		.onModuleActivated(timerModesSetting -> onTimerModeChanged(timerModesSetting.get()))
		.onChanged(this::onTimerModeChanged)
		.build()
	);


	private VelocityMode currentMode;

	private void onTimerModeChanged(VelocityModes mode) {
		switch (mode) {
			case Grim -> {
				currentMode = new Grim();
			}
		}
	}

	@Override
	public void onActivate() {
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
	@EventHandler
	private void onRecivePacket(PacketEvent.Receive event) {
		currentMode.onReceivePacket(event);
	}
}
