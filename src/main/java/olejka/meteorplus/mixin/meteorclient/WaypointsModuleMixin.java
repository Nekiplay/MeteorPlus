package olejka.meteorplus.mixin.meteorclient;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import baritone.api.pathing.goals.GoalGetToBlock;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.WaypointsModule;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.Utils;
import olejka.meteorplus.mixininterface.meteorclient.EditWaypointScreen;
import olejka.meteorplus.mixininterface.meteorclient.WIcon;
import olejka.meteorplus.modules.speed.SpeedModes;
import olejka.meteorplus.modules.speed.modes.*;
import org.spongepowered.asm.mixin.Mixin;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.render.color.Color.GRAY;

@Mixin(WaypointsModule.class)
public class WaypointsModuleMixin {
	private final Freecam waypoints = (Freecam)(Object) this;

	private final SettingGroup meteorPlusTab = waypoints.settings.createGroup("Meteor Plus");

	private final Setting<Boolean> showDistance = meteorPlusTab.add(new BoolSetting.Builder()
		.name("show-distance")
		.description("show-distance-in-this-gui.")
		.build()
	);

	private void onSpeedModeChanged(Boolean aBoolean) {
		if (aBoolean) {

		}
	}

	@Inject(method = "getWidget", at = @At("HEAD"), remap = false, cancellable = true)
	private void getWidget(GuiTheme theme, CallbackInfoReturnable<WWidget> cir) {
		if (!Utils.canUpdate()) {
			cir.setReturnValue(theme.label("You need to be in a world."));
		}

		WTable table = theme.table();
		initTable(theme, table);
		cir.setReturnValue(table);
	}

	@Unique
	private void initTable(GuiTheme theme, WTable table) {
		table.clear();

		for (Waypoint waypoint : Waypoints.get()) {
			boolean validDim = Waypoints.checkDimension(waypoint);

			table.add(new WIcon(waypoint));

			WLabel name = table.add(theme.label(waypoint.name.get())).expandCellX().widget();
			if (showDistance.get()) {
				if (mc.player == null)
					name = table.add(theme.label(waypoint.name.get() + " (unknown)")).expandCellX().widget();
				else {
					long distance = Math.round(mc.player.getPos().distanceTo(waypoint.getPos().toCenterPos()));
					name = table.add(theme.label(waypoint.name.get() + " (" + distance + "m)")).expandCellX().widget();
				}
			}
			if (!validDim) name.color = GRAY;

			WCheckbox visible = table.add(theme.checkbox(waypoint.visible.get())).widget();
			visible.action = () -> {
				waypoint.visible.set(visible.checked);
				Waypoints.get().save();
			};

			WButton edit = table.add(theme.button(GuiRenderer.EDIT)).widget();
			edit.action = () -> mc.setScreen(new EditWaypointScreen(theme, waypoint, null));

			// Goto
			if (validDim) {
				WButton gotoB = table.add(theme.button("Goto")).widget();
				gotoB.action = () -> {
					IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
					if (baritone.getPathingBehavior().isPathing()) baritone.getPathingBehavior().cancelEverything();
					baritone.getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(waypoint.getPos()));
				};
			}

			WMinus remove = table.add(theme.minus()).widget();
			remove.action = () -> {
				Waypoints.get().remove(waypoint);
				initTable(theme, table);
			};

			table.row();
		}

		table.add(theme.horizontalSeparator()).expandX();
		table.row();

		WButton create = table.add(theme.button("Create")).expandX().widget();
		create.action = () -> mc.setScreen(new EditWaypointScreen(theme, null, () -> initTable(theme, table)));
	}
}
