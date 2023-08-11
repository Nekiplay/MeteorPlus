package nekiplay.meteorplus.modules.integrations.journeymap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.ModPopupMenu;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.fabric.FabricEvents;
import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.meteorplus.MeteorPlus;
import nekiplay.meteorplus.modules.integrations.MapIntegration;
import net.minecraft.text.Text;

public class JourneyMapMeteorPlus implements IClientPlugin {
	public final String JourneyMapLOGPREFIX = "[Journey Map]";
	@Override
	public void initialize(final IClientAPI jmClientApi) {
		MapIntegration mapIntegration = Modules.get().get(MapIntegration.class);


		MeteorPlus.LOG.info(MeteorPlus.LOGPREFIX + " " + JourneyMapLOGPREFIX + " loading Journey Map integrate");
		FabricEvents.FULLSCREEN_POPUP_MENU_EVENT.register(event -> {
			MeteorPlus.LOG.info(MeteorPlus.LOGPREFIX + " " + JourneyMapLOGPREFIX + " register fullscreen Journey Map");
			ModPopupMenu popupMenu = event.getPopupMenu();

			if (mapIntegration.baritoneGoto.get()) {
				popupMenu.addMenuItem(Text.translatable("journey.map.goto").getString(), p -> {
					GoalBlock goal = new GoalBlock(p.up());
					BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
				});
			}

			MeteorPlus.LOG.info(MeteorPlus.LOGPREFIX + " " + JourneyMapLOGPREFIX + " register fullscreen Journey Map done");
		});

		FabricEvents.WAYPOINT_POPUP_MENU_EVENT.register(event -> {
			MeteorPlus.LOG.info(MeteorPlus.LOGPREFIX + " " + JourneyMapLOGPREFIX + " register waypoints Journey Map");
			ModPopupMenu popupMenu = event.getPopupMenu();

			if (mapIntegration != null && mapIntegration.baritoneGoto.get()) {
				popupMenu.addMenuItem(Text.translatable("journey.map.goto").getString(), p -> {
					GoalBlock goal = new GoalBlock(p.up());
					BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
				});
			}

			MeteorPlus.LOG.info(MeteorPlus.LOGPREFIX + " " + JourneyMapLOGPREFIX + " register waypoints Journey Map done");
		});

		MeteorPlus.LOG.info(MeteorPlus.LOGPREFIX + " " + JourneyMapLOGPREFIX + " initializing tab...");


		MeteorPlus.LOG.info(MeteorPlus.LOGPREFIX + " " + JourneyMapLOGPREFIX + " loaded tab");


		MeteorPlus.LOG.info(MeteorPlus.LOGPREFIX + " " + JourneyMapLOGPREFIX + " Journey Map integrate loaded");
	}

	@Override
	public String getModId() {
		return "meteorplus";
	}

	@Override
	public void onEvent(ClientEvent event) {

	}
}
