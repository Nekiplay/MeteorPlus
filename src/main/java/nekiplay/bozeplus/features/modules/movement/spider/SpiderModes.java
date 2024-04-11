package nekiplay.bozeplus.features.modules.movement.spider;

public enum SpiderModes {
	Matrix_Lower_7,
	Vulcan,
	Elytra_clip;

	@Override
	public String toString() {
		return super.toString().replace('_', ' ').replaceAll("_Lower_", "<");
	}
}
