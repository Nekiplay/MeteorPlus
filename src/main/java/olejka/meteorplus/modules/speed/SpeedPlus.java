package olejka.meteorplus.modules.speed;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.modules.speed.modes.*;

public class SpeedPlus extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public SpeedPlus() {
		super(MeteorPlus.CATEGORY, "speed-plus", "Bypass speed");
		onSpeedModeChanged(speedMode.get());
	}

	public final Setting<SpeedModes> speedMode = sgGeneral.add(new EnumSetting.Builder<SpeedModes>()
		.name("mode")
		.description("The method of applying speed.")
		.defaultValue(SpeedModes.MatrixExploit)
		.onModuleActivated(spiderModesSetting -> onSpeedModeChanged(spiderModesSetting.get()))
		.onChanged(this::onSpeedModeChanged)
		.build()
	);

	public final Setting<Double> speedMatrix = sgGeneral.add(new DoubleSetting.Builder()
		.name("Speed")
		.description("Speed.")
		.defaultValue(4)
		.visible(() -> speedMode.get() == SpeedModes.MatrixExploit)
		.build()
	);

	public final Setting<Double> speedVulcan = sgGeneral.add(new DoubleSetting.Builder()
		.name("Speed")
		.description("Speed.")
		.defaultValue(15)
		.visible(() -> speedMode.get() == SpeedModes.Vulcan)
		.build()
	);

	public final Setting<Boolean> autoSwapVulcan = sgGeneral.add(new BoolSetting.Builder()
		.name("auto-swap")
		.description("Auto swap.")
		.defaultValue(true)
		.visible(() -> speedMode.get() == SpeedModes.Vulcan)
		.build()
	);

	private SpeedMode currentMode;

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
	private void onPlayerMoveEvent(PlayerMoveEvent event) {
		currentMode.onPlayerMoveEvent(event);
	}


	private void onSpeedModeChanged(SpeedModes mode) {
		switch (mode) {
			case MatrixExploit:   currentMode = new MatrixExploit(); break;
			case Matrix:   currentMode = new Matrix(); break;
			case AACHop438:   currentMode = new AACHop438(); break;
			case Vulcan:   currentMode = new Vulcan(); break;
			case NCPHop:   currentMode = new NCPHop(); break;
		}
	}
}
