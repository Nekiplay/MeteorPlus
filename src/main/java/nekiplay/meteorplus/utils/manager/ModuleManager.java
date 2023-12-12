package nekiplay.meteorplus.utils.manager;

import lombok.NonNull;
import lombok.SneakyThrows;
import meteordevelopment.meteorclient.systems.modules.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModuleManager {
	private final List<Class<? extends Module>> _module;
	private final PackageScanner scanner;
	String modulePackage;
	boolean loaded;

	public void addToIgnoreClass(String... className) {
		for (String s : className) {
			scanner.addToIgnoreClass(s);
		}
	}

	public void addToIgnorePackage(String... packageName) {
		for (String s : packageName) {
			scanner.addToIgnorePackage(s);
		}
	}

	@SneakyThrows
	public void registry(Consumer<Module> consumer) {
		if (!loaded) this.buildModule();
		for (Class<? extends Module> module : this._module) {
			consumer.accept(module.getDeclaredConstructor().newInstance());
		}
	}

	private void buildModule() {
		this.loaded = MeteorManager.build(scanner, this._module, modulePackage, Module.class);
	}


	public ModuleManager(@NonNull String modulePackage) {
		this.modulePackage = modulePackage;
		_module = new ArrayList<>();
		scanner = new PackageScanner();
	}
}
