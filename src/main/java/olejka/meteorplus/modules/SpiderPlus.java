package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import olejka.meteorplus.MeteorPlus;

public class SpiderPlus extends Module {
	public SpiderPlus() {
		super(MeteorPlus.CATEGORY, "spider-plus", "Matrix spider.");
	}
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	@EventHandler
	private void onSendPacket(PacketEvent.Send event) {
		work(event.packet);
	}
	@EventHandler
	private void onSentPacket(PacketEvent.Sent event) {
		work(event.packet);
	}

	private void work(Packet<?> packet) {
		if (modify) {
			if (packet instanceof PlayerMoveC2SPacket move) {
				double y = mc.player.getY();
				y = move.getY(y);
				if (YGround(y, 0.0, 0.1)) {
					((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
				}
				if (YGround(y, RGround(startY) - 0.1, RGround(startY) + 0.1)) {
					((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
				}
			}
		}
	}

	private int tick = 0;
	private int sended = 0;

	@Override
	public void onActivate() {
		tick = 0;
		start = false;
		modify = false;

		startY = mc.player.getPos().y;
	}

	private boolean modify = false;
	private boolean start = false;
	private boolean startOnGround = false;

	private double startY = 0;

	private boolean YGround(double height, double min, double max) {
		String yString = String.valueOf(height);
		yString = yString.substring(yString.indexOf("."));
		double y = Double.parseDouble(yString);
		if (y >= min && y <= max) {
			return true;
		}
		else {
			return false;
		}
	}

	private double RGround(double height) {
		String yString = String.valueOf(height);
		yString = yString.substring(yString.indexOf("."));
		double y = Double.parseDouble(yString);
		return y;
	}

	private double coff = 0.0000000000326;

	@EventHandler
	private void onTick(TickEvent.Post event) {
		ClientPlayerEntity player = mc.player;
		Vec3d pl_velocity = player.getVelocity();
		Vec3d pos = player.getPos();
		ClientPlayNetworkHandler h = mc.getNetworkHandler();
		modify = player.horizontalCollision;
		if (player.horizontalCollision) {
			if (!start) {
				start = true;
				startY = mc.player.getPos().y;
			}
			if (tick == 0) {
				mc.player.setVelocity(pl_velocity.x, 0.41999998688698, pl_velocity.z);
				tick = 1;
			}
			else if (tick == 1) {
				mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.08679999325 - coff, pl_velocity.z);
				tick = 2;
			}
			else if (tick == 2) {
				mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.17186398826 - coff, pl_velocity.z);
				tick = 0;
			}

			//if (mc.player.getPos().y <= startY + 0.41999998688698) {
			//	mc.player.setVelocity(0, 0.41999998688698, 0);
			//} else if (mc.player.getPos().y <= startY + 0.7531999805212) {
			//	mc.player.setVelocity(0, 0.33319999363, 0);
			//} else if (mc.player.getPos().y == startY + 1.00133597911214) {
			//	mc.player.setVelocity(0, 0.00133597911214, 0);
			//} else if (mc.player.getPos().y <= startY + 1.42133596599912) {
			//	mc.player.setVelocity(0, 0.41999998688698, 0);
			//}
			//else if (mc.player.getPos().y <= startY + 1.75453595963334) {
			//	mc.player.setVelocity(0, 0.33319999363, 0);
			//}
			//else if (mc.player.getPos().y <= startY + 2.00267195822428) {
			//	mc.player.setVelocity(0, 0.0267195822428, 0);
			//}

		} else {
			modify = false;
			if (start) {
				mc.player.setVelocity(pl_velocity.x, 0, pl_velocity.z);
				mc.player.setOnGround(true);
			}
			startOnGround = false;
			tick = 0;
			start = false;
		}
	}
}
