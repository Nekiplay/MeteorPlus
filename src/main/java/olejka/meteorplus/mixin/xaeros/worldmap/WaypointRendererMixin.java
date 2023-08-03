package olejka.meteorplus.mixin.xaeros.worldmap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import olejka.meteorplus.modules.MapModIntegration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointReader;

import java.util.ArrayList;

@Mixin(WaypointReader.class)
public class WaypointRendererMixin {
	@Inject(method = "getRightClickOptions(Lxaero/map/mods/gui/Waypoint;Lxaero/map/gui/IRightClickableElement;)Ljava/util/ArrayList;", at = @At("TAIL"), remap = false, cancellable = true)
	private void getRightClickOptions(Waypoint element, IRightClickableElement target, CallbackInfoReturnable<ArrayList<RightClickOption>> cir) {
		ArrayList<RightClickOption> rightClickOptions = cir.getReturnValue();

		if (Modules.get().get(MapModIntegration.class).baritoneGotoInWaypointsMenu()) {
			rightClickOptions.add((new RightClickOption("journey.map.goto", rightClickOptions.size(), target) {
				public void onAction(Screen screen) {
					GoalBlock goal = new GoalBlock(new BlockPos(element.getX(), element.getY(), element.getZ()));
					BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
				}

				public boolean isActive() {
					return true;
				}
			}).setNameFormatArgs("G"));

		}

		cir.setReturnValue(rightClickOptions);
	}
}
