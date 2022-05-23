package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import olejka.meteorplus.MeteorPlus;

public class NoFallPlus extends Module {
	public NoFallPlus() {
		super(MeteorPlus.CATEGORY, "NoFall-plus", "Prevent you from fall damage.");
	}

	@EventHandler
	public void tickEventPre(TickEvent.Pre event) {
		ClientPlayerEntity player = mc.player;
		if (player != null) {
			info("Fall distance: " + mc.player.fallDistance);
			if (mc.player.fallDistance % 3 == 0) {
				mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
			}
		}
	}
}
