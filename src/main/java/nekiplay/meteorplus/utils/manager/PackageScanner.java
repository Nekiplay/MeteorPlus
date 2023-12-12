package nekiplay.meteorplus.utils.manager;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;


/**
 * This is a simple package scanner used to scan all classes under a
 * specified package
 */
@NoArgsConstructor
public class PackageScanner {

	private final List<String> ignoreClass = new ArrayList<>();
	private final List<String> ignorePackage = new ArrayList<>();

	public void addToIgnoreClass(String className) {
		ignoreClass.add(className);
	}

	public void addToIgnorePackage(String packageName) {
		ignorePackage.add(packageName);
	}

	private boolean isIgnored(String className) {
		if (ignoreClass.contains(className)) {
			return true;
		}
		for (String ignoredPackage : ignorePackage) {
			if (className.startsWith(ignoredPackage)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Scans all classes under the specified package.
	 *
	 * @param pkg The name of the package to be scanned.
	 * @return A list containing the names of the scanned classes.
	 */
	@SneakyThrows
	public List<Class<?>> getClasses(@NonNull String pkg) {
		List<Class<?>> classes = new ArrayList<>();
		String path = pkg.replace('.', '/');
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> resources = loader.getResources(path);

		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			if (url.getProtocol().equals("file")) {
				File file = new File(url.getFile());
				classes.addAll(findClasses(file, pkg));
			} else if (url.getProtocol().equals("jar")) {
				JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
				JarFile jarFile = jarURLConnection.getJarFile();
				classes.addAll(findClassesInJar(jarFile, path));
			}
		}
		return classes;
	}

	@SneakyThrows
	private Collection<Class<?>> findClassesInJar(JarFile jarFile, String path) {
		List<Class<?>> classes = new ArrayList<>();
		while (jarFile.entries().hasMoreElements()) {
			String name = jarFile.entries().nextElement().getName();
			if (!isClassFileInPath(name, path) || !isIgnored(extractClassName(name)))
				continue;
			classes.add(Class.forName(extractClassName(name)));
		}
		return classes;
	}

	private boolean isClassFileInPath(String name, String path) {
		return name.startsWith(path) && name.endsWith(".class");
	}

	private String extractClassName(String name) {
		return name.substring(0, name.length() - 6).replace('/', '.');
	}

	@SneakyThrows
	private Collection<Class<?>> findClasses(File file, String pkg) {
		List<Class<?>> classes = new ArrayList<>();
		if (!file.exists()) return classes;

		for (File iFile : Objects.requireNonNull(file.listFiles())) {
			if (iFile.isDirectory()) {
				assert !iFile.getName().contains(".");
				classes.addAll(findClasses(iFile, String.format("%s.%s", pkg, iFile.getName())));
			} else {
				addClassFrom(pkg, iFile, classes);
			}
		}
		return classes;
	}

	@SneakyThrows
	private void addClassFrom(String pkg, File iFile, List<Class<?>> classes) {
		if (iFile.getName().endsWith(".class")) {
			String className = String
				.format("%s.%s", pkg, extractClassName(iFile.getName()));
			if (!isIgnored(className))
				classes.add(Class.forName(className));
		}
	}

}
