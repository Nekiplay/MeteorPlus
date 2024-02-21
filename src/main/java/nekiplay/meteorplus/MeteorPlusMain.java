package nekiplay.meteorplus;

import nekiplay.meteorplus.items.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nekiplay.meteorplus.MeteorPlusAddon.LOGPREFIX;

public class MeteorPlusMain implements ModInitializer {
	public static final Logger LOG = LoggerFactory.getLogger(MeteorPlusMain.class);
	@Override
	public void onInitialize() {
		LOG.info(LOGPREFIX + " initializing items...");
		ModItems.Initialize();
		LOG.info(LOGPREFIX + " loaded items");
	}
}
