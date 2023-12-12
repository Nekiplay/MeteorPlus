package nekiplay.meteorplus.features.commands.baritone;

import java.util.ArrayList;
import java.util.List;

public class CircleCalculation {

	public static List<Coordinate> makeCylinder(Coordinate center, int radius) {
		final List<Coordinate> object = new ArrayList<>();
		final int crx = (int) Math.ceil(radius + 0.5);
		final int crz = (int) Math.ceil(radius + 0.5);
		for (int x = 0; x <= crx; ++x) {
			for (int z = 0; z <= crz; ++z) {
				double xn = x * (1 / (radius + 0.5));
				double zn = z * (1 / (radius + 0.5));
				if ((xn * xn + zn * zn) > 1) continue;
				addToList(
					object,
					center.add(x, center.getY(), z),
					center.add(-x, center.getY(), z),
					center.add(x, center.getY(), -z),
					center.add(-x, center.getY(), -z)
				);
			}
		}
		return object;
	}

	private static void addToList(List<Coordinate> posList, Coordinate... pos) {
		for (Coordinate p : pos) {
			if (!posList.contains(p)) posList.add(p);
		}
	}

	public static List<PositionBean> calcNotHollowPosition(List<Coordinate> pos, CylinderRecord info) {
		List<PositionBean> objects = new ArrayList<>();
		for (Coordinate coordinate : calcPosition(pos, info)) {
			addToNotHollowList(mirror(coordinate, info), objects);
		}

		objects.forEach(o -> {
			o.getSecond().addY(info.getHeight() - 1);
		});

		objects.add(new PositionBean(
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y, info.getCenterPos().z - info.getRadius()),
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y + info.getHeight() - 1, info.getCenterPos().z + info.getRadius())
		));

		return objects;
	}

	private static void addToNotHollowList(Coordinate[] pos, List<PositionBean> container) {
		for (int i = 0; i < pos.length; i += 2) {
			container.add(
				new PositionBean(
					pos[i],
					pos[i + 1]
				)
			);
		}
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

		objects.add(new PositionBean(
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y, info.getCenterPos().z - info.getRadius()),
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y + info.getHeight()-1, info.getCenterPos().z - info.getRadius())
		));
		objects.add(new PositionBean(
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y, info.getCenterPos().z + info.getRadius()),
			new Coordinate(info.getCenterPos().x, info.getCenterPos().y + info.getHeight() - 1, info.getCenterPos().z + info.getRadius())
		));
		return objects;
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
		List<PositionBean> objects = new ArrayList<>();
		int radius = info.getRadius();
		int height = info.getHeight();
		Coordinate center = info.getCenterPos();

		List<Coordinate> temp = new ArrayList<>();
		for (int i = radius; i > 0; i--) {
			for (int j = radius; j > 0; j--) {
				Coordinate p = new Coordinate(center.getX() + i, center.getY(), center.getZ() + j);
				if (!pos.contains(p)) continue;
				temp.add(p);
				break;
			}
		}
		return temp;
	}
}
