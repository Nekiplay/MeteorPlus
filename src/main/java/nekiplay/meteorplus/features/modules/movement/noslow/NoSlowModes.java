package nekiplay.meteorplus.features.modules.movement.noslow;

public enum NoSlowModes {
	Vanila,
	NCP_Strict,
	Grim_1dot8,
	Grim_New;

	@Override
	public String toString() {
		return super.toString().replace('_', ' ').replaceAll("dot", ".");
	}
}
