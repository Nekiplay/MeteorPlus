package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
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

	private final Setting<Boolean> FreezeLookSilent = FSettings.add(new BoolSetting.Builder()
		.name("Freeze look silent")
		.description("Freezes your pitch and yaw.")
		.defaultValue(true)
		.visible(FreezeLook::get)
		.build()
	);

	@Override()
	public void onActivate() {
		if (mc.player != null){
			yaw = mc.player.getYaw();
			pitch = mc.player.getPitch();
		}
	}

	private void setFreezeLook(PacketEvent event, PlayerMoveC2SPacket playerMove)
	{
		if (playerMove.changesLook() && FreezeLook.get() && FreezeLookSilent.get()) {
			event.setCancelled(true);
		}
		else if (playerMove.changesLook() && FreezeLook.get() && !FreezeLookSilent.get()) {
			event.setCancelled(true);
			mc.player.setYaw(yaw);
			mc.player.setPitch(pitch);
		}
		if (playerMove.changesPosition()) {
			mc.player.setVelocity(0, 0, 0);
			event.setCancelled(true);
		}
	}
	@EventHandler
	private void onMovePacket(PacketEvent.Sent event) {
		if (event.packet instanceof PlayerMoveC2SPacket playerMove) {
			setFreezeLook(event, playerMove);
		}
	}
	@EventHandler
	private void onMovePacket2(PacketEvent.Send event) {
		if (event.packet instanceof PlayerMoveC2SPacket playerMove) {
			setFreezeLook(event, playerMove);
		}
	}

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		if (mc.player != null) {
			mc.player.setVelocity(0, 0, 0);
		}
	}

	float yaw = 0;
	float pitch = 0;
}
