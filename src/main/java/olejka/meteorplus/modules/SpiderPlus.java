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

	private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
		.name("Mode")
		.description("Spider mode.")
		.defaultValue(Mode.Matrix)
		.build()
	);

	public enum Mode
	{
		Matrix,
		Vulcan,
	}

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
					jumps++;
				}
				if (YGround(y, RGround(startY) - 0.1, RGround(startY) + 0.1)) {
					((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
					jumps++;
				}
				if (mc.player.isOnGround() && block) {
					block = false;
					startY = mc.player.getPos().y;
					start = false;
				}
			}
		}
		else {
			if (packet instanceof PlayerMoveC2SPacket move) {
				if (mc.player.isOnGround() && block) {
					block = false;
					start = false;
				}
				if (mc.player.isOnGround()) {
					startY = mc.player.getPos().y;
				}
			}
		}
	}

	private int tick = 0;
	private int jumps = 0;

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

	private boolean block = false;
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
			if (!block) {
				if (tick == 0) {
					mc.player.setVelocity(pl_velocity.x, 0.41999998688698, pl_velocity.z);
					tick = 1;
					jumps++;
				} else if (tick == 1) {
					mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.08679999325 - coff, pl_velocity.z);
					tick = 2;
					jumps++;
				} else if (tick == 2) {
					mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.17186398826 - coff, pl_velocity.z);
					tick = 0;
					jumps++;
				}
				if (mc.player.getPos().y >= startY + 3.5 && mode.get() == Mode.Vulcan) {
					block = true;
				}
			}

		} else {
			modify = false;
			jumps = 0;
			startOnGround = false;
			tick = 0;
		}
	}
}
