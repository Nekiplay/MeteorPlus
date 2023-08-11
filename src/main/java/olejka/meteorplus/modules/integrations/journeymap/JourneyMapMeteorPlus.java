package olejka.meteorplus.modules.integrations.journeymap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.ModPopupMenu;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.fabric.FabricEvents;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.text.Text;
import olejka.meteorplus.modules.integrations.MapIntegration;

import static olejka.meteorplus.MeteorPlus.LOG;
import static olejka.meteorplus.MeteorPlus.LOGPREFIX;

public class JourneyMapMeteorPlus implements IClientPlugin {
	public final String JourneyMapLOGPREFIX = "[Journey Map]";
	@Override
	public void initialize(final IClientAPI jmClientApi) {
		MapIntegration mapIntegration = Modules.get().get(MapIntegration.class);


		LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " loading Journey Map integrate");
		FabricEvents.FULLSCREEN_POPUP_MENU_EVENT.register(event -> {
			LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " register fullscreen Journey Map");
			ModPopupMenu popupMenu = event.getPopupMenu();

			if (mapIntegration.baritoneGoto.get()) {
				popupMenu.addMenuItem(Text.translatable("journey.map.goto").getString(), p -> {
					GoalBlock goal = new GoalBlock(p.up());
					BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
				});
			}

			LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " register fullscreen Journey Map done");
		});

		FabricEvents.WAYPOINT_POPUP_MENU_EVENT.register(event -> {
			LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " register waypoints Journey Map");
			ModPopupMenu popupMenu = event.getPopupMenu();

			if (mapIntegration != null && mapIntegration.baritoneGoto.get()) {
				popupMenu.addMenuItem(Text.translatable("journey.map.goto").getString(), p -> {
					GoalBlock goal = new GoalBlock(p.up());
					BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
				});
			}

			LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " register waypoints Journey Map done");
		});

		LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " initializing tab...");


		LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " loaded tab");


		LOG.info(LOGPREFIX + " " + JourneyMapLOGPREFIX + " Journey Map integrate loaded");
	}

	@Override
	public String getModId() {
		return "meteorplus";
	}

	@Override
	public void onEvent(ClientEvent event) {

	}
}
