package nekiplay.meteorplus.features.modules.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import nekiplay.meteorplus.features.modules.nofall.NoFallModes;
import nekiplay.meteorplus.features.modules.nofall.NofallMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.shape.VoxelShape;

import java.util.Iterator;

public class Vulcan277 extends NofallMode  {

	public Vulcan277() {
		super(NoFallModes.Vulcan_2dot7dot7);
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		if (event.packet instanceof PlayerMoveC2SPacket) {
			PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket)event.packet;
			PlayerMoveC2SPacketAccessor accessor = (PlayerMoveC2SPacketAccessor)packet;

			if (mc.player.fallDistance > 7.0) {
				((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
				mc.player.fallDistance = 0f;
				var vel = mc.player.getVelocity();
				mc.player.setVelocity(vel.x, 0, vel.z);
			}
		}
	}
}
