package nekiplay.meteorplus.features.modules.jesus;

public enum JesusModes {
	Matrix_Zoom,
	Matrix_Zoom_2,
	Vulcan;

	@Override
	public String toString() {
		return super.toString().replace('_', ' ');
	}
}
