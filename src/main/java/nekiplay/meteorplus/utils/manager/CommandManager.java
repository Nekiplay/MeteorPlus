package nekiplay.meteorplus.utils.manager;

import lombok.NonNull;
import lombok.SneakyThrows;
import meteordevelopment.meteorclient.commands.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
		if (!loaded) this.buildCommand();
		for (Class<? extends Command> cmd : _command) {
			consumer.accept(cmd.getDeclaredConstructor().newInstance());
		}
	}

	private void buildCommand(){
		this.loaded = MeteorManager.build(scanner, _command, commandPackage, Command.class);
	}

	public CommandManager(@NonNull String pkg) {
		this.commandPackage = pkg;
		_command = new ArrayList<>();
		scanner = new PackageScanner();
	}
}
