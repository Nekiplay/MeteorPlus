package nekiplay.meteorplus.utils.manager;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

public interface MeteorManager {

	static <T> boolean build(PackageScanner scanner, List<Class<? extends T>> container, String pkg, Class<T> type) {
		List<Class<?>> classes = scanner.getClasses(pkg);
		List<Class<? extends T>> ms = classes.stream()
			.filter(type::isAssignableFrom)
			.filter(clazz -> clazz.isAnnotationPresent(AutoRegistry.class))
			.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
			.map(clazz -> (Class<? extends T>) clazz.asSubclass(type))
			.collect(Collectors.toList());
		container.addAll(ms);
		return true;
	}
}
