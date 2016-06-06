package co.edu.udea.optimizacion;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class City {
	private double x;
	private double y;
	private double z;

	public City(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public double distanceTo(City city) {
		Vector3D fromCity = new Vector3D(getX(), getY(), getZ());
		Vector3D destinationCity = new Vector3D(city.getX(), city.getY(), city.getZ());

		return fromCity.distanceSq(destinationCity);
	}

	public Vector3D getPoint() {
		return new Vector3D(getX(), getY(), getZ());
	}

	public boolean tripIntersectsFrontier(City destinationCity) {
		Vector3D originPoint = getPoint();
		Vector3D destinationPoint = destinationCity.getPoint();
		Line trip = new Line(originPoint, destinationPoint);

		Line frontier = SimulatedAnnealing.getFrontierLine();
		Plane plane = SimulatedAnnealing.getFrontierPlane();

		return (plane.intersection(trip) != null);

		// if (frontier.contains(originPoint) &&
		// frontier.contains(destinationPoint)) {
		// return false;
		// }
		//
		// return trip.intersection(frontier) != null;
	}

	@Override
	public String toString() {
		return getX() + ", " + getY() + ", " + getZ();
	}

	public boolean equals(City city) {
		return this.getX() == city.getX() && this.getY() == city.getY() && this.getZ() == city.getZ();
	}
}
