package olejka.meteorplus.modules.jesus;

import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.modules.jesus.modes.MatrixZoom;
import olejka.meteorplus.modules.jesus.modes.MatrixZoom2;
import olejka.meteorplus.modules.jesus.modes.Vulcan;


public class JesusPlus extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public JesusPlus() {
		super(MeteorPlus.CATEGORY, "jesus-plus", "Bypass jesus");
		onJesusModeChanged(jesusMode.get());
	}

	public final Setting<JesusModes> jesusMode = sgGeneral.add(new EnumSetting.Builder<JesusModes>()
		.name("mode")
		.description("The method of applying jesus.")
		.defaultValue(JesusModes.MatrixZoom)
		.onModuleActivated(spiderModesSetting -> onJesusModeChanged(spiderModesSetting.get()))
		.onChanged(this::onJesusModeChanged)
		.build()
	);

	public final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
		.name("Speed")
		.description("Jesus speed.")
		.defaultValue(1.25)
		.max(2500)
		.sliderRange(0, 2500)
		.build()
	);

	public final Setting<Boolean> autoSwapVulcan = sgGeneral.add(new BoolSetting.Builder()
		.name("auto-swap")
		.description("Auto swap.")
		.defaultValue(true)
		.visible(() -> jesusMode.get() == JesusModes.Vulcan)
		.build()
	);

	private JesusMode currentMode;

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
	public void onCanWalkOnFluid(CanWalkOnFluidEvent event) {
		currentMode.onCanWalkOnFluid(event);
	}
	@EventHandler
	public void onCollisionShape(CollisionShapeEvent event) {
		currentMode.onCollisionShape(event);
	}

	@EventHandler
	private void onPlayerMoveEvent(PlayerMoveEvent event) {
		currentMode.onPlayerMoveEvent(event);
	}


	private void onJesusModeChanged(JesusModes mode) {
		switch (mode) {
			case MatrixZoom:   currentMode = new MatrixZoom(); break;
			case MatrixZoom2:   currentMode = new MatrixZoom2(); break;
			case Vulcan:   currentMode = new Vulcan(); break;
		}
	}
}
