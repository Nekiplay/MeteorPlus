package nekiplay.meteorplus.features.modules.spider;

public enum SpiderModes {
	Matrix,
	Vulcan,
	Elytra_clip;

	@Override
	public String toString() {
		return super.toString().replace('_', ' ');
	}
}
