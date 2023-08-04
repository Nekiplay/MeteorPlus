package olejka.meteorplus.mixin.xaerosminimap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import olejka.meteorplus.modules.MapModIntegration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.gui.GuiWaypoints;
import xaero.common.gui.MyTinyButton;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointWorld;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;

@Mixin(GuiWaypoints.class)
public class GuiWaypointsMixin extends Screen {
	@Shadow(remap = false)
	private WaypointWorld displayedWorld;
	@Shadow(remap = false)
	private ArrayList<Waypoint> waypointsSorted;
	@Shadow(remap = false)
	private WaypointsManager waypointsManager;
	@Shadow(remap = false)
	private ConcurrentSkipListSet<Integer> selectedListSet;
	protected GuiWaypointsMixin() {
		super(Text.of("Waypoints"));
	}

	@Inject(method = "init", at = @At("HEAD"), remap = false)
	private void init(CallbackInfo ci) {
		if (Modules.get().get(MapModIntegration.class).baritoneGotoInWaypointsMenu()) {
			addDrawableChild(new MyTinyButton(this.width / 2 - 120, this.height - 53, Text.literal("Goto"), (b) -> {
				Waypoint wp = getWaypoint(this.selectedListSet.first());
				GoalBlock goal = new GoalBlock(new BlockPos(wp.getX(), wp.getY(), wp.getZ()));
				BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
			}));
		}
	}

	@Unique
	private Waypoint getWaypoint(int slotIndex) {
		Waypoint waypoint = null;
		if (slotIndex < displayedWorld.getCurrentSet().getList().size()) {
			waypoint = waypointsSorted.get(slotIndex);
		} else if (waypointsManager.getServerWaypoints() != null) {
			int serverWPIndex = slotIndex - displayedWorld.getCurrentSet().getList().size();
			if (serverWPIndex < waypointsManager.getServerWaypoints().size()) {
				waypoint = waypointsManager.getServerWaypoints().get(serverWPIndex);
			}
		}

		return waypoint;
	}
}
