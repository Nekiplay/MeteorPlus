package olejka.meteorplus;

import meteordevelopment.meteorclient.systems.Systems;
import olejka.meteorplus.modules.*;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.hud.HUD;
import net.minecraft.item.Items;
import olejka.meteorplus.modules.AutoSell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.invoke.MethodHandles;

public class MeteorPlus extends MeteorAddon {
	public static final Logger LOG = LoggerFactory.getLogger(MeteorPlus.class);
	public static final Category CATEGORY = new Category("Meteor Plus", Items.EMERALD_BLOCK.getDefaultStack());

	@Override
	public void onInitialize() {
		LOG.info("Initializing MeteorPlus");

		// Required when using @EventHandler
		MeteorClient.EVENT_BUS.registerLambdaFactory("olejka.meteorplus", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));


		//Modules
		Modules.get().add(new SpeedPlus());
		Modules.get().add(new FlyPlus());
		Modules.get().add(new SpiderPlus());
		Modules.get().add(new BoatAura());
		Modules.get().add(new BedrockStorageBruteforce());
		Modules.get().add(new JesusPlus());
		Modules.get().add(new AutoSell());
		Modules.get().add(new AutoCraftPlus());
		Modules.get().add(new AutoPortalMine());
		Modules.get().add(new XrayBruteforce());
		Modules.get().add(new AutoLeave());
		Modules.get().add(new AutoAccept());
		Modules.get().add(new AutoRepair());
		Modules.get().add(new GhostBlockFixer());
		Modules.get().add(new SafeMine());
		Modules.get().add(new Freeze());

		// Hud
		HUD hud = Systems.get(HUD.class);
		hud.elements.add(new MeteorPlusLogoHud(hud));
	}

	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}
}
