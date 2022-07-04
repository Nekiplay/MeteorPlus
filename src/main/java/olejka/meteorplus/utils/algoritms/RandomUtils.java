package olejka.meteorplus.utils.algoritms;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
	public static float nextFloat(float min, float max) {
		if (min == max) return max;
		return ThreadLocalRandom.current().nextFloat(min, max);
	}
	public static int nextInt(int min, int max) {
		if (min == max) return max;
		return ThreadLocalRandom.current().nextInt(min, max);
	}
	public static double nextDouble(double min, double max) {
		if (min == max) return max;
		return ThreadLocalRandom.current().nextDouble(min, max);
	}
}
