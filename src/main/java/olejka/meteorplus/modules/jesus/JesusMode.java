package olejka.meteorplus.modules.jesus;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import olejka.meteorplus.modules.speed.SpeedModes;
import olejka.meteorplus.modules.speed.SpeedPlus;

public class JesusMode {
	protected final MinecraftClient mc;
	protected final JesusPlus settings;
	private final JesusModes type;

	public JesusMode(JesusModes type) {
		this.settings = Modules.get().get(JesusPlus.class);
		this.mc = MinecraftClient.getInstance();
		this.type = type;
	}

	public void onSendPacket(PacketEvent.Send event) {}
	public void onSentPacket(PacketEvent.Sent event) {}
	public void onPlayerMoveEvent(PlayerMoveEvent event) {}

	public void onTickEventPre(TickEvent.Pre event) {}
	public void onTickEventPost(TickEvent.Post event) {}

	public void onActivate() {}
	public void onDeactivate() {}
}
