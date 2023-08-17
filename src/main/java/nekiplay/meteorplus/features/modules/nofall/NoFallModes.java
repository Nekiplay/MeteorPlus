package nekiplay.meteorplus.features.modules.nofall;

public enum NoFallModes {
	Matrix_New,
	Vulcan,
	Elytra_Clip;
	@Override
	public String toString() {
		return super.toString().replace('_', ' ');
	}
}
