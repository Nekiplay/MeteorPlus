package nekiplay.bozeplus.features.modules.movement.spider;

import dev.boze.api.Globals;
import dev.boze.api.addon.module.ToggleableModule;
import dev.boze.api.event.EventTick;
import dev.boze.api.setting.SettingMode;
import dev.boze.api.setting.SettingToggle;
import meteordevelopment.orbit.EventHandler;
import nekiplay.bozeplus.events.packets.PacketEvent;
import nekiplay.bozeplus.mixin.minecraft.PlayerMoveC2SPacketAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class SpiderPlus extends ToggleableModule {

	private final SettingMode mode = new SettingMode("Mode", "Bypass mode", new ArrayList<>() {{
		add("Matrix");
	}});

	private int tick = 0;
	private boolean modify = false;
	private boolean start = false;

	private double startY = 0;
	private double lastY = 0;

	private double coff = 0.0000000000326;

	public SpiderPlus() {
		super("Spider", "Climb on walls");
		elements.add(mode);
	}

	@Override
	protected void onEnable() {

	}
	private boolean YGround(double height, double min, double max) {
		String yString = String.valueOf(height);
		yString = yString.substring(yString.indexOf("."));
		double y = Double.parseDouble(yString);
		return y >= min && y <= max;
	}

	private double RGround(double height) {
		String yString = String.valueOf(height);
		yString = yString.substring(yString.indexOf("."));
		return Double.parseDouble(yString);
	}
	private void work(Packet<?> packet) {
		if (modify) {
			if (packet instanceof PlayerMoveC2SPacket move) {
				assert MinecraftClient.getInstance().player != null;
				double y = MinecraftClient.getInstance().player.getY();
				y = move.getY(y);

				if (YGround(y, RGround(startY) - 0.1, RGround(startY) + 0.1)) {
					((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
				}
				if (MinecraftClient.getInstance().player.isOnGround() && block) {
					block = false;
					startY = MinecraftClient.getInstance().player.getPos().y;
					start = false;
				}
			}
		} else {
			assert MinecraftClient.getInstance().player != null;
			if (MinecraftClient.getInstance().player.isOnGround() && block) {
				block = false;
				startY = MinecraftClient.getInstance().player.getPos().y;
				start = false;
			}
		}
	}

	@EventHandler
	public void onSendPacket(PacketEvent.Send event) {
		work(event.packet);
	}

	@EventHandler
	public void onSentPacket(PacketEvent.Sent event) {
		work(event.packet);
	}

	private boolean block = false;

	@EventHandler
	public void onTickEventPre(EventTick.Pre event) {
		if (modify) {
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			double y = player.getPos().y;
			if (lastY == y && tick > 1) {
				block = true;
			} else {
				lastY = y;
			}
		}
	}

	@EventHandler
	public void onTickEventPost(EventTick.Post event) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		assert player != null;
		Vec3d pl_velocity = player.getVelocity();
		Vec3d pos = player.getPos();
		ClientPlayNetworkHandler h = MinecraftClient.getInstance().getNetworkHandler();
		modify = player.horizontalCollision;
		if (MinecraftClient.getInstance().player.isOnGround()) {
			block = false;
			startY = MinecraftClient.getInstance().player.getPos().y;
			start = false;
		}
		if (player.horizontalCollision) {
			if (!start) {
				start = true;
				startY = MinecraftClient.getInstance().player.getPos().y;
				lastY = MinecraftClient.getInstance().player.getY();
			}
			if (!block) {
				if (tick == 0) {
					MinecraftClient.getInstance().player.setVelocity(pl_velocity.x, 0.41999998688698, pl_velocity.z);
					tick = 1;
				} else if (tick == 1) {
					MinecraftClient.getInstance().player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.08679999325 - coff, pl_velocity.z);
					tick = 2;
				} else if (tick == 2) {
					MinecraftClient.getInstance().player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.17186398826 - coff, pl_velocity.z);
					tick = 0;
				}
			}
		}
		else {
			modify = false;
			tick = 0;
		}
	}
}
