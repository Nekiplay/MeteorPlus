package nekiplay.meteorplus.features.commands.baritone;

import java.util.ArrayList;
import java.util.List;

public class CircleCalculation {

	public static List<Coordinate> makeCylinder(Coordinate center, int radius) {
		final List<Coordinate> object = new ArrayList<>();
		for (int x = 0; x <= (int) Math.ceil(radius + 0.5); ++x) {
			object.addAll(calcCylinder(center, x, radius));
		}
		return object;
	}

	private static List<Coordinate> calcCylinder(Coordinate center, int x, int radius) {
		ArrayList<Coordinate> object = new ArrayList<>();
		for (int z = 0; z < (int) Math.ceil(radius + 0.5); ++z) {
			double xn = x * (1 / (radius + 0.5));
			double zn = z * (1 / (radius + 0.5));
			if (isInsiderCylinder(xn, zn)) {
				addCylinderCoordinates(object, center, x, z);
			}
		}
		return object;
	}

	private static boolean isInsiderCylinder(double xn, double zn) {
		return Math.pow(xn, 2) + Math.pow(zn, 2) <= 1;
	}

	private static void addCylinderCoordinates(List<Coordinate> object, Coordinate center, int x, int z) {
		addToList(object, center.add(x, center.getY(), z),
			center.add(-x, center.getY(), z),
			center.add(x, center.getY(), -z),
			center.add(-x, center.getY(), -z));
	}


	private static void addToList(List<Coordinate> posList, Coordinate... pos) {
		for (Coordinate p : pos) {
			if (!posList.contains(p)) posList.add(p);
		}
	}

	public static List<PositionBean> calcNotHollowPosition(List<Coordinate> pos, CylinderRecord info) {
		List<PositionBean> objects = new ArrayList<>();
		for (Coordinate coordinate : calcPosition(pos, info)) {
			addPosition(objects, mirror(coordinate, info));
		}
		objects.forEach(o -> {
			o.getSecond().addY(info.getHeight() - 1);
		});
		addPosition(objects,
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y, info.getCenterPos().z - info.getRadius()),
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y + info.getHeight() - 1, info.getCenterPos().z + info.getRadius())
		);
		return objects;
	}

	private static Coordinate[] mirror(Coordinate pos, CylinderRecord info) {
		return new Coordinate[]{
			pos,
			new Coordinate(pos.x, pos.y, info.getCenterPos().getZ() - (pos.z - info.getCenterPos().getZ())),
			new Coordinate(info.getCenterPos().getX() - (pos.x - info.getCenterPos().getX()), pos.y, pos.z),
			new Coordinate(info.getCenterPos().getX() - (pos.x - info.getCenterPos().getX()), pos.y, info.getCenterPos().getZ() - (pos.z - info.getCenterPos().getZ()))
		};
	}

	public static List<PositionBean> calcWithHollowPosition(List<Coordinate> pos, CylinderRecord info) {
		List<PositionBean> objects = new ArrayList<>();
		for (Coordinate coordinate : calcPosition(pos, info)) {
			addToHollowList(mirror(coordinate, info), objects, info);
		}

		addPosition(objects,
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y, info.getCenterPos().z - info.getRadius()),
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y + info.getHeight() - 1, info.getCenterPos().z - info.getRadius()),
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y, info.getCenterPos().z + info.getRadius()),
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y + info.getHeight() - 1, info.getCenterPos().z + info.getRadius())
		);
		return objects;
	}

	private static void addPosition(List<PositionBean> o, Coordinate... pos) {
		if (pos.length % 2 != 0) return;
		for (int i = 0; i < pos.length; i += 2) {
			o.add(new PositionBean(pos[i], pos[i + 1]));
		}
	}

	private static void addToHollowList(Coordinate[] pos, List<PositionBean> container, CylinderRecord info) {
		if (pos[0].getX() - info.getCenterPos().getX() == info.getRadius()) {
			container.add(
				new PositionBean(pos[0], pos[1].addY(info.getHeight() - 1))
			);
			container.add(
				new PositionBean(pos[2], pos[3].addY(info.getHeight() - 1))
			);
			return;
		}
		for (Coordinate p : pos) {
			container.add(
				new PositionBean(p, Coordinate.of(p.x, p.y + info.getHeight() - 1, p.z))
			);
		}
	}

	private static List<Coordinate> calcPosition(List<Coordinate> pos, CylinderRecord info) {
		int radius = info.getRadius();
		List<Coordinate> temp = new ArrayList<>();
		for (int i = radius; i > 0; i--) {
			temp.addAll(makePosition(pos, info, i));
		}
		return temp;
	}

	private static List<Coordinate> makePosition(List<Coordinate> pos, CylinderRecord info, int i) {
		Coordinate center = info.getCenterPos();
		int radius = info.getRadius();
		List<Coordinate> object = new ArrayList<>();
		for (int j = radius; j > 0; j--) {
			Coordinate p = new Coordinate(center.getX() + i, center.getY(), center.getZ() + j);
			if (!pos.contains(p)) continue;
			object.add(p);
			break;
		}
		return object;
	}
}
