package nekiplay.meteorplus.features.modules.world.timer;

public enum TimerModes {
	NCP,
	Intave,
	Vulcan,
	Grim,
	OldFag,
	rem6g6s,
	Custom
	;

	@Override
	public String toString() {
		String name = name();
		return name.replace('_', ' ').replaceAll("rem", "");
	}
}
