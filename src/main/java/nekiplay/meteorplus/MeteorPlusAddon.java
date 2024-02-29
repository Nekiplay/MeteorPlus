package nekiplay.meteorplus;

import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import nekiplay.meteorplus.features.commands.*;
import nekiplay.meteorplus.features.modules.combat.AutoLeave;
import nekiplay.meteorplus.features.modules.combat.TriggerBot;
import nekiplay.meteorplus.features.modules.misc.AutoAccept;
import nekiplay.meteorplus.features.modules.misc.ChatGPT;
import nekiplay.meteorplus.features.modules.misc.ChatPrefix;
import nekiplay.meteorplus.features.modules.misc.MultiTasks;
import nekiplay.meteorplus.features.modules.movement.Freeze;
import nekiplay.meteorplus.features.modules.movement.InventoryMovePlus;
import nekiplay.meteorplus.features.modules.movement.NoJumpDelay;
import nekiplay.meteorplus.features.modules.movement.NoSlowPlus;
import nekiplay.meteorplus.features.modules.player.AutoCraftPlus;
import nekiplay.meteorplus.features.modules.player.AutoDropPlus;
import nekiplay.meteorplus.features.modules.player.MiddleClickExtraPlus;
import nekiplay.meteorplus.features.modules.render.*;
import nekiplay.meteorplus.features.modules.render.holograms.HologramModule;
import nekiplay.meteorplus.features.modules.world.BedrockStorageBruteforce;
import nekiplay.meteorplus.features.modules.world.GhostBlockFixer;
import nekiplay.meteorplus.features.modules.world.SafeMine;
import nekiplay.meteorplus.features.modules.world.autoobsidianmine.AutoObsidianFarm;
import nekiplay.meteorplus.features.modules.combat.AntiBotPlus;
import nekiplay.meteorplus.features.modules.movement.elytrafly.ElytraFlyPlus;
import nekiplay.meteorplus.features.modules.integrations.WhereIsIt;
import nekiplay.meteorplus.features.modules.combat.killaura.KillAuraPlus;
import nekiplay.meteorplus.gui.tabs.HiddenModulesTab;
import nekiplay.meteorplus.hud.TimerPlusCharge;
import nekiplay.meteorplus.features.modules.integrations.LitematicaPrinter;
import nekiplay.meteorplus.features.modules.integrations.MapIntegration;
import nekiplay.meteorplus.features.modules.world.timer.TimerPlus;
import nekiplay.meteorplus.items.ModItems;
import net.fabricmc.loader.api.FabricLoader;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import net.minecraft.item.ItemStack;
import nekiplay.meteorplus.features.modules.movement.fastladder.FastLadderPlus;
import nekiplay.meteorplus.features.modules.movement.fly.FlyPlus;
import nekiplay.meteorplus.features.modules.movement.jesus.JesusPlus;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallPlus;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedPlus;
import nekiplay.meteorplus.features.modules.movement.spider.SpiderPlus;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nekiplay.meteorplus.MixinPlugin.*;

public class MeteorPlusAddon extends MeteorAddon {
	public static final Logger LOG = LoggerFactory.getLogger(MeteorPlusAddon.class);
	public static final ItemStack logo_mods_item = ModItems.LOGO_MODS_ITEM.getDefaultStack();

	public static final Category CATEGORYMODS = new Category("Integrations", logo_mods_item);
	public static final HudGroup HUD_GROUP = new HudGroup("Meteor+ Hud");
	public static final String LOGPREFIX = "[Meteor+]";

	private static MeteorPlusAddon instance;

	public static MeteorPlusAddon getInstance() {
		return instance;
	}

