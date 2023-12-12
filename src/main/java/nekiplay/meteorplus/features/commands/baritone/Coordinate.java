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

	public static Coordinate of(int x, int y, int z) {
		return new Coordinate(x, y, z);
	}

	public Coordinate addX(int x) {
		this.x += x;
		return this;
	}

	public Coordinate addY(int y) {
		this.y += y;
		return this;
	}

	public Coordinate addZ(int z) {
		this.z += z;
		return this;
	}
}
