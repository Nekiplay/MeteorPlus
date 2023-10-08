package nekiplay.meteorplus;

import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import nekiplay.meteorplus.commands.ClearInventory;
import nekiplay.meteorplus.commands.Eclip;
import nekiplay.meteorplus.commands.GPT;
import nekiplay.meteorplus.commands.GotoPlus;
import nekiplay.meteorplus.features.modules.autoobsidianmine.AutoObsidianFarm;
import nekiplay.meteorplus.features.modules.killaura.KillAuraPlus;
import nekiplay.meteorplus.gui.tabs.HiddenModulesTab;
import nekiplay.meteorplus.hud.TimerPlusCharge;
import nekiplay.meteorplus.features.modules.*;
import nekiplay.meteorplus.features.modules.integrations.LitematicaPrinter;
import nekiplay.meteorplus.features.modules.integrations.MapIntegration;
import nekiplay.meteorplus.features.modules.timer.TimerPlus;
import net.fabricmc.loader.api.FabricLoader;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import net.minecraft.item.Items;
import nekiplay.meteorplus.features.modules.fastladder.FastLadderPlus;
import nekiplay.meteorplus.features.modules.fly.FlyPlus;
import nekiplay.meteorplus.features.modules.jesus.JesusPlus;
import nekiplay.meteorplus.features.modules.nofall.NoFallPlus;
import nekiplay.meteorplus.features.modules.speed.SpeedPlus;
import nekiplay.meteorplus.features.modules.spider.SpiderPlus;
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
		Commands.add(new ClearInventory());
		Commands.add(new GotoPlus());
		Commands.add(new GPT());

		LOG.info(LOGPREFIX + " loaded commands");
		//endregion
		//region Modules
		LOG.info(LOGPREFIX + " initializing modules...");
		Modules modules = Modules.get();
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
		modules.add(new BoatAura());
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
		//modules.add(new VelocityPlus());
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
		return "nekiplay.meteorplus";
	}
}
