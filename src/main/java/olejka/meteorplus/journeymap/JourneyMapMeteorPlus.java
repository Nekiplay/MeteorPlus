package olejka.meteorplus.journeymap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.ModPopupMenu;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.fabric.FabricEvents;
import olejka.meteorplus.MeteorPlus;

import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.*;

public class JourneyMapMeteorPlus implements IClientPlugin {
	// API reference
	private IClientAPI jmAPI = null;

	private static JourneyMapMeteorPlus INSTANCE;

	public JourneyMapMeteorPlus()
	{
		INSTANCE = this;
	}

	@Override
	public void initialize(final IClientAPI jmClientApi) {

		MeteorPlus.LOG.info("Loading Journey Map integrate");
		jmAPI = jmClientApi;
		FabricEvents.FULLSCREEN_POPUP_MENU_EVENT.register(event -> {
			MeteorPlus.LOG.info("Register fullscreen Journey Map");
			ModPopupMenu popupMenu = event.getPopupMenu();

			popupMenu.addMenuItem("Goto", p -> {
				GoalBlock goal = new GoalBlock(p.up());
				BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
			});

			MeteorPlus.LOG.info("Register fullscreen Journey Map done");
		});

		FabricEvents.WAYPOINT_POPUP_MENU_EVENT.register(event -> {
			MeteorPlus.LOG.info("Register waypoints Journey Map");
			ModPopupMenu popupMenu = event.getPopupMenu();

			popupMenu.addMenuItem("Goto", p -> {
				GoalBlock goal = new GoalBlock(p.up());
				BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
			});

			MeteorPlus.LOG.info("Register waypoints Journey Map done");
		});

		// Subscribe to desired ClientEvent types from JourneyMap
		this.jmAPI.subscribe(getModId(), EnumSet.of(DEATH_WAYPOINT, MAPPING_STARTED, MAPPING_STOPPED, REGISTRY));

		MeteorPlus.LOG.info("Journey Map integrate loaded");
	}

	@Override
	public String getModId() {
		return "meteorplus";
	}

	@Override
	public void onEvent(ClientEvent event) {

	}
}
