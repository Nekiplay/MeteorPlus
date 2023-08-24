package nekiplay.meteorplus.features.modules.autoobsidianmine;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;

public class AutoObsidianFarmMode {
	protected final MinecraftClient mc;
	protected final AutoObsidianFarm settings;
	private final AutoObsidianFarmModes type;

	public AutoObsidianFarmMode(AutoObsidianFarmModes type) {
		this.settings = Modules.get().get(AutoObsidianFarm.class);;
		this.mc = MinecraftClient.getInstance();
		this.type = type;
	}

	public void onActivate() {}
	public void onDeactivate() {}
	public void onTickEventPre(TickEvent.Pre event) {}
}
