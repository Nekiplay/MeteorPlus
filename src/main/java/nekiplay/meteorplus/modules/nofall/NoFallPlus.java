package nekiplay.meteorplus.modules.nofall;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlus;
import nekiplay.meteorplus.modules.nofall.modes.Eclip;

public class NoFallPlus extends Module {
	public NoFallPlus() {
		super(MeteorPlus.CATEGORY, "no-fall+", "Bypass fall damage or reduce fall damage");
		onModeChanged(mode.get());
	}

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
