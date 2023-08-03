package olejka.meteorplus.mixin.xaero.minimap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
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
	private final GuiWaypoints gutWaypoints = (GuiWaypoints)(Object) this;
	@Shadow(remap = false)
	private WaypointWorld displayedWorld;
	@Shadow(remap = false)
	private ArrayList<Waypoint> waypointsSorted;
	@Shadow(remap = false)
	private WaypointsManager waypointsManager;
	@Shadow(remap = false)
	private ConcurrentSkipListSet<Integer> selectedListSet;
	@Unique
	private ButtonWidget gotoButton;
	protected GuiWaypointsMixin() {
		super(Text.of("Waypoints"));
	}

	@Inject(method = "init", at = @At("HEAD"), remap = false)
	private void init(CallbackInfo ci) {
		addDrawableChild(gotoButton = new MyTinyButton(this.width / 2 - 120, this.height - 53, Text.literal(I18n.translate("gui.xaero_waypoint_teleport", new Object[0]) + " (T)"), (b) -> {

			Waypoint wp = getWaypoint((Integer)this.selectedListSet.first());
			GoalBlock goal = new GoalBlock(new BlockPos(wp.getX(), wp.getY(), wp.getZ()));
			BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
		}));
	}

	@Unique
	private Waypoint getWaypoint(int slotIndex) {
		Waypoint waypoint = null;
		if (slotIndex < displayedWorld.getCurrentSet().getList().size()) {
			waypoint = (Waypoint)waypointsSorted.get(slotIndex);
		} else if (waypointsManager.getServerWaypoints() != null) {
			int serverWPIndex = slotIndex - displayedWorld.getCurrentSet().getList().size();
			if (serverWPIndex < waypointsManager.getServerWaypoints().size()) {
				waypoint = (Waypoint)waypointsManager.getServerWaypoints().get(serverWPIndex);
			}
		}

		return waypoint;
	}
}
