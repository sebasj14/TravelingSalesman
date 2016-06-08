package co.edu.udea.optimizacion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.log4j.Logger;
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

	private static Logger log = Logger.getLogger(SimulatedAnnealing.class);

	public List<CroppableLineStrip> tourLines = new ArrayList<CroppableLineStrip>();
	private static List<City> bestSolution = new ArrayList<>();

	private static Line frontierLine;
	private static Plane frontierPlane;

	private static double frontierP1X;
	private static double frontierP1Y;
	private static double frontierP1Z;
	private static double frontierP2X;
	private static double frontierP2Y;
	private static double frontierP2Z;
	private static double frontierP3X;
	private static double frontierP3Y;
	private static double frontierP3Z;

	public static void main(String[] args) {
		long initTime = System.currentTimeMillis();
		loadFileWithCities(args[0]);

		initializeFrontier(args);

		double temperature = 100;

		// Initial solution
		Tour currentSolution = new Tour();
		currentSolution.generateRandomIndividual();

		City initialCity = getInitialCity(args);
		setFirstCity(currentSolution, initialCity);

		simulate(temperature, currentSolution);
		long finalTime = System.currentTimeMillis();
		System.out.println("###### TIME SPENT #####\n" + (finalTime - initTime) + " millisecs");
		drawSolution();
	}

	private static void simulate(double temperature, Tour currentSolution) {
		// Set as current best
		Tour best = new Tour(currentSolution.getTour());

		System.out.println("Initial solution cost: " + currentSolution.getDistance());

		// Loop until system has cooled
		while (temperature > 1) {
			// Create new solution tour
			Tour newSolution = new Tour(currentSolution.getTour());

			shuffleTour(newSolution);
			currentSolution = getNewSolution(temperature, currentSolution, newSolution);

			// Keep track of the best solution found
			if (currentSolution.getDistance() < best.getDistance()) {
				best = new Tour(currentSolution.getTour());
			}

			temperature = temperature - temperature*0.2;
		}

		System.out.println("Best solution cost: " + best.getDistance());
		System.out.println("Tour: " + best);

		bestSolution = best.getTour();
	}

	private static void setFirstCity(Tour currentSolution, City initialCity) {
		// Get index from initial city
		int initialCityIndex = 0;
		for (int cityIndex = 0; cityIndex < currentSolution.getTour().size(); cityIndex++) {
			if (currentSolution.getCity(cityIndex).equals(initialCity)) {
				initialCityIndex = cityIndex;
			}
		}

		// Put initial city at the first position of the tour
		City citySwap1a = currentSolution.getCity(initialCityIndex);
		City citySwap2a = currentSolution.getCity(0);
		currentSolution.setCity(0, citySwap1a);
		currentSolution.setCity(initialCityIndex, citySwap2a);
	}

	private static City getInitialCity(String[] args) {
		double initialX = Double.parseDouble(args[10]);
		double initialY = Double.parseDouble(args[11]);
		double initialZ = Double.parseDouble(args[12]);
		City initialCity = new City(initialX, initialY, initialZ);
		return initialCity;
	}

	private static void drawSolution() {
		try {
			AnalysisLauncher.open(new SimulatedAnnealing());
		} catch (Exception e) {
			log.error("Error al intentar graficar.", e);
		}
	}

	private static Tour getNewSolution(double temperature, Tour currentSolution, Tour newSolution) {
		double currentEnergy = currentSolution.getDistance();
		double neighbourEnergy = newSolution.getDistance();

		// Decide if we should accept the new solution
		if (acceptanceProbability(currentEnergy, neighbourEnergy, temperature) > Math.random()) {
			currentSolution = new Tour(newSolution.getTour());
		}
		return currentSolution;
	}

	private static void shuffleTour(Tour newSolution) {
		// Get random positions in the tour from the second city to the last city.
		int tourPos1 = ThreadLocalRandom.current().nextInt(1, TourManager.getNumberOfCities());
		int tourPos2 = ThreadLocalRandom.current().nextInt(1, TourManager.getNumberOfCities());

		// Swap cities randomly
		City citySwap1 = newSolution.getCity(tourPos1);
		City citySwap2 = newSolution.getCity(tourPos2);
		newSolution.setCity(tourPos2, citySwap1);
		newSolution.setCity(tourPos1, citySwap2);
	}

	private static void initializeFrontier(String[] args) {
		frontierP1X = Double.parseDouble(args[1]);
		frontierP1Y = Double.parseDouble(args[2]);
		frontierP1Z = Double.parseDouble(args[3]);
		frontierP2X = Double.parseDouble(args[4]);
		frontierP2Y = Double.parseDouble(args[5]);
		frontierP2Z = Double.parseDouble(args[6]);
		frontierP3X = Double.parseDouble(args[7]);
		frontierP3Y = Double.parseDouble(args[8]);
		frontierP3Z = Double.parseDouble(args[9]);

		Vector3D frontierP1 = new Vector3D(frontierP1X, frontierP1Y, frontierP1Z);
		Vector3D frontierP2 = new Vector3D(frontierP2X, frontierP2Y, frontierP2Z);
		Vector3D frontierP3 = new Vector3D(frontierP3X, frontierP3Y, frontierP3Z);

		frontierLine = new Line(frontierP1, frontierP2);
		frontierPlane = new Plane(frontierP1, frontierP2, frontierP3);
	}

	public static double acceptanceProbability(double currentEnergy, double neighbourEnergy, double temperature) {
		// If the new solution is better, accept it
		// If the new solution is worse, calculate an acceptance probability
		return (neighbourEnergy < currentEnergy) ? 1.0 : Math.exp((currentEnergy - neighbourEnergy) / temperature);
	}

	public void init() throws Exception {
		Mapper planeFunction = new Mapper() {
			@Override
			public double f(double x, double y) {
				double coeffX = frontierPlane.getNormal().getX();
				double coeffY = frontierPlane.getNormal().getY();
				double coeffZ = frontierPlane.getNormal().getZ();
				double bias = frontierPlane.getOffset(new Vector3D(0, 0, 0));

				return (-coeffX * x - coeffY * y - bias) / coeffZ;
			}
		};

		chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
		drawCities();
		drawFrontierPlane(planeFunction);
		drawTour();
		setView();
	}

	private void setView() {
		IAxeLayout axeLayout = chart.getAxeLayout();
		axeLayout.setXAxeLabel("Eje X");
		axeLayout.setYAxeLabel("Eje Y");
		axeLayout.setZAxeLabel("Eje Z");

		axeLayout.setXTickRenderer(new IntegerTickRenderer());
		axeLayout.setYTickRenderer(new IntegerTickRenderer());
		axeLayout.setZTickRenderer(new IntegerTickRenderer());

		axeLayout.setXTickProvider(new SmartTickProvider(10));
		axeLayout.setYTickProvider(new SmartTickProvider(10));
		axeLayout.setZTickProvider(new SmartTickProvider(10));

		chart.getView().setViewPoint(new Coord3d(-2 * Math.PI / 3, Math.PI / 4, 0));
	}

	private void drawTour() {
		this.parseFileWithCities("./Datos1.csv");
		chart.getScene().getGraph().add(this.tourLines);
	}

	private void drawFrontierPlane(Mapper mapper) {
		Range range = new Range(-20, 20);
		int steps = 10;

		final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
		surface.setColorMapper(
				new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);

		chart.getScene().getGraph().add(surface);
	}

	private void drawCities() {
		double x;
		double y;
		double z;
		Coord3d[] points = new Coord3d[bestSolution.size()];
		Color[] colors = new Color[bestSolution.size()];

		for (int i = 0; i < bestSolution.size(); i++) {
			x = bestSolution.get(i).getX();
			y = bestSolution.get(i).getY();
			z = bestSolution.get(i).getZ();
			points[i] = new Coord3d(x, y, z);
			colors[i] = Color.BLACK;
		}

		colors[0] = Color.RED;

		Scatter scatter = new Scatter(points, colors, 5);
		chart.getScene().add(scatter);
	}

	private static void loadFileWithCities(String filename) {
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
				String[] cityCoordinates = line.split(cvsSplitBy);

				x = Double.parseDouble(cityCoordinates[0]);
				y = Double.parseDouble(cityCoordinates[1]);
				z = Double.parseDouble(cityCoordinates[2]);

				city = new City(x, y, z);
				TourManager.addCity(city);
			}
		} catch (IOException e) {
			log.error("No se pudo leer el archivo.", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("No se pudo leer el archivo.", e);
				}
			}
		}
	}

	public List<CroppableLineStrip> parseFileWithCities(String filename) {
		CroppableLineStrip lineStrip = new CroppableLineStrip();
		lineStrip.setWireframeColor(Color.BLUE);

		// Add cities to the line strip
		for (City city : bestSolution) {
			lineStrip.add(new Point(new Coord3d(city.getX(), city.getY(), city.getZ())));
		}

		// Close trip to initial city
		lineStrip.add(new Point(new Coord3d(bestSolution.get(0).getX(), bestSolution.get(0).getY(), bestSolution.get(0).getZ())));

		CroppableLineStrip frontier = new CroppableLineStrip();
		frontier.setWireframeColor(Color.RED);
		frontier.setWidth(1);
		Point point1 = new Point(new Coord3d(frontierP1X, frontierP1Y, frontierP1Z));
		Point point2 = new Point(new Coord3d(frontierP2X, frontierP2Y, frontierP2Z));
		frontier.add(point1);
		frontier.add(point2);

		tourLines.add(lineStrip);
//		tourLines.add(frontier);

		return tourLines;
	}

	public static Line getFrontierLine() {
		return frontierLine;
	}

	public static Plane getFrontierPlane() {
		return frontierPlane;
	}
}
