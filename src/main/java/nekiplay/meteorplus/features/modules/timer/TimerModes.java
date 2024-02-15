package nekiplay.meteorplus.features.modules.timer;

public enum TimerModes {
	NCP,
	Intave,
	Custom,
	Old_Fag,
	rem6g6s
	;

	@Override
	public String toString() {
		String name = name();
		return name.replace('_', ' ').replaceAll("rem", "");
	}
}
