package nekiplay.meteorplus.utils.manager;

import lombok.NonNull;
import lombok.SneakyThrows;
import meteordevelopment.meteorclient.systems.modules.Module;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
		if (!loaded) this.build();
		for (Class<? extends Module> module : this._module) {
			consumer.accept(module.getDeclaredConstructor().newInstance());
		}
	}

	private void build() {
		List<Class<?>> classes = scanner.getClasses(this.modulePackage);
		List<Class<? extends Module>> ms = classes.stream()
			.filter(Module.class::isAssignableFrom)
			.filter(clazz -> clazz.isAnnotationPresent(AutoRegistry.class))
			.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
			.map(clazz -> (Class<? extends Module>) clazz.asSubclass(Module.class))
			.collect(Collectors.toList());
		this._module.addAll(ms);
		loaded = true;
	}


	public ModuleManager(@NonNull String modulePackage) {
		this.modulePackage = modulePackage;
		_module = new ArrayList<>();
		scanner = new PackageScanner();
	}
}
