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
		String name = name();
		if (name.equals("Matrix_6_7_0")) {
			return "Matrix 6.7.0";
		}
		if (name.equals("AAC_Hop_4_3_8")) {
			return "AAC Hop 4.3.8";
		}
		return super.toString().replace('_', ' ');
	}
}
