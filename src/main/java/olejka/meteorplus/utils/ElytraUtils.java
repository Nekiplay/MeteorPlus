package olejka.meteorplus.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class ElytraUtils {
	public static void startFly(MinecraftClient mc) {
		if (mc.player != null && mc.player.networkHandler != null) {
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
		}
	}
}
