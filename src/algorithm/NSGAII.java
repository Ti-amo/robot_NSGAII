package algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import util.Graph;
import util.Line;
import util.Obstacle;
import util.Point;
import util.Path;

public class NSGAII {
	public Graph graph;
	public Point startPoint;
	public Point endPoint;
	public double distanceX;
	public final int NP = 1; // population size
	public int numY = 6;
	public Path[] particles = new Path[NP];
	static final double maxPointy = 20;
	static final double minPointy = -20;
	static Random rd = new Random();
	public LinkedList<Point> pointsToVisit = new LinkedList<Point>();

	public double getDistanceX(double numY, Point start, Point end) {
		double a2, b2, distance;
		double num;
		a2 = Math.pow(Math.abs(start.x - end.x), 2);
		b2 = Math.pow(Math.abs(start.y - end.y), 2);
		distance = Math.sqrt(a2 + b2);
		System.out.println("Start to end point distance: " + distance);
		num = distance / numY;
		return num;
	}

	public void initialize() {
		distanceX = getDistanceX(numY, startPoint, endPoint);
		double tPointy;
		Point tPoint;
		int i;
		for (i = 0; i < NP; i++) {
			Path newPath = new Path(numY + 1);
//			do {
			for (int j = 0; j < Path.n; j++) {
				System.out.println("so lan" + j);
				if (j == 0)
					newPath.points[j] = startPoint;
				else {
					do {
					tPointy = rd.nextDouble() * ((maxPointy - minPointy) + 1) + minPointy;
					tPoint = Path.convertPointytoPoints(tPointy, j * distanceX, startPoint, endPoint);

					} while (tPoint.isIntersectGraph(graph));
					newPath.pointy[j] = tPointy;
					newPath.points[j] = tPoint;

				}
				newPath.points[numY] = endPoint;

			}
			particles[i] = newPath;
		}

		for (i = 0; i < NP; i++) {
			System.out.print("\nParticle " + i + ": ");
			for (int j = 0; j < Path.n; j++) {
				System.out.print("(" + particles[i].points[j].x + ", " + particles[i].points[j].y + ")");
				Point point = new Point(particles[i].points[j].x, particles[i].points[j].y);
				pointsToVisit.add(point);
			}
		}
	}
	
	public LinkedList<Point> getPath() {
		return pointsToVisit;
	}

	public NSGAII(Graph graph, Point startPoint, Point endPoint) {
		this.graph = graph;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		getDistanceX(numY, startPoint, endPoint);
		initialize();
		getPath();
	}

	

}
