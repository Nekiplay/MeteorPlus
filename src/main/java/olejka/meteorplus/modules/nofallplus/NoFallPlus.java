package olejka.meteorplus.modules.nofallplus;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.modules.nofallplus.modes.Eclip;
import olejka.meteorplus.modules.spider.SpiderMode;
import olejka.meteorplus.modules.spider.SpiderModes;
import olejka.meteorplus.modules.spider.modes.Matrix;
import olejka.meteorplus.modules.spider.modes.Vulcan;

public class NoFallPlus extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private NofallMode currentMode;

	public final Setting<NoFallModes> mode = sgGeneral.add(new EnumSetting.Builder<NoFallModes>()
		.name("mode")
		.description("The method of applying nofall.")
		.defaultValue(NoFallModes.ElytraClip)
		.onModuleActivated(spiderModesSetting -> onModeChanged(spiderModesSetting.get()))
		.onChanged(this::onModeChanged)
		.build()
	);

	public NoFallPlus() {
		super(MeteorPlus.CATEGORY, "no-fall-plus", "Bypass fall damage or reduce fall damage");
		onModeChanged(mode.get());
	}

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


	private void onModeChanged(NoFallModes mode) {
		switch (mode) {
			case ElytraClip -> currentMode = new Eclip();
		}
	}
}
