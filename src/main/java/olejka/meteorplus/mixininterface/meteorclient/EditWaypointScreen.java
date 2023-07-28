package olejka.meteorplus.mixininterface.meteorclient;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.EditSystemScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import olejka.meteorplus.mixin.meteorclient.WaypointsModuleMixin;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class EditWaypointScreen extends EditSystemScreen<Waypoint> {
	public EditWaypointScreen(GuiTheme theme, Waypoint value, Runnable reload) {
		super(theme, value, reload);
	}

	@Override
	public Waypoint create() {
		return new Waypoint.Builder()
			.pos(mc.player.getBlockPos().up(2))
			.dimension(PlayerUtils.getDimension())
			.build();
	}

	@Override
	public boolean save() {
		return !isNew || Waypoints.get().add(value);

	}

	@Override
	public Settings getSettings() {
		return value.settings;
	}
}
