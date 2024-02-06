package nekiplay.meteorplus.features.modules.velocity.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import nekiplay.meteorplus.features.modules.velocity.VelocityMode;
import nekiplay.meteorplus.features.modules.velocity.VelocityModes;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class Grim extends VelocityMode {
	public Grim() {
		super(VelocityModes.Grim);
	}

	private int cancelPacket = 6;
	private int resetPersec = 8;
	private int grimTCancel = 0;
	private int updates = 0;

	@Override
	public void onActivate() {
		grimTCancel = 0;
	}

	@Override
	public void onReceivePacket(PacketEvent.Receive event) {
		Packet packet = event.packet;

		if (packet instanceof EntityVelocityUpdateS2CPacket) {
			EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket = (EntityVelocityUpdateS2CPacket)packet;
			if (entityVelocityUpdateS2CPacket.getId() == mc.player.getId()) {
				event.cancel();
				grimTCancel = cancelPacket;
			}
		}
		if (packet instanceof KeepAliveS2CPacket && grimTCancel > 0) {
			event.cancel();
			grimTCancel--;
		}
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		updates++;

		if (resetPersec > 0) {
			if (updates >= 0 || updates >= resetPersec) {
				updates = 0;
				if (grimTCancel > 0){
					grimTCancel--;
				}
			}
		}
	}
}
