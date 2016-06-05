package co.edu.udea.optimizacion;

public class City {
	double x;
	double y;
	double z;

	// Constructs a city at chosen x, y, z location
	public City(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Gets city's x coordinate
	public double getX() {
		return this.x;
	}

	// Gets city's y coordinate
	public double getY() {
		return this.y;
	}

	public double getZ() {
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
		// TODO: Straight line
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

	public boolean equals(City city) {
		return this.getX() == city.getX() && this.getY() == city.getY() && this.getZ() == city.getZ();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
