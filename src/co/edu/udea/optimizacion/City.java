package co.edu.udea.optimizacion;

public class City {
	int x;
	int y;
	int z;

	// Constructs a randomly placed city
	public City() {
		this.x = (int) (Math.random() * 200);
		this.y = (int) (Math.random() * 200);
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
		double xDistance = Math.abs(getX() - city.getX());
		double yDistance = Math.abs(getY() - city.getY());
		double zDistance = Math.abs(getZ() - city.getZ());
		double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance) + (zDistance * zDistance));

		return distance;
	}

	@Override
	public String toString() {
		return getX() + ", " + getY();
	}
}
