package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import olejka.meteorplus.MeteorPlus;

public class FlyPlus extends Module {
	public FlyPlus() {
		super(MeteorPlus.CATEGORY, "fly-plus", "Bypasses fly.");
	}
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
		.name("Mode")
		.description("Fly mode.")
		.defaultValue(Mode.MatrixExploit)
		.build()
	);

	public enum Mode
	{
		MatrixExploit,
	}

	private final Setting<Double> xspeed = sgGeneral.add(new DoubleSetting.Builder()
		.name("Speed")
		.description("Speed.")
		.defaultValue(1.2)
		.build()
	);

	private int tick = 0;
	private int tick2 = 0;

	@Override
	public void onActivate() {
		tick = 0;
		tick = 2;
		FindItemResult r = InvUtils.find(Items.ELYTRA);
		if (!r.found()) {
			error("Elytra not found");
			toggle();
		}
	}

	@Override
	public void onDeactivate() {
		if (!mc.player.isSpectator()) {
			mc.player.getAbilities().flying = false;
			mc.player.getAbilities().setFlySpeed(0.05f);
			if (mc.player.getAbilities().creativeMode) return;
			mc.player.getAbilities().allowFlying = false;
		}
	}

	public void startFly() {
		mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
	}

	@EventHandler
	public void onTickPre(TickEvent.Pre event) {
		FindItemResult r = InvUtils.find(Items.ELYTRA);

		float yaw = mc.player.getYaw();
		Vec3d forward = Vec3d.fromPolar(0, yaw);
		Vec3d right = Vec3d.fromPolar(0, yaw + 90);

		double velX = 0;
		double velZ = 0;
		double s = xspeed.get();
		double speedValue = 0.01;
		if (mc.options.forwardKey.isPressed()) {
			velX += forward.x - s;
			velZ += forward.z - s;
		}
		if (mc.options.backKey.isPressed()) {
			velX -= forward.x - s;
			velZ -= forward.z - s;
		}

		if (mc.options.rightKey.isPressed()) {
			velX += right.x - s;
			velZ += right.z - s;
		}
		if (mc.options.leftKey.isPressed()) {
			velX -= right.x - s;
			velZ -= right.z - s;
		}

		if (tick == 0) {
			if (r.found()) {
				InvUtils.move().from(r.slot()).toArmor(2);
				startFly();
				startFly();
				InvUtils.move().fromArmor(2).to(r.slot());
				tick = 21;
			}
		}
		else {
			tick--;
		}
		if (tick2 >= 0) {
			fly(0.100000001490116);
			tick2++;
			if (tick2 >= 13) {
				//((IVec3d) mc.player.getVelocity()).set(velX, -0.060000001490116, velZ);
				fly(-0.060000001490116);
				if (tick2 == 16) {
					tick2 = 0;
				}
			}
		}
	}

	private void fly(double y) {
		//mc.player.airStrafingSpeed = xspeed.get().floatValue();

		mc.player.setVelocity(0, y, 0);
		//Vec3d initialVelocity = mc.player.getVelocity();
	}
}
