package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.meteor.ActiveModulesChangedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import olejka.meteorplus.MeteorPlus;

public class Freeze extends Module {
	public Freeze() {
		super(MeteorPlus.CATEGORY, "Freeze", "Freezes your position.");
	}
	private final SettingGroup FSettings = settings.createGroup("Freeze Settings");

	private final Setting<Boolean> FreezeLook = FSettings.add(new BoolSetting.Builder()
		.name("Freeze look")
		.description("Freezes your pitch and yaw.")
		.defaultValue(false)
		.build()
	);

	@Override()
	public void onActivate() {
		if (mc.player != null){
			yaw = mc.player.getYaw();
			pitch = mc.player.getPitch();
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onTick(TickEvent.Pre event) {
		if (mc.player == null) return;
		mc.player.setVelocity(0, 0, 0);
		if (FreezeLook.get()) {
			mc.player.setYaw(yaw);
			mc.player.setPitch(pitch);
		}
	}

	float yaw = 0;
	float pitch = 0;
}
