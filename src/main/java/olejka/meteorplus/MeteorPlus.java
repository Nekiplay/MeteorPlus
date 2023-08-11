package olejka.meteorplus;

import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import net.fabricmc.loader.api.FabricLoader;
import olejka.meteorplus.commands.Eclip;
import olejka.meteorplus.gui.tabs.HiddenModulesTab;
import olejka.meteorplus.hud.MeteorPlusLogoHud;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import net.minecraft.item.Items;
import olejka.meteorplus.hud.TimerPlusCharge;
import olejka.meteorplus.modules.*;
import olejka.meteorplus.modules.fastladder.FastLadderPlus;
import olejka.meteorplus.modules.fly.FlyPlus;
import olejka.meteorplus.modules.integrations.LitematicaPrinter;
import olejka.meteorplus.modules.integrations.MapIntegration;
import olejka.meteorplus.modules.jesus.JesusPlus;
import olejka.meteorplus.modules.nofall.NoFallPlus;
import olejka.meteorplus.modules.speed.SpeedPlus;
import olejka.meteorplus.modules.spider.SpiderPlus;
import olejka.meteorplus.modules.timer.TimerPlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeteorPlus extends MeteorAddon {
	public static final Logger LOG = LoggerFactory.getLogger(MeteorPlus.class);
	public static final Category CATEGORY = new Category("Meteor+", Items.EMERALD_BLOCK.getDefaultStack());
	public static final Category CATEGORYMODS = new Category("Meteor+ Mods", Items.REDSTONE_BLOCK.getDefaultStack());
	public static final HudGroup HUD_GROUP = new HudGroup("Meteor+ Hud");
	public static final String LOGPREFIX = "[Meteor+]";

	private static MeteorPlus instance;

	public static MeteorPlus getInstance() {
		return instance;
	}


	@Override
	public void onInitialize() {
		instance = this;
		LOG.info(LOGPREFIX + " initializing...");

		//region Commands
		LOG.info(LOGPREFIX + " initializing commands...");

		Commands.add(new Eclip());

		LOG.info(LOGPREFIX + " loaded commands");
		//endregion
		//region Modules
		LOG.info(LOGPREFIX + " initializing modules...");
		Modules modules = Modules.get();

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
		modules.add(new BoatAura());
		modules.add(new BedrockStorageBruteforce());
		modules.add(new AutoCraftPlus());
		modules.add(new AutoObsidianMine());
		modules.add(new XrayBruteforce());
		modules.add(new AutoLeave());
		modules.add(new AutoAccept());
		modules.add(new GhostBlockFixer());
		modules.add(new SafeMine());
		modules.add(new Freeze());
		modules.add(new AntiBotPlus());
		modules.add(new MultiTasks());
		if (MixinPlugin.isXaeroWorldMapresent || MixinPlugin.isJourneyMapPresent) {
			modules.add(new MapIntegration());
			LOG.info(LOGPREFIX + " loaded mini-map integration");
		}
		if (MixinPlugin.isLitematicaMapresent) {
			modules.add(new LitematicaPrinter());
			LOG.info(LOGPREFIX + " loaded litematica integration");
		}
		LOG.info(LOGPREFIX + " loaded modules");
		//endregion

		//region Hud
		LOG.info(LOGPREFIX + " initializing hud...");

		Hud.get().register(MeteorPlusLogoHud.INFO);
		Hud.get().register(TimerPlusCharge.INFO);

		LOG.info(LOGPREFIX + " loaded hud");
		//endregion

		//region Tabs
		LOG.info(LOGPREFIX + " initializing tabs...");

		Tabs.add(new HiddenModulesTab());

		LOG.info(LOGPREFIX + " loaded tabs");
		//endregion
		LOG.info(LOGPREFIX + " loaded");
	}

	@Override
	public void onRegisterCategories() {
		LOG.info(LOGPREFIX + " registering categories...");
		if (MixinPlugin.isXaeroWorldMapresent ||
			MixinPlugin.isJourneyMapPresent ||
			MixinPlugin.isLitematicaMapresent
		) {
			Modules.registerCategory(CATEGORYMODS);
		}
		Modules.registerCategory(CATEGORY);
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
		return "olejka.meteorplus";
	}
}
