package nekiplay.meteorplus.features.modules.combat.killaura.modes;

import nekiplay.meteorplus.features.modules.combat.killaura.KillAuraPlusMode;
import nekiplay.meteorplus.features.modules.combat.killaura.KillAuraPlusModes;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public class VanilaPlus extends KillAuraPlusMode {
	public VanilaPlus() {
		super(KillAuraPlusModes.VanilaPlus);
	}

	private final ArrayList<Entity> targets = new ArrayList<>();
	private int switchTimer, hitTimer;
	private boolean wasPathing = false;


}
