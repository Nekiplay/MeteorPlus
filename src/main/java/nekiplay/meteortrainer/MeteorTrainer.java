package nekiplay.meteortrainer;

import meteordevelopment.meteorclient.addons.GithubRepo;
import net.fabricmc.loader.api.FabricLoader;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeteorTrainer extends MeteorAddon {
	public static final Logger LOG = LoggerFactory.getLogger(MeteorTrainer.class);

	private static MeteorTrainer instance;

	public static MeteorTrainer getInstance() {
		return instance;
	}


	@Override
	public void onInitialize() {
		instance = this;
	}

	@Override
	public void onRegisterCategories() {

	}

	@Override
	public String getWebsite() {
		return "https://meteor-plus.com/";
	}

	@Override
	public GithubRepo getRepo() {
		return new GithubRepo("Nekiplay", "MeteorTrainer", "master");
	}

	@Override
	public String getCommit() {
		String commit = FabricLoader
			.getInstance()
			.getModContainer("meteortrainer")
			.get().getMetadata()
			.getCustomValue("github:sha")
			.getAsString();
		return commit.isEmpty() ? null : commit.trim();
	}

	@Override
	public String getPackage() {
		return "nekiplay.meteortrainer";
	}
}
