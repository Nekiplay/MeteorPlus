package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import olejka.meteorplus.MeteorPlus;

public class SpiderPlus extends Module {
	public SpiderPlus() {
		super(MeteorPlus.CATEGORY, "spider-plus", "Matrix spider.");
	}
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
		.name("delay")
		.description("delay.")
		.defaultValue(2)
		.build()
	);
	private final Setting<Double> motion = sgGeneral.add(new DoubleSetting.Builder()
		.name("motion")
		.description("motion.")
		.defaultValue(2)
		.build()
	);
	private final Setting<Double> motionLinit = sgGeneral.add(new DoubleSetting.Builder()
		.name("motion-limit")
		.description("motion-limit.")
		.defaultValue(2)
		.build()
	);


	private void onSendPacket(PacketEvent.Send event) {
		if (mc.player.getAbilities().creativeMode
			|| !(event.packet instanceof PlayerMoveC2SPacket)
			|| ((IPlayerMoveC2SPacket) event.packet).getTag() == 1337) return;
			((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(true);
	}

	private int tick = 0;

	@Override
	public void onActivate() {
		tick = 0;
	}

	private boolean isSide = false;

	@EventHandler
	private void onTick(TickEvent.Post event) {
		ClientPlayerEntity player = mc.player;
		Vec3d pl_velocity = player.getVelocity();
		isSide = player.horizontalCollision;
		if (tick == 0) {
			if (player.horizontalCollision) {
				if (pl_velocity.getY() <= motionLinit.get()) {
					player.jump();
					tick = delay.get();
				}
			}
		}
		else {
			tick--;
		}
	}
}
