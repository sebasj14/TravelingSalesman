package co.edu.udea.optimizacion;

import java.util.ArrayList;

public class Tour {

	private ArrayList<City> tour = new ArrayList<>();
	private double distance = 0;

	public Tour() {
		for (int i = 0; i < TourManager.getNumberOfCities(); i++) {
			tour.add(null);
		}
	}

	@SuppressWarnings("unchecked")
	public Tour(ArrayList<City> tour) {
		this.tour = (ArrayList<City>) tour.clone();
	}

	public ArrayList<City> getTour() {
		return tour;
	}

	public void generateRandomIndividual() {
		for (int cityIndex = 0; cityIndex < TourManager.getNumberOfCities(); cityIndex++) {
			setCity(cityIndex, TourManager.getCity(cityIndex));
		}
	}

	public City getCity(int tourPosition) {
		return tour.get(tourPosition);
	}

	public void setCity(int tourPosition, City city) {
		tour.set(tourPosition, city);
		distance = 0;
	}

	public double getDistance() {
		if (distance == 0) {
			double tourDistance = 0;
			for (int cityIndex = 0; cityIndex < tourSize(); cityIndex++) {
				City fromCity = getCity(cityIndex);
				City destinationCity;

				// If we are our last city, set tour's final destination city to our starting city
				if (cityIndex + 1 < tourSize()) {
					destinationCity = getCity(cityIndex + 1);
				} else {
					destinationCity = getCity(0);
				}

				if (fromCity.tripIntersectsFrontier(destinationCity)) {
					tourDistance += fromCity.distanceTo(destinationCity) * 2;
				} else {
					tourDistance += fromCity.distanceTo(destinationCity);
				}
			}
			distance = tourDistance;
		}
		return distance;
	}

	public int tourSize() {
		return tour.size();
	}

	@Override
	public String toString() {
		String tourString = "|";
		for (int i = 0; i < tourSize(); i++) {
			tourString += "\n" + getCity(i) + "|";
		}
		return tourString;
	}
}
