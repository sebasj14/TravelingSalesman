package co.edu.udea.optimizacion;

public class City {
	int x;
	int y;
	int z;

	// Constructs a randomly placed city
	public City() {
		this.x = (int) (Math.random() * 200);
		this.y = (int) (Math.random() * 200);
		this.z = (int) (Math.random() * 200);
	}

	// Constructs a city at chosen x, y, z location
	public City(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Gets city's x coordinate
	public int getX() {
		return this.x;
	}

	// Gets city's y coordinate
	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	// Gets the distance to given city
	public double distanceTo(City city) {
		double xDistance = getX() - city.getX();
		double yDistance = getY() - city.getY();
		double zDistance = getZ() - city.getZ();
		double distance = (xDistance * xDistance) + (yDistance * yDistance) + (zDistance * zDistance);

		return distance;
	}

	public int sideOfBoundary() {
		// TODO: Find plane equation from 3 given points.
		// TODO: Recta
		double plane = this.getX() - 2 * this.getY() + 3 * this.getZ() - 1;
		if (Double.compare(plane, 0) < 0) {
			return -1;
		} else if (plane > 0) {
			return 1;
		}

		return 0;
	}

	@Override
	public String toString() {
		return getX() + ", " + getY() + ", " + getZ();
	}
}
