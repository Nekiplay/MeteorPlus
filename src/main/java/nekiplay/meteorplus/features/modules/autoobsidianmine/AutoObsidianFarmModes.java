package nekiplay.meteorplus.features.modules.autoobsidianmine;

public enum AutoObsidianFarmModes {
	Portals_Vanila,
	Portal_Homes,;
	//Cauldrons;

	@Override
	public String toString() {
		String name = name();
		return name.replace('_', ' ');
	}
}
