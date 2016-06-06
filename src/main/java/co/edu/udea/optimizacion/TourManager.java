package co.edu.udea.optimizacion;

import java.util.ArrayList;

public class TourManager {

	private static ArrayList<City> cities = new ArrayList<>();

	public static void addCity(City city) {
		cities.add(city);
	}

	public static City getCity(int index) {
		return cities.get(index);
	}

	public static int getNumberOfCities() {
		return cities.size();
	}
}
