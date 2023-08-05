package olejka.meteorplus.modules.timer;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.client.MinecraftClient;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.modules.spider.SpiderPlus;

public class TimerMode {
	protected final MinecraftClient mc;
	protected final TimerPlus settings;
	private final TimerModes type;

	public TimerMode(TimerModes type) {
		this.settings = Modules.get().get(TimerPlus.class);;
		this.mc = MinecraftClient.getInstance();
		this.type = type;
	}

	public void onSendPacket(PacketEvent.Send event) {}
	public void onSentPacket(PacketEvent.Sent event) {}

	public void onTickEventPre(TickEvent.Pre event) {}
	public void onTickEventPost(TickEvent.Post event) {}

	public void onActivate() {}
	public void onDeactivate() {}
}
