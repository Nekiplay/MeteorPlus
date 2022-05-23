package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import olejka.meteorplus.MeteorPlus;

public class NoFallPlus extends Module {
	public NoFallPlus() {
		super(MeteorPlus.CATEGORY, "NoFall-plus", "Prevent you from fall damage.");
	}
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
		.name("Mode")
		.description("NoFall mode.")
		.defaultValue(Mode.Matrix3Beta)
		.build()
	);

	public enum Mode
	{
		Matrix3Beta,
	}

	@EventHandler
	private void onSendPacket(PacketEvent.Send event) {
		work(event.packet);
	}

	@EventHandler
	private void onSendPacketSent(PacketEvent.Sent event) {
		work(event.packet);
	}

	private boolean checkY(PlayerMoveC2SPacket packet) {
		if (packet.changesPosition()) {
			if ((int)mc.player.fallDistance % 3 == 0 && mc.player.fallDistance >= 3) {
				return true;
			}
		}
		return false;
	}

	private double lastY = 0;

	private void work(Packet<?> packet) {
		if (packet instanceof PlayerMoveC2SPacket move) {
			if (checkY(move)) {
				((PlayerMoveC2SPacketAccessor) move).setOnGround(true);
				mc.player.setOnGround(true);
			}
		}
	}
}
