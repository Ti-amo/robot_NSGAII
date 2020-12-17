package algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;



import util.Graph;
import util.Line;
import util.Obstacle;
import util.Point;
import util.Path;

public class NSGAII {
	public Graph graph;
	public static Point startPoint;
	public static Point endPoint;
	public double distanceX;
	public final int NP = 1; // population size
	public int numY = 6;
	public Path[] particles = new Path[NP];
	static final double maxPointy = 20;
	static final double minPointy = -20;
	static Random rd = new Random();
	public LinkedList<Point> pointsToVisit = new LinkedList<Point>();
	public Path[] NDPOP = new Path[NP];

	public double distanceBetweenTwoPoints(Point a, Point b) {
		double a2, b2, distance;
		a2 = Math.pow(Math.abs(a.x - b.x), 2);
		b2 = Math.pow(Math.abs(a.y - b.y), 2);
		distance = Math.sqrt(a2 + b2);

		return distance;
	}

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

//		for (i = 0; i < NP; i++) {
//			
//			for (int j = 0; j < Path.n; j++) {
////				System.out.print("(" + particles[i].points[j].x + ", " + particles[i].points[j].y + ")");
//				Point point = new Point(particles[i].points[j].x, particles[i].points[j].y);
//				pointsToVisit.add(point);
////				System.out.println("AAAAA" + particles[i].points[j].x);
//			}
////			InvalidSolutionOperator(particles[i]);
//		}
		for (i = 0; i < NP; i++) {
		Path rightPath = InvalidSolutionOperator(particles[i]);
		for (int j = 0; j < rightPath.n ; j++) {
//			System.out.print("(" + particles[i].points[j].x + ", " + particles[i].points[j].y + ")");
			Point point = new Point(rightPath.points[j].x , rightPath.points[j].y);
			pointsToVisit.add(point);
//			System.out.println("pointtoVisit " + j + ": " + rightPath.points[j].x + " " + rightPath.points[j].y);
		}
	}
	}

	public Path InvalidSolutionOperator(Path path) {
		List<Point> listPoint = new LinkedList<Point>(Arrays.asList(path.points));
		int size = listPoint.size();
		int count = 1;
//		int i= 0;
		System.out.println("SIZE" + listPoint.size());

		for (int i = 0; i < size - 1; i++) {
//			Line tempLine = new Line(path.points[i], path.points[i + 1]);
//			System.out.println("pree" + i + " " + path.points[i] + "next " + path.points[i + 1]);
////			System.out.println("--------" + i);
//			if (tempLine.isIntersectGraphReturnObstacles(graph) != null) {
//				System.out.println("true" + tempLine.isIntersectGraphReturnObstacles(graph) + " " + i);
//				Obstacle intersectObstacle = tempLine.isIntersectGraphReturnObstacles(graph);
//				Point intersectPoint = intersectObstacle.points[0];
//				double minDistance = distanceBetweenTwoPoints(path.points[i], intersectObstacle.points[0]);
//				for (int j = 1; j < intersectObstacle.points.length - 1; j++) {
////					System.out.println("true " + distanceBetweenTwoPoints(listPoint.get(i), intersectObstacle.points[j]));
//					if (minDistance > distanceBetweenTwoPoints(path.points[i], intersectObstacle.points[j])) {
//						minDistance = distanceBetweenTwoPoints(path.points[i], intersectObstacle.points[j]);
//						intersectPoint = intersectObstacle.points[j];
//
//					}
//				}
//
//				listPoint.add(i + count, intersectPoint);
//				Line tempLine2 = new Line(listPoint.get(i+count), path.points[i+1]);
//				
//				count++;
////				System.out.println("minDistance" + minDistance);
////				System.out.println("true" + tempLine.isIntersectGraphReturnObstacles(graph) + " " + i);
////				System.out.println("---------");
//			}
//			i++;
if (findWayAvoidObs(path.points[i], path.points[i + 1], listPoint, count, i) == true) count++;
		}
//	for (int j = 0; i < listPoint.size(); i++) {
//			System.out.println("point " + listPoint.get(i));
//		}
		System.out.println("SIZE" + listPoint.size());
//		Point[] System.out.println( = new Point[listPoint.size()];
		Path newPath = new Path(listPoint.size());
		 for (int i = 0; i < listPoint.size(); i++)  {
			 newPath.points[i] = listPoint.get(i) ;
		 }
			 
//		arrayPoints = listPoint.toArray(arrayPoints);
		
//		for (int j = 0; j < newPath.n; j++) {
//			System.out.println("AAAAAAA" + newPath.points[j].x);
////            newPath.points[j] = array[j];
//		}
		return newPath;
	}
	
	public boolean findWayAvoidObs(Point a, Point b, List<Point> listPoint, int count, int i) {
		Line tempLine = new Line(a, b);

		if (tempLine.isIntersectGraphReturnObstacles(graph) != null) {
			System.out.println("--------" + i);
			Obstacle intersectObstacle = tempLine.isIntersectGraphReturnObstacles(graph);
//			Point intersectPoint = intersectObstacle.points[0];
//			System.out.println("aaaaaaaaaaa" + checkOnSameSide(a,b,intersectObstacle.points[0]));
//			double minDistance = distanceBetweenTwoPoints(a, intersectObstacle.points[0]);
//			for (int j = 1; j < intersectObstacle.points.length; j++) {
////				System.out.println("true " + distanceBetweenTwoPoints(listPoint.get(i), intersectObstacle.points[j]));
//				if (minDistance > (distanceBetweenTwoPoints(a, intersectObstacle.points[j]))) {
//					minDistance = distanceBetweenTwoPoints(a, intersectObstacle.points[j]);
//					intersectPoint = intersectObstacle.points[j];
//
//				}
//			}
			List<Point> LeftCorner = new LinkedList<Point>();
			List<Point> RightCorner = new LinkedList<Point>();

			for (int j = 0; j < intersectObstacle.points.length; j++) {
				if (checkOnSameSide(a, b, intersectObstacle.points[j]) == "RIGHT") {
					RightCorner.add(intersectObstacle.points[j]);
					System.out.println("RIGHT" + intersectObstacle.points[j].x);
				} else if (checkOnSameSide(a, b, intersectObstacle.points[j]) == "LEFT") {
					LeftCorner.add(intersectObstacle.points[j]);
					System.out.println("LEFT");
				} else {
					listPoint.add(i + count, intersectObstacle.points[j]);
					count++;
				}
			}

			System.out.println("LEFTSIDE" + LeftCorner.size() + " RIGHSIZE" + RightCorner.size());

			if (LeftCorner.size() < RightCorner.size()) {
				for (int j = 0; j < LeftCorner.size(); j++) {
					listPoint.add(i + count + j, LeftCorner.get(j));
				}
				count = count + LeftCorner.size();
			} else if (RightCorner.size() < LeftCorner.size()) {
				for (int j = 0; j < RightCorner.size(); j++) {
					listPoint.add(i + count + j, RightCorner.get(j));
				}
				count = count + LeftCorner.size();
			}

//			else {
//				double minDistance = distanceBetweenTwoPoints(a, intersectObstacle.points[0]);
//				Point intersectPoint = intersectObstacle.points[0];
//				for (int j = 1; j < intersectObstacle.points.length; j++) {
////				System.out.println("true " + distanceBetweenTwoPoints(listPoint.get(i), intersectObstacle.points[j]));
//				if (minDistance > (distanceBetweenTwoPoints(a, intersectObstacle.points[j]))) {
//					minDistance = distanceBetweenTwoPoints(a, intersectObstacle.points[j]);
//					intersectPoint = intersectObstacle.points[j];
//				}
//				if (RightCorner.contains(intersectPoint)) {
//					for (int j1 = 0; j1< LeftCorner.size(); j1++) {
//						listPoint.add(i+count + j1, RightCorner.get(j1));
//					}
//					count = count + LeftCorner.size();
//				}
//				else {
//					for (int j1 = 0; j1< LeftCorner.size(); j1++) {
//						listPoint.add(i+count + j1, LeftCorner.get(j1));
//					}
//					count = count + LeftCorner.size();
//				}
//			}
//			}
			
//			count++;

//			Line tempLine2 = new Line(listPoint.get(i+count -1), b);
//
//			if (tempLine2.isIntersectGraphReturnObstacles(graph) != null) {
//				System.out.println("trueeeeeeeeeee");
//				Obstacle intersectObstacle2 = tempLine.isIntersectGraphReturnObstacles(graph);
//				Point intersectPoint2 = intersectObstacle2.points[0];
//				double minDistance2 = distanceBetweenTwoPoints(listPoint.get(i+count -1), intersectObstacle2.points[0]);
//				for (int j = 1; j < intersectObstacle.points.length; j++) {
////				System.out.println("true " + distanceBetweenTwoPoints(listPoint.get(i), intersectObstacle.points[j]));
//					if (minDistance2 > (distanceBetweenTwoPoints(listPoint.get(i+count -1), intersectObstacle2.points[j]))) {
//						minDistance2 = distanceBetweenTwoPoints(listPoint.get(i+count -1), intersectObstacle2.points[j]);
//						intersectPoint2 = intersectObstacle2.points[j];
//
//					}
//				}
//
//				listPoint.add(i + count, intersectPoint2);
//
//				count++;
//			}
//			System.out.println("minDistance" + minDistance);
//			System.out.println("true" + tempLine.isIntersectGraphReturnObstacles(graph) + " " + i);
//			System.out.println("---------");
			return true;
		}
		return false;
	}

	public String checkOnSameSide(Point a, Point b, Point h) {
		// subtracting co-ordinates of point A
		// from B and P, to make A as origin
		Point a1 = new Point(a.x, a.y);
		Point b1 =new Point(b.x, b.y);
		Point h1=new Point(h.x, h.y);

		b1.x -= a1.x;
		b1.y -= a1.y;
		h1.x -= a1.x;
		h1.y -= a1.y;
//
		// Determining cross Product
		double cross_product = b1.x * h1.y - b1.y * h1.x;

		// return RIGHT if cross product is positive
		if (cross_product > 0)
			return "RIGHT";

		// return LEFT if cross product is negative
		if (cross_product < 0)
			return "LEFT";

		// return ZERO if cross product is zero.
		return "ZERO";
	}
	public double[] sorting(double a[]) {
		for (int i = 0; i < a.length - 1; i++) {
			for (int j = i + 1; j < a.length; j++) {
				double temp;
				if (a[i] > a[j]) {
					temp = a[i];
					a[i] = a[j];
					a[j] = temp;
				}
			}
		}
		return a;
	}

	public void printArr(double a[]) {
		for (int i = 0; i < a.length; i++) {
			System.out.print("Arr" + a[i] + " ");

		}
		System.out.println("\n");
	}

	public Path[] removeElement(Path[] array, int index) {
		for (int i = index; i < array.length - 1; i++) {
			array[i] = array[i + 1];
		}
		return array;
	}

	public void ranking(Path[] path) {

		double bestDistance = particles[0].pathDistance();
		double bestSmooth = particles[0].pathSmooth();
		double bestSafe = particles[0].pathSafety(graph);

		double distanceArr[] = new double[NP];
		double smoothArr[] = new double[NP];
		double safeArr[] = new double[NP];

		for (int i = 0; i < NP; i++) {
			distanceArr[i] = particles[i].pathDistance();
			smoothArr[i] = particles[i].pathSmooth();
			safeArr[i] = particles[i].pathSafety(graph);
		}

//		printArr(sorting(distanceArr));
//		printArr(sorting(smoothArr));
//		printArr(sorting(safeArr));

		int current = 0;
		int front = 0;
		Path[] tempPath = path;
		ArrayList<ArrayList<Path>> F = new ArrayList<ArrayList<Path>>();

		while (current < path.length) {
			System.out.println("current" + tempPath[current].pathDistance());
			ArrayList<Path> tempFront = new ArrayList<Path>();
			removeElement(tempPath, current);
			for (Path path2 : tempPath) {
				if (path[current].pathDistance() <= path2.pathDistance()) {
					if (path[current].pathSafety(graph) <= path2.pathSafety(graph)) {
						if (path[current].pathSmooth() >= path2.pathSmooth()) {
							tempFront.add(path[current]);
							current++;
							break;
						}
					}
				}
			}
			F.add(tempFront);
			current++;

		}

		printResult(F);

//		for (int i = 0; i < NP; i++) {
//			Path temp = new Path(NP);
//			int count = 0;
//			if (bestDistance < particles[i].pathDistance()) {
//				count 
//			}
//		}
	}

	private void printResult(ArrayList<ArrayList<Path>> f) {
		// TODO Auto-generated method stub
		for (ArrayList obj : f) {

			ArrayList<Path> temp = obj;

			for (Path job : temp) {
				System.out.print("particle[" + f.indexOf(obj) + "] = " + job.pathDistance() + " " + " "
						+ job.pathSafety(graph) + " " + job.pathSmooth() + " ");
			}
			System.out.println();
		}
	}

	public void printResult() {
		for (int i = 0; i < NP; i++) {
			System.out.println("Particle " + i);
			System.out.print("Distance: " + particles[i].pathDistance() + "\nSmooth: " + particles[i].pathSmooth()
					+ "\nSafety: " + particles[i].pathSafety(graph));
			System.out.println("\n");
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
//		printResult();
//		ranking(particles);

	}
}
