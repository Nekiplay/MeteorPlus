package olejka.meteorplus.modules.speed;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import olejka.meteorplus.modules.spider.SpiderModes;
import olejka.meteorplus.modules.spider.SpiderPlus;

public class SpeedMode {
	protected final MinecraftClient mc;
	protected final SpeedPlus settings;
	private final SpeedModes type;

	public SpeedMode(SpeedModes type) {
		this.settings = Modules.get().get(SpeedPlus.class);
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
