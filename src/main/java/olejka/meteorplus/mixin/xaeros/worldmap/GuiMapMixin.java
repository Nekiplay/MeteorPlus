package olejka.meteorplus.mixin.xaeros.worldmap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.math.BlockPos;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.gui.tabs.XaerosWorldMapTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.controls.ControlsRegister;
import xaero.map.gui.GuiMap;
import xaero.map.gui.MapTileSelection;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.misc.Misc;
import xaero.map.mods.SupportMods;
import xaero.map.teleport.MapTeleporter;
import xaero.map.world.MapDimension;

import java.util.ArrayList;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(GuiMap.class)
public class GuiMapMixin {
	private final GuiMap guiMap = (GuiMap)(Object) this;
	@Shadow(remap = false)

	private MapTileSelection mapTileSelection;
	@Shadow(remap = false)
	private int rightClickX;
	@Shadow(remap = false)
	private int rightClickY;
	@Shadow(remap = false)
	private int rightClickZ;
	@Shadow(remap = false)
	private MapProcessor mapProcessor;
	@Shadow(remap = false)
	private ButtonWidget exportButton;
	@Shadow(remap = false)
	private void onExportButton(ButtonWidget b) { }
	@Shadow(remap = false)
	private ButtonWidget settingsButton;
	@Shadow(remap = false)
	private void onSettingsButton(ButtonWidget b) { }

