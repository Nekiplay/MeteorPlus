package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
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

	private float yaw = 0;
	private float pitch = 0;
	private Vec3d position = Vec3d.ZERO;

	@Override()
	public void onActivate() {
		if (mc.player != null){
			yaw = mc.player.getYaw();
			pitch = mc.player.getPitch();
			position = mc.player.getPos();
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
			mc.player.setPos(position.getX(), position.getY(), position.getZ());
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
			mc.player.setPos(position.getX(), position.getY(), position.getZ());
		}
	}
}
