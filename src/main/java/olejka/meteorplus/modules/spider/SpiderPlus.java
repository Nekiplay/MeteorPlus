package olejka.meteorplus.modules.spider;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.modules.spider.modes.Matrix;
import olejka.meteorplus.modules.spider.modes.Vulcan;

public class SpiderPlus extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public SpiderPlus() {
		super(MeteorPlus.CATEGORY, "spider-plus", "Bypass spider");
		onSpiderModeChanged(spiderMode.get());
	}

	public final Setting<SpiderModes> spiderMode = sgGeneral.add(new EnumSetting.Builder<SpiderModes>()
		.name("mode")
		.description("The method of applying spider.")
		.defaultValue(SpiderModes.Matrix)
		.onModuleActivated(spiderModesSetting -> onSpiderModeChanged(spiderModesSetting.get()))
		.onChanged(this::onSpiderModeChanged)
		.build()
	);

	private SpiderMode currentMode;

	@Override
	public void onActivate() {
		currentMode.onActivate();
	}

	@Override
	public void onDeactivate() {
		currentMode.onDeactivate();
	}

	@EventHandler
	private void onPreTick(TickEvent.Pre event) {
		currentMode.onTickEventPre(event);
	}

	@EventHandler
	private void onPostTick(TickEvent.Post event) {
		currentMode.onTickEventPost(event);
	}
	@EventHandler
	public void onSendPacket(PacketEvent.Send event) {
		currentMode.onSendPacket(event);
	}
	@EventHandler
	public void onSentPacket(PacketEvent.Sent event) {
		currentMode.onSentPacket(event);
	}


	private void onSpiderModeChanged(SpiderModes mode) {
		switch (mode) {
			case Matrix:   currentMode = new Matrix(); break;
			case Vulcan:   currentMode = new Vulcan(); break;
		}
	}
}
