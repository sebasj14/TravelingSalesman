package co.edu.udea.optimizacion;

import java.util.ArrayList;
import java.util.Collections;

public class Tour {

	// Holds our tour of cities
	private ArrayList<City> tour = new ArrayList<>();
	// Cache
	private double distance = 0;

	// Constructs a blank tour
	public Tour() {
		for (int i = 0; i < TourManager.getNumberOfCities(); i++) {
			tour.add(null);
		}
	}

	// Constructs a tour from another tour
	@SuppressWarnings("unchecked")
	public Tour(ArrayList<City> tour) {
		this.tour = (ArrayList<City>) tour.clone();
	}

	// Returns tour information
	public ArrayList<City> getTour() {
		return tour;
	}

	// Creates a random individual
	public void generateIndividual() {
		// Loop through all our destination cities and add them to our tour
		for (int cityIndex = 0; cityIndex < TourManager.getNumberOfCities(); cityIndex++) {
			setCity(cityIndex, TourManager.getCity(cityIndex));
		}
		// Randomly reorder the tour
		Collections.shuffle(tour);
	}

	// Gets a city from the tour
	public City getCity(int tourPosition) {
		return tour.get(tourPosition);
	}

	// Sets a city in a certain position within a tour
	public void setCity(int tourPosition, City city) {
		tour.set(tourPosition, city);
		// If the tours been altered we need to reset the fitness and distance
		distance = 0;
	}

	// Gets the total distance of the tour
	public double getDistance() {
		if (distance == 0) {
			double tourDistance = 0;
			// Loop through our tour's cities
			for (int cityIndex = 0; cityIndex < tourSize(); cityIndex++) {
				// Get city we're traveling from
				City fromCity = getCity(cityIndex);
				// City we're traveling to
				City destinationCity;
				// Check we're not on our tour's last city, if we are set our
				// tour's final destination city to our starting city
				if (cityIndex + 1 < tourSize()) {
					destinationCity = getCity(cityIndex + 1);
				} else {
					destinationCity = getCity(0);
				}
				// Check if the cities are on the same side of the boundary
				if (fromCity.sideOfBoundary() == destinationCity.sideOfBoundary()) {
					// Get the distance between the two cities
					tourDistance += fromCity.distanceTo(destinationCity);
				} else {
					// Get the distance between the two cities
					tourDistance += fromCity.distanceTo(destinationCity) * 2;
				}
			}
			distance = tourDistance;
		}
		return distance;
	}

	// Get number of cities on our tour
	public int tourSize() {
		return tour.size();
	}

	@Override
	public String toString() {
		String geneString = "|";
		for (int i = 0; i < tourSize(); i++) {
			geneString += "\n" + getCity(i) + "|";
		}
		return geneString;
	}
}