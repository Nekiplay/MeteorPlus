package olejka.meteorplus;

import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import net.fabricmc.loader.api.FabricLoader;
import olejka.meteorplus.commands.Eclip;
//import olejka.meteorplus.hud.CustomImageHud;
import olejka.meteorplus.gui.tabs.HiddenModulesTab;
import olejka.meteorplus.hud.MeteorPlusLogoHud;
//import olejka.meteorplus.hud.TargetHud;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import net.minecraft.item.Items;
import olejka.meteorplus.modules.*;
import olejka.meteorplus.modules.fastladder.FastLadderPlus;
import olejka.meteorplus.modules.fly.FlyPlus;
import olejka.meteorplus.modules.jesus.JesusPlus;
import olejka.meteorplus.modules.nofallplus.NoFallPlus;
import olejka.meteorplus.modules.speed.SpeedPlus;
import olejka.meteorplus.modules.spider.SpiderPlus;
import olejka.meteorplus.utils.algoritms.ShadyRotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeteorPlus extends MeteorAddon {
	public static final Logger LOG = LoggerFactory.getLogger(MeteorPlus.class);
	public static final Category CATEGORY = new Category("MeteorPlus", Items.EMERALD_BLOCK.getDefaultStack());
	public static final HudGroup HUD_GROUP = new HudGroup("MeteorPlusHud");


	//region Modules
	public SpiderPlus spiderPlus;
	public NoFallPlus noFallPlus;
	public AntiBotPlus antiBotPlus;
	//endregion

	private static MeteorPlus instance;

	public static MeteorPlus getInstance() {
		return instance;
	}

	@Override
	public void onInitialize() {
		instance = this;
		LOG.info("Meteor Plus initializing...");

		//region Commands
		LOG.info("Meteor Plus initializing commands...");

		Commands.add(new Eclip());

		LOG.info("Meteor Plus loaded commands");
		//endregion
		//region Modules
		LOG.info("Meteor Plus initializing modules...");
		Modules modules = Modules.get();

		spiderPlus = new SpiderPlus();
		noFallPlus = new NoFallPlus();
		antiBotPlus = new AntiBotPlus();

		modules.add(new FastLadderPlus());
		modules.add(new TriggerBot());
		modules.add(new EyeFinder());
		modules.add(new InventoryMovePlus());
		modules.add(new MiddleClickExtraPlus());
		modules.add(new AutoDropPlus());
		modules.add(noFallPlus);
		modules.add(new SpeedPlus());
		modules.add(new FlyPlus());
		modules.add(spiderPlus);
		modules.add(new JesusPlus());
		modules.add(new BoatAura());
		modules.add(new BedrockStorageBruteforce());
		modules.add(new AutoCraftPlus());
		modules.add(new AutoPortalMine());
		modules.add(new XrayBruteforce());
		modules.add(new AutoLeave());
		modules.add(new AutoAccept());
		modules.add(new GhostBlockFixer());
		modules.add(new SafeMine());
		modules.add(new Freeze());
		modules.add(new Noclip());
		modules.add(antiBotPlus);
		LOG.info("MeteorPlus loaded modules");
		//endregion
		//region Hud
		LOG.info("Meteor Plus initializing hud...");

		Hud.get().register(MeteorPlusLogoHud.INFO);

		LOG.info("Meteor Plus loaded hud");
		//endregion
		//region Tabs
		LOG.info("Meteor Plus initializing tabs...");

		Tabs.add(new HiddenModulesTab());

		LOG.info("Meteor Plus loaded hud");
		//endregion

		LOG.info("Meteor Plus loaded");
	}

	@Override
	public void onRegisterCategories() {
		LOG.info("MeteorPlus registering categories...");
		Modules.registerCategory(CATEGORY);
		LOG.info("MeteorPlus register categories");
	}

	@Override
	public String getWebsite() {
		return "https://github.com/Nekiplay/MeteorPlus";
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
