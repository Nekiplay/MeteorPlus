package olejka.meteorplus;

import meteordevelopment.meteorclient.systems.modules.Modules;
import olejka.meteorplus.modules.*;
import olejka.meteorplus.modules.fastladder.FastLadderPlus;
import olejka.meteorplus.modules.fly.FlyPlus;
import olejka.meteorplus.modules.jesus.JesusPlus;
import olejka.meteorplus.modules.nofallplus.NoFallPlus;
import olejka.meteorplus.modules.speed.SpeedPlus;
import olejka.meteorplus.modules.spider.SpiderPlus;

public class MeteorPlusModules {
	public SpiderPlus spiderPlus;
	public NoFallPlus noFallPlus;

	public void Register() {
		Modules modules = Modules.get();

		spiderPlus = new SpiderPlus();
		noFallPlus = new NoFallPlus();

		modules.add(new FastLadderPlus());
		modules.add(new ServerSpoofPlus());
		modules.add(new TriggerBot());
		modules.add(new EyeFinder());
		modules.add(new InventoryMovePlus());
		modules.add(new MiddleClickExtraPlus());
		modules.add(new KillAuraBetter());
		modules.add(new AutoDropPlus());
		modules.add(noFallPlus);
		modules.add(new SpeedPlus());
		modules.add(new FlyPlus());
		modules.add(spiderPlus);
		modules.add(new JesusPlus());
		modules.add(new BoatAura());
		modules.add(new BedrockStorageBruteforce());
		modules.add(new AutoSell());
		modules.add(new AutoCraftPlus());
		modules.add(new AutoPortalMine());
		modules.add(new XrayBruteforce());
		modules.add(new AutoLeave());
		modules.add(new AutoAccept());
		modules.add(new AutoRepair());
		modules.add(new GhostBlockFixer());
		modules.add(new SafeMine());
		modules.add(new Freeze());
		modules.add(new Noclip());
		modules.add(new AntiBotPlus());
	}
}
