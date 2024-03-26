package nekiplay.meteorplus.features.modules.combat.velocity.modes;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.ESPBlock;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityMode;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityModes;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class Grim extends VelocityMode {
	public Grim() {
		super(VelocityModes.Grim);
	}

	private boolean canCancel = false;

	@Override
	public void onActivate() {
		canCancel = false;
	}

	@Override
	public void onReceivePacket(PacketEvent.Receive event) {
		Packet packet = event.packet;

		if (packet instanceof EntityDamageS2CPacket && ((EntityDamageS2CPacket) packet).entityId() == mc.player.getId()) {
			canCancel = true;
		}

		if ((packet instanceof EntityVelocityUpdateS2CPacket && ((EntityVelocityUpdateS2CPacket) packet).getId() == mc.player.getId() || packet instanceof ExplosionS2CPacket) && canCancel) {
			event.cancel();
			MeteorExecutor.execute(() -> {
                try {
                    MeteorExecutor.executor.wait(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

				mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
				mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, mc.player.getBlockPos(), mc.player.getHorizontalFacing().getOpposite()));
				canCancel = false;
            });
		}

	}
}
