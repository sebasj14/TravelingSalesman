package co.edu.udea.optimizacion;

import java.util.ArrayList;

public class TourManager {

	// Holds our cities
	private static ArrayList<City> cities = new ArrayList<>();

	// Adds a destination city
	public static void addCity(City city) {
		cities.add(city);
	}

	// Get a city
	public static City getCity(int index) {
		return cities.get(index);
	}

	// Get the number of destination cities
	public static int getNumberOfCities() {
		return cities.size();
	}
}
