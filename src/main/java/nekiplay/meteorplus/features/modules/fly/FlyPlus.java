package nekiplay.meteorplus.features.modules.fly;

import meteordevelopment.meteorclient.events.entity.DamageEvent;
import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.EnumSettingV2;
import nekiplay.meteorplus.MeteorPlus;
import nekiplay.meteorplus.features.modules.fly.modes.Damage;
import nekiplay.meteorplus.features.modules.fly.modes.MatrixExploit;
import nekiplay.meteorplus.features.modules.fly.modes.MatrixExploit2;
import nekiplay.meteorplus.features.modules.fly.modes.VulcanClip;

public class FlyPlus extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public FlyPlus() {
		super(MeteorPlus.CATEGORY, "fly+", "Bypass fly");
		onFlyModeChanged(flyMode.get());
	}

	public final Setting<FlyModes> flyMode = sgGeneral.add(new EnumSetting.Builder<FlyModes>()
		.name("mode")
		.description("The method of applying fly.")
		.defaultValue(FlyModes.Matrix_Exploit)
		.onModuleActivated(spiderModesSetting -> onFlyModeChanged(spiderModesSetting.get()))
		.onChanged(this::onFlyModeChanged)
		.build()
	);

	public final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
		.name("Speed")
		.description("Fly speed.")
		.defaultValue(1.25)
		.max(2500)
		.sliderRange(0, 2500)
		.visible(() -> flyMode.get() == FlyModes.Matrix_Exploit)
		.build()
	);

	public final Setting<Double> speed2 = sgGeneral.add(new DoubleSetting.Builder()
		.name("Speed")
		.description("Fly speed.")
		.defaultValue(0.3)
		.max(5)
		.sliderRange(0, 5)
		.visible(() -> flyMode.get() == FlyModes.Matrix_Exploit_2)
		.build()
	);

	public final Setting<Double> speedDamage = sgGeneral.add(new DoubleSetting.Builder()
		.name("Speed")
		.description("Fly speed.")
		.defaultValue(1.25)
		.max(2500)
		.sliderRange(0, 2500)
		.visible(() -> flyMode.get() == FlyModes.Damage)
		.build()
	);

	public final Setting<Double> speedDamageY = sgGeneral.add(new DoubleSetting.Builder()
		.name("Speed Y")
		.description("Fly speed Y.")
		.defaultValue(1.25)
		.max(2500)
		.sliderRange(0, 2500)
		.visible(() -> flyMode.get() == FlyModes.Damage)
		.build()
	);

	public final Setting<Integer> speedDamageTicks = sgGeneral.add(new IntSetting.Builder()
		.name("Max ticks")
		.description("Max fly ticks.")
		.defaultValue(240)
		.max(2500)
		.sliderRange(0, 2500)
		.visible(() -> flyMode.get() == FlyModes.Damage)
		.build()
	);

	public final Setting<Boolean> canClip = sgGeneral.add(new BoolSetting.Builder()
		.name("Can Clip")
		.description("Max fly ticks.")
		.visible(() -> flyMode.get() == FlyModes.Vulcan_Clip)
		.build()
	);

	private FlyMode currentMode;

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
	@EventHandler
	private void onPlayerMoveSendPre(SendMovementPacketsEvent.Pre event) {
		currentMode.onPlayerMoveSendPre(event);
	}


	@EventHandler
	private void onDamage(DamageEvent event) {
		currentMode.onDamage(event);
	}

	private void onFlyModeChanged(FlyModes mode) {
		switch (mode) {
			case Matrix_Exploit_2 -> currentMode = new MatrixExploit2();
			case Matrix_Exploit -> currentMode = new MatrixExploit();
			case Vulcan_Clip -> currentMode = new VulcanClip();
			case Damage -> currentMode = new Damage();
		}
	}
}
