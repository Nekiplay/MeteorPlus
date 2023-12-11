package nekiplay.meteorplus.utils.manager;

import lombok.NonNull;
import lombok.SneakyThrows;
import meteordevelopment.meteorclient.commands.Command;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandManager {
	private final List<Class<? extends Command>> _command;
	private final PackageScanner scanner;
	String commandPackage;
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
	public void registry(Consumer<Command> consumer) {
		if (!loaded) this.build();
		for (Class<? extends Command> cmd : _command) {
			consumer.accept(cmd.getDeclaredConstructor().newInstance());
		}
	}


	private void build() {
		PackageScanner scanner = new PackageScanner();
		List<Class<?>> classes = scanner.getClasses(this.commandPackage);
		List<Class<? extends Command>> cs = classes.stream()
			.filter(Command.class::isAssignableFrom)
			.filter(clazz -> clazz.isAnnotationPresent(AutoRegistry.class))
			.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
			.map(clazz -> (Class<? extends Command>) clazz.asSubclass(Command.class))
			.collect(Collectors.toList());
		this._command.addAll(cs);
		loaded = true;
	}

	public CommandManager(@NonNull String pkg) {
		this.commandPackage = pkg;
		_command = new ArrayList<>();
		scanner = new PackageScanner();
	}
}
