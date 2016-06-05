package co.edu.udea.optimizacion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.primitives.CroppableLineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.providers.SmartTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.IntegerTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.CroppingView;
import org.jzy3d.plot3d.rendering.view.View;

import au.com.bytecode.opencsv.CSVReader;

public class SimulatedAnnealing extends AbstractAnalysis {

	private static List<City> solution = new ArrayList<>();

	public List<CroppableLineStrip> lineStrips = new ArrayList<CroppableLineStrip>();
	String newline = System.getProperty("line.separator");

	public List<CroppableLineStrip> parseFile(String filename) {
		try {

			// Get world map csv location
			File worldMapFile = new File(filename);

			// Create file reader and CSV reader from world map file
			FileReader fileReader = new FileReader(worldMapFile);
			CSVReader reader = new CSVReader(fileReader);

			// Create row holder and line number counter
			String[] rowHolder;
			int lineNumber = 1;

			// Create local line strip and set color
			CroppableLineStrip lineStrip = new CroppableLineStrip();
			lineStrip.setWireframeColor(Color.BLACK);

			// Loop through rows while a next row exists
			while ((rowHolder = reader.readNext()) != null) {

				switch (rowHolder.length) {
					case 1:
						if (rowHolder[0].equals("")) {

							// If row is blank, add line strip to list of line
							// strips and clear line strip
							lineStrips.add(lineStrip);
							lineStrip = new CroppableLineStrip();
							lineStrip.setWireframeColor(Color.BLACK);
							break;
						} else {

							// Throw error if a map point only has one coordinate
							String oneCoordinateError = "Error on line: " + lineNumber + newline + "The row contains only 1 coordinate";
							JOptionPane.showMessageDialog(null, oneCoordinateError, "Incorrect number of coordinates", JOptionPane.ERROR_MESSAGE);
							System.exit(-1);
						}
					case 2:
						try {

							// Add the map point to the line strip
							lineStrip.add(new Point(new Coord3d(Float.valueOf(rowHolder[0]), Float.valueOf(rowHolder[1]), 0.0)));
						} catch (NumberFormatException e) {

							// Throw error if a map point coordinate cannot be
							// converted to a Float
							String malformedCoordinateError = "Error on line: " + lineNumber + newline + "Coordinate is incorrectly formatted";
							JOptionPane.showMessageDialog(null, malformedCoordinateError, "Incorrect Format", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
							System.exit(-1);
						}
						break;
					case 3:
						try {

							// Add the map point to the line strip
							lineStrip.add(new Point(new Coord3d(Float.valueOf(rowHolder[0]), Float.valueOf(rowHolder[1]), Float.valueOf(rowHolder[2]))));
						} catch (NumberFormatException e) {

							// Throw error if a map point coordinate cannot be
							// converted to a Float
							String malformedCoordinateError = "Error on line: " + lineNumber + newline + "Coordinate is incorrectly formatted";
							JOptionPane.showMessageDialog(null, malformedCoordinateError, "Incorrect Format", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
							System.exit(-1);
						}
						break;
					default:

						// Throw error if the map point has more than three
						// coordinates
						String numCoordinateError = "Error on line: " + lineNumber + newline + "The row contains " + rowHolder.length + " coordinates";
						JOptionPane.showMessageDialog(null, numCoordinateError, "Incorrect number of coordinates", JOptionPane.ERROR_MESSAGE);
						System.exit(-1);
				}

				// Add the final lineStrip after while loop is complete.
				lineStrips.add(lineStrip);
				lineNumber++;
			}
			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("WARNING: World map file not found");

		} catch (IOException e) {
			e.printStackTrace();
		}
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

		solution = best.getTour();

		try {
			AnalysisLauncher.open(new SimulatedAnnealing());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No grafica." + e.toString());
		}

		System.out.println("Final solution distance: " + best.getDistance());
		System.out.println("Tour: " + solution);
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

		Scatter scatter = new Scatter(points, Color.RED);
		chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
		chart.getScene().add(scatter);
		// // Define range and precision for the function to plot
		// Range range = new Range(-3, 3);
		// int steps = 80;
		//
		// // Create the object to represent the function over the given range.
		// final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range,
		// steps, range, steps), mapper);
		// surface.setColorMapper(
		// new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
		// surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		// surface.setFaceDisplayed(true);
		// surface.setWireframeDisplayed(false);
		//
		// // Create a chart //
		// chart = AWTChartComponentFactory.chart(Quality.Advanced,
		// getCanvasType());
		// chart.getScene().getGraph().add(surface);

		// Create the world map chart
		AWTChartComponentFactory f = new AWTChartComponentFactory() {
			@Override
			public View newView(Scene scene, ICanvas canvas, Quality quality) {
				return new CroppingView(getFactory(), scene, canvas, quality);
			}
		};
		chart = f.newChart(Quality.Advanced, "awt");

		// Instantiate world map and parse the file
		this.parseFile("./Datos1.csv");

		// Add world map line stripe to chart
		chart.getScene().getGraph().add(this.lineStrips);

		// Set axis labels for chart
		IAxeLayout axeLayout = chart.getAxeLayout();
		axeLayout.setXAxeLabel("Longitude (deg)");
		axeLayout.setYAxeLabel("Latitude (deg)");
		axeLayout.setZAxeLabel("Altitude (km)");

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

		// Animate bounds change for demo
		Executors.newCachedThreadPool().execute(shiftBoundsTask());

	}

	private Runnable shiftBoundsTask() {
		return new Runnable() {
			int step = 1;

			@Override
			public void run() {
				int n = 0;
					n++;
					BoundingBox3d b = chart.getView().getBounds();
					chart.getView().setScaleX(b.getXRange().add(step), false);
					chart.getView().setScaleY(b.getYRange().add(step), false);
					chart.getView().setScaleZ(b.getZRange().add(step), false);
					chart.getView().shoot();
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
					}
			}

		};
	}
}
