package nekiplay.meteorplus.features.modules.speed;

public enum SpeedModes {
	NCP_Hop,
	Matrix_Exploit,
	Matrix_Exploit_2,
	Matrix_6_7_0,
	Matrix,
	Vulcan,
	AAC_Hop_4_3_8;

	@Override
	public String toString() {
		return super.toString().replace('_', ' ');
	}
}
