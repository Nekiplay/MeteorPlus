package olejka.meteorplus.modules.journeymap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.ModPopupMenu;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.fabric.FabricEvents;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.text.Text;
import olejka.meteorplus.modules.MapModIntegration;

import static olejka.meteorplus.MeteorPlus.LOG;
import static olejka.meteorplus.MeteorPlus.LOGPREFIX;

public class JourneyMapMeteorPlus implements IClientPlugin {
	public final String JourneyMapLOGPREFIX = "[Journey Map]";

	@Override
	public void initialize(final IClientAPI jmClientApi) {
		LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " loading Journey Map integrate");

		FabricEvents.FULLSCREEN_POPUP_MENU_EVENT.register(event -> {
			LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " register fullscreen Journey Map");
			addContextMenu(event.getPopupMenu());
			LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " register fullscreen Journey Map done");
		});

		FabricEvents.WAYPOINT_POPUP_MENU_EVENT.register(event -> {
			LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " register waypoints Journey Map");
			addContextMenu(event.getPopupMenu());
			LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " register waypoints Journey Map done");
		});

		LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " Journey Map integrate loaded");
	}

	private void addContextMenu(ModPopupMenu popupMenu2) {
		if (Modules.get().get(MapModIntegration.class).baritoneGotoInContextMenu()) {
			popupMenu2.addMenuItem(Text.translatable("journey.map.goto").getString(), p -> {
				GoalBlock goal = new GoalBlock(p.up());
				BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
			});
		}
	}

	@Override
	public String getModId() {
		return "meteorplus";
	}

	@Override
	public void onEvent(ClientEvent event) {

	}
}