	@Inject(method = "getRightClickOptions", at = @At("HEAD"), remap = false, cancellable = true)
	private void rightClickOptins(CallbackInfoReturnable<ArrayList<RightClickOption>> cir) {
		ArrayList<RightClickOption> options = new ArrayList();
		options.add(new RightClickOption("gui.xaero_right_click_map_title", options.size(), guiMap) {
			public void onAction(Screen screen) {
			}
		});
		if (WorldMap.settings.coordinates && (!SupportMods.minimap() || !SupportMods.xaeroMinimap.hidingWaypointCoordinates())) {
			if (this.mapTileSelection != null) {
				String chunkOption = this.mapTileSelection.getStartX() == this.mapTileSelection.getEndX() && this.mapTileSelection.getStartZ() == this.mapTileSelection.getEndZ() ? String.format("C: (%d;%d)", this.mapTileSelection.getLeft(), this.mapTileSelection.getTop()) : String.format("C: (%d;%d):(%d;%d)", this.mapTileSelection.getLeft(), this.mapTileSelection.getTop(), this.mapTileSelection.getRight(), this.mapTileSelection.getBottom());
				options.add(new RightClickOption(chunkOption, options.size(), guiMap) {
					public void onAction(Screen screen) {
					}
				});
			}

			options.add(new RightClickOption(String.format(this.rightClickY != 32767 ? "X: %1$d, Y: %2$d, Z: %3$d" : "X: %1$d, Z: %3$d", this.rightClickX, this.rightClickY, this.rightClickZ), options.size(), guiMap) {
				public void onAction(Screen screen) {
				}
			});
		}
		SettingGroup group = XaerosWorldMapTab.getSettings().getGroup("Full map");
		if (group != null) {
			BoolSetting settings = (BoolSetting)group.get("Baritone goto in context menu");
			if (settings.get()) {
				options.add(new RightClickOption("journey.map.goto", options.size(), guiMap) {
					public void onAction(Screen screen) {
						GoalBlock goal = new GoalBlock(new BlockPos(rightClickX, rightClickY + 1, rightClickZ));
						BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
					}
				});
			}
		}

		if (SupportMods.minimap() && WorldMap.settings.waypoints) {
			options.add((new RightClickOption("gui.xaero_right_click_map_create_waypoint", options.size(), guiMap) {
				public void onAction(Screen screen) {
					SupportMods.xaeroMinimap.createWaypoint(guiMap, rightClickX, rightClickY == 32767 ? 32767 : rightClickY + 1, rightClickZ);
				}
			}).setNameFormatArgs(new Object[]{Misc.getKeyName(SupportMods.xaeroMinimap.getWaypointKeyBinding())}));
			options.add((new RightClickOption("gui.xaero_right_click_map_create_temporary_waypoint", options.size(), guiMap) {
				public void onAction(Screen screen) {
					SupportMods.xaeroMinimap.createTempWaypoint(rightClickX, rightClickY == 32767 ? 32767 : rightClickY + 1, rightClickZ);
				}
			}).setNameFormatArgs(new Object[]{Misc.getKeyName(SupportMods.xaeroMinimap.getTempWaypointKeyBinding())}));
		}

		MapDimension currentDimension = this.mapProcessor.getMapWorld().getCurrentDimension();
		if (mc.interactionManager.hasStatusBars() && (currentDimension == null || !currentDimension.currentMultiworldWritable)) {
			options.add(new RightClickOption("gui.xaero_right_click_map_cant_teleport_world", options.size(), guiMap) {
				public void onAction(Screen screen) {
				}
			});
		} else if (!this.mapProcessor.getMapWorld().isTeleportAllowed() || this.rightClickY == 32767 && mc.interactionManager.hasStatusBars()) {
			if (!this.mapProcessor.getMapWorld().isTeleportAllowed()) {
				options.add(new RightClickOption("gui.xaero_wm_right_click_map_teleport_not_allowed", options.size(), guiMap) {
					public void onAction(Screen screen) {
					}
				});
			} else {
				options.add(new RightClickOption("gui.xaero_right_click_map_cant_teleport", options.size(), guiMap) {
					public void onAction(Screen screen) {
					}
				});
			}
		} else {
			options.add(new RightClickOption("gui.xaero_right_click_map_teleport", options.size(), guiMap) {
				public void onAction(Screen screen) {
					MapDimension currentDimension = mapProcessor.getMapWorld().getCurrentDimension();
					if ((!mc.interactionManager.hasStatusBars() || currentDimension != null && currentDimension.currentMultiworldWritable) && (rightClickY != 32767 || !mc.interactionManager.hasStatusBars())) {
						(new MapTeleporter()).teleport(guiMap, mapProcessor.getMapWorld(), rightClickX, rightClickY == 32767 ? 32767 : rightClickY + 1, rightClickZ);
					}

				}
			});
		}

		if (SupportMods.minimap()) {
			options.add(new RightClickOption("gui.xaero_right_click_map_share_location", options.size(), guiMap) {
				public void onAction(Screen screen) {
					SupportMods.xaeroMinimap.shareLocation(guiMap, rightClickX, rightClickY == 32767 ? 32767 : rightClickY + 1, rightClickZ);
				}
			});
			if (WorldMap.settings.waypoints) {
				options.add((new RightClickOption("gui.xaero_right_click_map_waypoints_menu", options.size(), guiMap) {
					public void onAction(Screen screen) {
						SupportMods.xaeroMinimap.openWaypointsMenu(mc, guiMap);
					}
				}).setNameFormatArgs(new Object[]{Misc.getKeyName(SupportMods.xaeroMinimap.getTempWaypointsMenuKeyBinding())}));
			}
		}

		if (SupportMods.pac()) {
			SupportMods.xaeroPac.addRightClickOptions(guiMap, options, this.mapTileSelection);
		}

		options.add(new RightClickOption("gui.xaero_right_click_box_map_export", options.size(), guiMap) {
			public void onAction(Screen screen) {
				onExportButton(exportButton);
			}
		});
		options.add((new RightClickOption("gui.xaero_right_click_box_map_settings", options.size(), guiMap) {
			public void onAction(Screen screen) {
				onSettingsButton(settingsButton);
			}
		}).setNameFormatArgs(new Object[]{Misc.getKeyName(ControlsRegister.keyOpenSettings)}));
		cir.setReturnValue(options);
	}
}
