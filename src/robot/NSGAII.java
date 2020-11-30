package robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class NSGAII {
	public static Graph g;
	public static double distanceX;
	public static final int NP = 1; // population size
	public static int numY = 6;
	public static Point startPoint;
	public static Point endPoint;
	public static Path[] particles = new Path[NP];
	static final double maxPointy = 20;
	static final double minPointy = -20;
	public static GUIRobotics gui = new GUIRobotics(500, 110, 11);
	static Random rd = new Random();
	public static LinkedList<Point> pointsToVisit = new LinkedList<Point>();

	public static double getDistanceX(double numY, Point start, Point end) {
		double a2, b2, distance;
		double num;
		a2 = Math.pow(Math.abs(start.x - end.x), 2);
		b2 = Math.pow(Math.abs(start.y - end.y), 2);
		distance = Math.sqrt(a2 + b2);
		System.out.println("Start to end point distance: " + distance);
		num = distance / numY;
		return num;
	}

	public static void getStartEndPoint() {
		Scanner scan = null;
		try {
			scan = new Scanner(new File("input.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// startPoint + endPoint
		double x = scan.nextDouble();
		while (x != -1) {
			double y = scan.nextDouble();
			pointsToVisit.addLast(new Point(x, y));
			x = scan.nextDouble();
		}
		scan.close();

		startPoint = pointsToVisit.getFirst();
		endPoint = pointsToVisit.getLast();
	}

	public static void initialize() {
		distanceX = getDistanceX(numY, startPoint, endPoint);
		double tPointy;
		Point tPoint;
		System.out.println("Population size: " + particles.length);
		int i;
		for (i = 0; i < NP; i++) {
			Path newPath = new Path(numY + 1);
//			do {
			for (int j = 0; j < Path.n; j++) {
				System.out.println("so lan" + j);
				if (j == 0)
					newPath.points[j] = startPoint;
				else {
					tPointy = rd.nextDouble() * ((maxPointy - minPointy) + 1) + minPointy;
					tPoint = Path.convertPointytoPoints(tPointy, j * NSGAII.distanceX, NSGAII.startPoint,
							NSGAII.endPoint);
					newPath.pointy[j] = tPointy;
					newPath.points[j] = tPoint;
					System.out.println("Point" + NSGAII.startPoint.printPoint);
				}
				newPath.points[numY] = endPoint;
				
//		} else if (j > 0 && j < Path.n - 1) {
//			tPointy = rd.nextDouble() * ((maxPointy - minPointy) + 1) + minPointy;
//			tPoint = Path.convertPointytoPoints(tPointy, (j + 1) * NSGAII.distanceX, NSGAII.startPoint,
//					NSGAII.endPoint);
//			newPath.pointy[j] = tPointy;
//			newPath.points[j] = tPoint;
//		} else {
//			tPointy = rd.nextDouble() * ((maxPointy - minPointy) + 1) + minPointy;
//			tPoint = Path.convertPointytoPoints(tPointy, (j + 1) * NSGAII.distanceX, NSGAII.startPoint,
//					NSGAII.endPoint);
//			newPath.pointy[j] = tPointy;
//			newPath.points[j] = tPoint;
//		}

			}
//			System.out.println("Path Detect Collision " + pathDetectCollision(newPath));
//			} while ((pathDetectCollision(newPath) == false));

			particles[i] = newPath;
		}
		ArrayList<Obstacle.Point> points = new ArrayList<>();

		for (i = 0; i < NP; i++) {
			System.out.print("\nParticle " + i + ": ");
			LinkedList<Point> result = new LinkedList<Point>();
			for (int j = 0; j < Path.n; j++) {
				System.out.print("(" + particles[i].points[j].x + ", " + particles[i].points[j].y + ")");
				Obstacle.Point point = new Obstacle.Point(particles[i].points[j].x, particles[i].points[j].y);
				points.add(point);
			}
		}
		gui.canvas.drawLines(points, pointsToVisit);
	}

	static boolean pathDetectCollision(Path pa) {
		boolean colli = false;
		for (int i = 0; i < Path.n; i++) {
			if (i == 0) {
				if (CollisionDetection.graphLine(g, pa.points[i], NSGAII.startPoint)) {
					colli = true;
				}
			} else if (i == Path.n - 1) {
				if (CollisionDetection.graphLine(g, pa.points[i - 1], pa.points[i])
						|| CollisionDetection.graphLine(g, pa.points[i], NSGAII.endPoint)) {
					colli = true;
				}
			} else {
				if (CollisionDetection.graphLine(g, pa.points[i - 1], pa.points[i])) {
					colli = true;
				}
			}
		}
		return colli;
	}

	public static void NSGAII() {
		getDistanceX(numY, startPoint, endPoint);
		initialize();
	}

	public static void main(String[] args) throws FileNotFoundException {

		gui.generateEnvironment("obstacles.txt");
		Graph test_graph = new Graph("obstacles.txt");
		getStartEndPoint();

		NSGAII();
		gui.canvas.drawLineStartToEnd(pointsToVisit);
		try {
			GraphDivision graphDivision = new GraphDivision(test_graph, pointsToVisit);
			MakeWeightedGraph makeGraph = new MakeWeightedGraph(test_graph, pointsToVisit, graphDivision.midPoints,
					graphDivision.MAKLINK);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}
		System.out.println("End!");
	}
}
