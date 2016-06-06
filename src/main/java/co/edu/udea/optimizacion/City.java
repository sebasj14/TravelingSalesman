package co.edu.udea.optimizacion;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class City {
	private double x;
	private double y;
	private double z;

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
		Vector3D fromCity = new Vector3D(getX(), getY(), getZ());
		Vector3D toCity = new Vector3D(city.getX(), city.getY(), city.getZ());

		return fromCity.distanceSq(toCity);
	}

	public Vector3D getPoint() {
		return new Vector3D(getX(), getY(), getZ());
	}

	public boolean tripIntersectsFrontier(City destinationCity) {
		Vector3D originPoint = getPoint();
		Vector3D destinationPoint = destinationCity.getPoint();
		Line trip = new Line(originPoint, destinationPoint);
		Line frontier = SimulatedAnnealing.getFrontier();

		if (frontier.contains(originPoint) && frontier.contains(destinationPoint)) {
			return false;
		}

		return trip.intersection(frontier) != null;
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
