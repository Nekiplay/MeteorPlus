package olejka.meteorplus.modules.nofallplus;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.client.MinecraftClient;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.modules.spider.SpiderModes;
import olejka.meteorplus.modules.spider.SpiderPlus;

public class NofallMode {
	protected final MinecraftClient mc;
	protected final NoFallPlus settings;
	private final NoFallModes type;

	public NofallMode(NoFallModes type) {
		this.settings = MeteorPlus.getInstance().noFallPlus;
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
