package nekiplay.meteorplus.features.modules.fly;

public enum FlyModes {
	Vulcan_Clip,
	Matrix_Exploit_2,
	Matrix_Exploit,
	Damage,
	Damage_OldFag
	;

	@Override
	public String toString() {
		String name = name();
		return name.replace('_', ' ');
	}
}
