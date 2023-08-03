package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.MixinPlugin;

public class MapModIntegration extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Boolean> baritoneGotoInContextMenu = sgGeneral.add(new BoolSetting.Builder()
		.name("baritone-goto-in-context-menu")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> showBlockInContextMenu = sgGeneral.add(new BoolSetting.Builder()
		.name("show-block-in-context-menu")
		.defaultValue(true)
		.visible(() -> MixinPlugin.isXaeroWorldMapresent)
		.build()
	);

	private final Setting<Boolean> baritoneGotoInWaypointsMenu = sgGeneral.add(new BoolSetting.Builder()
		.name("baritone-goto-in-waypoints-menu")
		.defaultValue(true)
		.visible(() -> MixinPlugin.isJourneyMapPresent || MixinPlugin.isXaeroMiniMapresent)
		.build()
	);

	public MapModIntegration() {
		super(MeteorPlus.CATEGORY, "map-mod-integration", "Added baritone goto support to journey map & xaero map");
	}

	public boolean baritoneGotoInContextMenu() {
		return isActive() && baritoneGotoInContextMenu.get();
	}

	public boolean showBlockInContextMenu() {
		return isActive() && showBlockInContextMenu.get();
	}

	public boolean baritoneGotoInWaypointsMenu() {
		return isActive() && baritoneGotoInWaypointsMenu.get();
	}
}
