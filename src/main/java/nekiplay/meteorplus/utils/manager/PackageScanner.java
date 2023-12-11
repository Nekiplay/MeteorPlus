package nekiplay.meteorplus.utils.manager;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
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
		Enumeration<JarEntry> entries = jarFile.entries();

		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			String name = jarEntry.getName();

			if (name.startsWith(path) && name.endsWith(".class")) {
				String className = name.substring(0, name.length() - 6).replace('/', '.');
				if (!isIgnored(className)) classes.add(Class.forName(className));
			}
		}
		return classes;
	}

	@SneakyThrows
	private Collection<Class<?>> findClasses(File file, String pkg) {
		List<Class<?>> classes = new ArrayList<>();
		if (!file.exists()) return classes;

		File[] files = file.listFiles();
		if (null != files) {
			for (File iFile : files) {
				if (iFile.isDirectory()) {
					assert !iFile.getName().contains(".");
					classes.addAll(findClasses(iFile, pkg + "." + iFile.getName()));
				} else if (iFile.getName().endsWith(".class")) {
					String className = pkg + "." + iFile.getName().substring(0, iFile.getName().length() - 6);
					if (!isIgnored(className)) classes.add(Class.forName(className));
				}
			}
		}
		return classes;
	}

}
