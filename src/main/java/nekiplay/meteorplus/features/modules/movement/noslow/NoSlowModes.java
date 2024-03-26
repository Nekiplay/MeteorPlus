package nekiplay.meteorplus.features.modules.movement.noslow;

public enum NoSlowModes {
	Vanila,
	Grim,
	Grim_New;

	@Override
	public String toString() {
		return super.toString().replace('_', ' ').replaceAll("dot", ".");
	}
}
