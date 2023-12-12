package nekiplay.meteorplus.features.commands.baritone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@AllArgsConstructor
@Data
public class CylinderRecord {
	@NonNull private Coordinate centerPos;
	private int radius;
	private int height;
}
