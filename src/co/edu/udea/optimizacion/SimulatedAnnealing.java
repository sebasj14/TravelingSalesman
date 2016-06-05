package co.edu.udea.optimizacion;

public class SimulatedAnnealing {
	// Calculate the acceptance probability
	public static double acceptanceProbability(double currentEnergy, double neighbourEnergy, double temperature) {
		// If the new solution is better, accept it
		if (neighbourEnergy < currentEnergy) {
			return 1.0;
		}
		// If the new solution is worse, calculate an acceptance probability
		return Math.exp((currentEnergy - neighbourEnergy) / temperature);
	}

	public static void main(String[] args) {
		// Create and add our cities

		// TODO: Load files with cities.
		City city = new City(5, 1, 2);
		TourManager.addCity(city);
		City city2 = new City(3, 0, 4);
		TourManager.addCity(city2);
		City city3 = new City(8, 1, 3);
		TourManager.addCity(city3);
		City city4 = new City(1, 1, 0);
		TourManager.addCity(city4);

		// Set initial temperature
		double temperature = 100;

		// Initialize initial solution
		Tour currentSolution = new Tour();
		currentSolution.generateIndividual();

		System.out.println("Initial solution distance: " + currentSolution.getDistance());

		// Set as current best
		Tour best = new Tour(currentSolution.getTour());

		// Loop until system has cooled
		while (temperature > 1) {
			// Create new neighbor tour
			Tour newSolution = new Tour(currentSolution.getTour());

			// Get a random positions in the tour
			int tourPos1 = (int) (newSolution.tourSize() * Math.random());
			int tourPos2 = (int) (newSolution.tourSize() * Math.random());

			// Get the cities at selected positions in the tour
			City citySwap1 = newSolution.getCity(tourPos1);
			City citySwap2 = newSolution.getCity(tourPos2);

			// Swap them
			newSolution.setCity(tourPos2, citySwap1);
			newSolution.setCity(tourPos1, citySwap2);

			// Get energy of solutions
			double currentEnergy = currentSolution.getDistance();
			double neighbourEnergy = newSolution.getDistance();

			// Decide if we should accept the neighbor
			if (acceptanceProbability(currentEnergy, neighbourEnergy, temperature) > Math.random()) {
				currentSolution = new Tour(newSolution.getTour());
			}

			// Keep track of the best solution found
			if (currentSolution.getDistance() < best.getDistance()) {
				best = new Tour(currentSolution.getTour());
			}

			// Cool system
			temperature = temperature / 2;
		}

		System.out.println("Final solution distance: " + best.getDistance());
		System.out.println("Tour: " + best);
	}
}
