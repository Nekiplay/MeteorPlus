package nekiplay.meteorplus.features.commands.baritone;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Coordinate {
	int x, y, z;

	public Coordinate add(int x, int y, int z) {
		return new Coordinate(this.x + x, this.y, this.z + z);
	}
}
