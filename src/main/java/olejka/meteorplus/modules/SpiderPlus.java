package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.starscript.compiler.Expr;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
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

	private final Setting<Boolean> safeMode = sgGeneral.add(new BoolSetting.Builder()
		.name("safe-mode")
		.description("Prevent kicks and bans.")
		.defaultValue(true)
		.visible(() -> mode.get() == Mode.Vulcan)
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
				if (lastY == y && tick > 1) {
					block = true;
				}
				else {
					lastY = y;
				}

				if (YGround(y, 0.0, 0.1)) {
					((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
				}
				if (YGround(y, RGround(startY) - 0.1, RGround(startY) + 0.1)) {
					((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
				}
				if (mc.player.isOnGround() && block) {
					block = false;
					startY = mc.player.getPos().y;
					start = false;
				}
				else if (upTouch && tick > 0) {
					block = true;
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
	private int tick2 = 0;

	@Override
	public void onActivate() {
		tick = 0;
		start = false;
		modify = false;

		startY = mc.player.getPos().y;
	}

	private boolean modify = false;
	private boolean start = false;

	private double startY = 0;
	private double lastY = 0;

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
	private boolean upTouch = false;

	@EventHandler
	private void onTick(TickEvent.Post event) {
		ClientPlayerEntity player = mc.player;
		Vec3d pl_velocity = player.getVelocity();
		Vec3d pos = player.getPos();
		ClientPlayNetworkHandler h = mc.getNetworkHandler();
		modify = player.horizontalCollision;
		upTouch = player.verticalCollision;
		if (player.horizontalCollision) {
			if (!start) {
				start = true;
				startY = mc.player.getPos().y;
				lastY = mc.player.getY();
			}
			if (!block) {
				if (tick == 0) {
					mc.player.setVelocity(pl_velocity.x, 0.41999998688698, pl_velocity.z);
					tick = 1;
				} else if (tick == 1) {
					mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.08679999325 - coff, pl_velocity.z);
					tick = 2;
				} else if (tick == 2) {
					mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.17186398826 - coff, pl_velocity.z);
					tick = 0;
				}
				if (mc.player.getPos().y >= startY + 3.5 && mode.get() == Mode.Vulcan) {
					block = true;
				}
				tick2++;
			}

		} else {
			modify = false;
			tick = 0;
		}
	}
}
