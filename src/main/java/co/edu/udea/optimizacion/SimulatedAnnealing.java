package co.edu.udea.optimizacion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.CroppableLineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.providers.SmartTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.IntegerTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class SimulatedAnnealing extends AbstractAnalysis {

	private static List<City> solution = new ArrayList<>();
	private static Line frontier;
	private static double frontierP1X;
	private static double frontierP1Y;
	private static double frontierP1Z;
	private static double frontierP2X;
	private static double frontierP2Y;
	private static double frontierP2Z;

	public List<CroppableLineStrip> lineStrips = new ArrayList<CroppableLineStrip>();

	public List<CroppableLineStrip> parseFile(String filename) {
		// Create local line strip and set color
		CroppableLineStrip lineStrip = new CroppableLineStrip();
		lineStrip.setWireframeColor(Color.BLUE);

		// Loop through rows while a next row exists
		for (City city : solution) {
			// Add the map point to the line strip
			lineStrip.add(new Point(new Coord3d(city.getX(), city.getY(), city.getZ())));
		}

		// Create frontier and add it to lineStrips.
		CroppableLineStrip frontierLine = new CroppableLineStrip();
		frontierLine.setWireframeColor(Color.RED);
		frontierLine.setWidth(2);
		Point point1 = new Point(new Coord3d(frontierP1X, frontierP1Y, frontierP1Z));
		Point point2 = new Point(new Coord3d(frontierP2X, frontierP2Y, frontierP2Z));
		frontierLine.add(point1);
		frontierLine.add(point2);

		// Add the final lineStrip after while loop is complete.
		lineStrips.add(lineStrip);
		lineStrips.add(frontierLine);

		return lineStrips;
	}

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

		// Load cities
		loadFile(args[0]);

		// Define frontier
		frontierP1X = Double.parseDouble(args[1]);
		frontierP1Y = Double.parseDouble(args[2]);
		frontierP1Z = Double.parseDouble(args[3]);
		Vector3D p1 = new Vector3D(frontierP1X, frontierP1Y, frontierP1Z);

		frontierP2X = Double.parseDouble(args[4]);
		frontierP2Y = Double.parseDouble(args[5]);
		frontierP2Z = Double.parseDouble(args[6]);
		Vector3D p2 = new Vector3D(frontierP2X, frontierP2Y, frontierP2Z);

		frontier = new Line(p1, p2);

		// Set initial temperature
		double temperature = 100;

		// Initialize initial solution
		Tour currentSolution = new Tour();
		currentSolution.generateIndividual();

		double initialX = Double.parseDouble(args[7]);
		double initialY = Double.parseDouble(args[8]);
		double initialZ = Double.parseDouble(args[9]);

		City initialCity = new City(initialX, initialY, initialZ);
		// Get a random positions in the tour
		int initialCityIndex = currentSolution.getTour().indexOf(initialCity);

		for (int cityIndex = 0; cityIndex < currentSolution.getTour().size(); cityIndex++) {
			if (currentSolution.getCity(cityIndex).equals(initialCity)) {
				initialCityIndex = cityIndex;
			}
		}

		// Get the cities at selected positions in the tour
		City citySwap1a = currentSolution.getCity(initialCityIndex);
		City citySwap2a = currentSolution.getCity(0);

		// Swap them
		currentSolution.setCity(0, citySwap1a);
		currentSolution.setCity(initialCityIndex, citySwap2a);

		System.out.println("Initial solution distance: " + currentSolution.getDistance());

		// Set as current best
		Tour best = new Tour(currentSolution.getTour());

		// Loop until system has cooled
		while (temperature > 1) {
			// Create new neighbor tour
			Tour newSolution = new Tour(currentSolution.getTour());

			// Get a random positions in the tour
			int tourPos1 = ThreadLocalRandom.current().nextInt(1, TourManager.getNumberOfCities());
			int tourPos2 = ThreadLocalRandom.current().nextInt(1, TourManager.getNumberOfCities());

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
			temperature -= 0.5;
		}

		solution = best.getTour();

		try {
			AnalysisLauncher.open(new SimulatedAnnealing());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No grafica." + e.toString());
		}

		System.out.println("Final solution distance: " + best.getDistance());
		System.out.println("Tour: " + best);
	}

	public void init() throws Exception {
		// Define a function to plot
		Mapper mapper = new Mapper() {
			@Override
			public double f(double x, double y) {
				return -3 * x + 6 * y + 3;
			}
		};

		double x;
		double y;
		double z;
		Coord3d[] points = new Coord3d[solution.size()];

		for (int i = 0; i < solution.size(); i++) {
			x = solution.get(i).getX();
			y = solution.get(i).getY();
			z = solution.get(i).getZ();
			points[i] = new Coord3d(x, y, z);
		}

		// TODO: Close trip to make last city = initial city.
		// TODO: Give initial city a marker and different color.
		Scatter scatter = new Scatter(points, Color.BLACK);
		scatter.setWidth(5);
		chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
		chart.getScene().add(scatter);

		// Define range and precision for the function to plot
		Range range = new Range(-3, 3);
		int steps = 80;

		// Create the object to represent the function over the given range.
		final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
		surface.setColorMapper(
				new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);

		// Create a chart //
		// chart = AWTChartComponentFactory.chart(Quality.Advanced,
		// getCanvasType());
		chart.getScene().getGraph().add(surface);

		// Parse the file
		this.parseFile("./Datos1.csv");

		// Add line stripe to chart
		chart.getScene().getGraph().add(this.lineStrips);

		// Set axis labels for chart
		IAxeLayout axeLayout = chart.getAxeLayout();
		axeLayout.setXAxeLabel("Eje X");
		axeLayout.setYAxeLabel("Eje Y");
		axeLayout.setZAxeLabel("Eje Z");

		// Set precision of tick values
		axeLayout.setXTickRenderer(new IntegerTickRenderer());
		axeLayout.setYTickRenderer(new IntegerTickRenderer());
		axeLayout.setZTickRenderer(new IntegerTickRenderer());

		// Define ticks for axis
		axeLayout.setXTickProvider(new SmartTickProvider(10));
		axeLayout.setYTickProvider(new SmartTickProvider(10));
		axeLayout.setZTickProvider(new SmartTickProvider(10));

		// Set map viewpoint
		chart.getView().setViewPoint(new Coord3d(-2 * Math.PI / 3, Math.PI / 4, 0));
	}

	private static void loadFile(String filename) {
		String line = "";
		String cvsSplitBy = ";";

		City city;
		double x;
		double y;
		double z;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				line = line.replace(',', '.');
				// Use semicolon as separator
				String[] cityCoordenates = line.split(cvsSplitBy);

				x = Double.parseDouble(cityCoordenates[0]);
				y = Double.parseDouble(cityCoordenates[1]);
				z = Double.parseDouble(cityCoordenates[2]);

				city = new City(x, y, z);
				TourManager.addCity(city);
			}
		} catch (IOException e) {
			System.out.println("No se pudo leer el archivo.");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Line getFrontier() {
		return frontier;
	}
}