	@Override
	public void onInitialize() {
		instance = this;

		LOG.info(LOGPREFIX + " Initializing...");

		if (isXaeroWorldMapresent) {
			if (!isBaritonePresent) {
				LOG.warn(LOGPREFIX + " [Baritone] not found, disabling Xaero's World Map improvement");
			}
		}
		if (isJourneyMapPresent) {
			if (!isBaritonePresent) {
				LOG.warn(LOGPREFIX + " [Baritone] not found, disabling Journey Map improvement");
			}
		}
		if (!isBaritonePresent) {
			LOG.warn(LOGPREFIX + " [Baritone] not found, disabling Freecam and Waypoints improvement");
		}
		if (!isWhereIsIt) {
			LOG.warn(LOGPREFIX + " [Where is it] not found, disabling ChestTracker improvement");
		}

		//region Commands
		LOG.info(LOGPREFIX + " Initializing commands...");

		Commands.add(new ItemRawIdCommand());
		Commands.add(new Eclip());
		Commands.add(new ClearInventory());
		if (isBaritonePresent) {
			Commands.add(new GotoPlus());
		}
		Commands.add(new GPT());

		LOG.info(LOGPREFIX + " Loaded commands");
		//endregion

		LOG.info(LOGPREFIX + " Initializing better chat custom head...");
		BetterChat.registerCustomHead("[Meteor+]", new Identifier("meteorplus", "chat/icon.png"));
		LOG.info(LOGPREFIX + " Loaded better chat");


		//region Modules
		LOG.info(LOGPREFIX + " Initializing modules...");
		Modules modules = Modules.get();

		modules.add(new HologramModule());
		modules.add(new ChatPrefix());
		modules.add(new ChatGPT());
		modules.add(new ItemHighlightPlus());
		modules.add(new FastLadderPlus());
		modules.add(new TriggerBot());
		modules.add(new EyeFinder());
		modules.add(new InventoryMovePlus());
		modules.add(new MiddleClickExtraPlus());
		modules.add(new AutoDropPlus());
		modules.add(new NoFallPlus());
		modules.add(new TimerPlus());
		modules.add(new SpeedPlus());
		modules.add(new FlyPlus());
		modules.add(new SpiderPlus());
		modules.add(new JesusPlus());
		modules.add(new BedrockStorageBruteforce());
		modules.add(new AutoCraftPlus());
		modules.add(new AutoObsidianFarm());
		modules.add(new XrayBruteforce());
		modules.add(new AutoLeave());
		modules.add(new AutoAccept());
		modules.add(new GhostBlockFixer());
		modules.add(new SafeMine());
		modules.add(new Freeze());
		modules.add(new AntiBotPlus());
		modules.add(new MultiTasks());
		modules.add(new ItemFrameEsp());
		modules.add(new KillAuraPlus());
		modules.add(new ElytraFlyPlus());
		if (!MixinPlugin.isMeteorRejects) {
			modules.add(new NoJumpDelay());
		}
		else {
			LOG.warn(LOGPREFIX + " Meteor Rejects detected, removing No Jump Delay");
		}
		modules.add(new NoSlowPlus());
		//modules.add(new VelocityPlus());
		if (isXaeroWorldMapresent || isJourneyMapPresent) {
			modules.add(new MapIntegration());
			LOG.info(LOGPREFIX + " Loaded mini-map integration");
		}
		if (MixinPlugin.isLitematicaMapresent) {
			modules.add(new LitematicaPrinter());
			LOG.info(LOGPREFIX + " Loaded litematica integration");
		}
		if (MixinPlugin.isWhereIsIt) {
			modules.add(new WhereIsIt());
			LOG.info(LOGPREFIX + " Loaded where is it integration");
		}
		LOG.info(LOGPREFIX + " Loaded modules");
		//endregion

		//region Hud
		LOG.info(LOGPREFIX + " Initializing hud...");

		Hud.get().register(TimerPlusCharge.INFO);

		LOG.info(LOGPREFIX + " Loaded hud");
		//endregion

		//region Tabs
		LOG.info(LOGPREFIX + " Initializing tabs...");

		Tabs.add(new HiddenModulesTab());

		LOG.info(LOGPREFIX + " Loaded tabs");
		//endregion
		LOG.info(LOGPREFIX + " Full loaded");
	}

	@Override
	public void onRegisterCategories() {
		LOG.info(LOGPREFIX + " registering categories...");
		if (isXaeroWorldMapresent ||
			isJourneyMapPresent ||
			MixinPlugin.isLitematicaMapresent ||
			MixinPlugin.isWhereIsIt
		) {
			Modules.registerCategory(CATEGORYMODS);
		}
		//Modules.registerCategory(CATEGORY);
		LOG.info(LOGPREFIX + " register categories");
	}

	@Override
	public String getWebsite() {
		return "https://meteor-plus.com/";
	}

	@Override
	public GithubRepo getRepo() {
		return new GithubRepo("Nekiplay", "MeteorPlus", "main");
	}

	@Override
	public String getCommit() {
		String commit = FabricLoader
			.getInstance()
			.getModContainer("meteorplus")
			.get().getMetadata()
			.getCustomValue("github:sha")
			.getAsString();
		return commit.isEmpty() ? null : commit.trim();
	}

	@Override
	public String getPackage() {
		return "nekiplay.meteorplus";
	}
}
