package algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import util.Graph;
import util.Line;
import util.Obstacle;
import util.Point;
import util.SingleParticle;
import util.Path;

public class NSGAII {
	public Graph graph;
	public static Point startPoint;
	public static Point endPoint;
	public double distanceX;
	public final int NP = 10; // population size
	public final int crossoverPoint = 2; // crossoverPoint
	public int numY = 6;
	public LinkedList<Path> POP = new LinkedList<Path>();
	public LinkedList<Path> NDPOP = new LinkedList<Path>();
	static final double maxPointy = 20;
	static final double minPointy = -20;
	static Random rd = new Random();
	public LinkedList<Point> pointsToVisit = new LinkedList<Point>();
	public LinkedList<Point> pointsToVisitAfterFixed = new LinkedList<Point>();
	public double maxDistance = 0;
	public double minDistance = 0;
	public double maxSafety = 0;
	public double minSafety = 0;
	public double maxSmooth = 0;
	public double minSmooth = 0;

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
			for (int j = 0; j < newPath.n; j++) {
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
			POP.add(newPath);
		}

		for (i = 0; i < NP; i++) {

			for (int j = 0; j < Path.n; j++) {
				Point point = new Point(POP.get(i).points[j].x, POP.get(i).points[j].y);
				pointsToVisit.add(point);
			}
		}
//		for (i = 0; i < NP; i++) {
//			Path rightPath = InvalidSolutionOperator(POP.get(i));
//			for (int j = 0; j < rightPath.n; j++) {
//				Point point = new Point(rightPath.points[j].x, rightPath.points[j].y);
//				pointsToVisitAfterFixed.add(point);
//			}
//		}
	}

	public void InvalidSolutionOperator(LinkedList<Path> listPath) {
		for (int i = 0; i < listPath.size(); i++) {
			Path rightPath = InvalidSolutionOperator(listPath.get(i));
			for (int j = 0; j < rightPath.points.length; j++) {
				Point point = new Point(rightPath.points[j].x, rightPath.points[j].y);
				pointsToVisitAfterFixed.add(point);
			}
		}
	}

	public Path InvalidSolutionOperator(Path path) {
		List<Point> listPoint = new LinkedList<Point>(Arrays.asList(path.points));
		int count = 1;
//		System.out.println("SIZE" + listPoint.size());

		for (int i = 0; i < path.points.length - 1; i++) {
			int check = findWayAvoidObs(path.points[i], path.points[i + 1], listPoint, count, i);
			if (check > 0)
				count = count + check;
		}
//		System.out.println("SIZE" + listPoint.size());
		Path newPath = new Path(listPoint.size());
		for (int i = 0; i < listPoint.size(); i++) {
			newPath.points[i] = listPoint.get(i);
		}
		return newPath;
	}

	public int findWayAvoidObs(Point a, Point b, List<Point> listPoint, int count, int i) {
		Line tempLine = new Line(a, b);
		int check = 0;

		if (tempLine.isIntersectGraphReturnObstacles(graph) != null) {
//			System.out.println("--------" + i);
			Obstacle intersectObstacle = tempLine.isIntersectGraphReturnObstacles(graph);
			List<Point> LeftCorner = new LinkedList<Point>();
			List<Point> RightCorner = new LinkedList<Point>();

			for (int j = 0; j < intersectObstacle.points.length; j++) {
				if (checkOnSameSide(a, b, intersectObstacle.points[j]) == "RIGHT") {
					RightCorner.add(intersectObstacle.points[j]);
//					System.out.println("RIGHT" + intersectObstacle.points[j].x);
				} else if (checkOnSameSide(a, b, intersectObstacle.points[j]) == "LEFT") {
					LeftCorner.add(intersectObstacle.points[j]);
//					System.out.println("LEFT" + intersectObstacle.points[j].x);
				} else {
					listPoint.add(i + count, intersectObstacle.points[j]);
					count++;
//					System.out.println("ZERO");
				}
			}

//			System.out.println("LEFTSIDE" + LeftCorner.size() + " RIGHSIZE" + RightCorner.size());

			if (LeftCorner.size() < RightCorner.size()) {
				for (int j = 0; j < LeftCorner.size(); j++) {
					listPoint.add(i + count + j, LeftCorner.get(j));
				}
				check = check + LeftCorner.size();

			} else if (RightCorner.size() < LeftCorner.size()) {
				for (int j = 0; j < RightCorner.size(); j++) {
					listPoint.add(i + count + j, RightCorner.get(j));
				}
				check = check + RightCorner.size();
			}

			else {
				double minDistance = distanceBetweenTwoPoints(a, intersectObstacle.points[0]);
				Point intersectPoint = intersectObstacle.points[0];
				for (int j = 1; j < intersectObstacle.points.length; j++) {
//				System.out.println("true " + distanceBetweenTwoPoints(listPoint.get(i), intersectObstacle.points[j]));
					if (minDistance > (distanceBetweenTwoPoints(a, intersectObstacle.points[j]))) {
						minDistance = distanceBetweenTwoPoints(a, intersectObstacle.points[j]);
						intersectPoint = intersectObstacle.points[j];
					}
					if (RightCorner.contains(intersectPoint)) {
						for (int j1 = 0; j1 < LeftCorner.size(); j1++) {
							listPoint.add(i + count + j1, RightCorner.get(j1));
						}
						check = check + LeftCorner.size();
					} else {
						for (int j1 = 0; j1 < LeftCorner.size(); j1++) {
							listPoint.add(i + count + j1, LeftCorner.get(j1));
						}
						check = check + RightCorner.size();
					}
				}
			}
		}
		return check;
	}

	public String checkOnSameSide(Point a, Point b, Point h) {
		// subtracting co-ordinates of point A
		// from B and P, to make A as origin
		Point a1 = new Point(a.x, a.y);
		Point b1 = new Point(b.x, b.y);
		Point h1 = new Point(h.x, h.y);

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

	public LinkedList<Path> ranking(LinkedList<Path> listPath) {

		LinkedList<Path> listPathAfterRanking = new LinkedList<Path>();

		maxDistance = listPath.get(0).pathDistance();
		minDistance = listPath.get(0).pathDistance();
		maxSafety = listPath.get(0).pathSafety(graph);
		minSafety = listPath.get(0).pathSafety(graph);
		maxSmooth = listPath.get(0).pathSmooth();
		minSmooth = listPath.get(0).pathSmooth();

		LinkedList<Path>[] front = new LinkedList[NP];

		for (int i = 0; i < front.length; i++) {
			front[i] = new LinkedList<Path>();
		}

		for (int i = 0; i < listPath.size(); i++) {
			int tempN = 0;

			if (maxDistance < listPath.get(i).pathDistance())
				maxDistance = listPath.get(i).pathDistance();
			if (minDistance > listPath.get(i).pathDistance())
				minDistance = listPath.get(i).pathDistance();
			if (maxSafety < listPath.get(i).pathSafety(graph))
				maxSafety = listPath.get(i).pathSafety(graph);
			if (minSafety > listPath.get(i).pathSafety(graph))
				minSafety = listPath.get(i).pathSafety(graph);
			if (maxSmooth < listPath.get(i).pathSmooth())
				maxSmooth = listPath.get(i).pathSmooth();
			if (minSmooth > listPath.get(i).pathSmooth())
				maxSmooth = listPath.get(i).pathSmooth();

			for (int j = 0; j < listPath.size(); j++) {
				if (i != j) {
					if (checkDomination(listPath.get(i), listPath.get(j)) == true) {
						listPath.get(i).S.add(listPath.get(j));
					} else if (checkDomination(listPath.get(j), listPath.get(i)) == true) {
						System.out.println("i = " + i + " non - dominate " + " j= " + j);

						tempN++;
					}
				}
			}
			listPath.get(i).non_dominated = tempN;
		}

		for (int i = 0; i < listPath.size(); i++) {
			if (listPath.get(i).non_dominated == 0) {
				front[0].add(listPath.get(i));
			}
		}

		int frontNum = 0;

//		System.out.println("size" + listPath.length);
//		for (int i = 0; i < listPath.length; i++) {
//			System.out.println("particle[" + i + "]= " + listPath.get(i).non_dominated + " " + listPath.get(i).pathDistance()
//					+ " " + listPath.get(i).pathSafety(graph) + "  " + listPath.get(i).pathSmooth());
//		}

		while (front[frontNum].size() != 0) {
			LinkedList<Path> Q = new LinkedList<Path>();
			System.out.println("aaaa" + frontNum + " : " + Q.size());
			for (int i = 0; i < front[frontNum].size(); i++) {
				for (int j = 0; j < front[frontNum].get(i).S.size(); j++) {
					front[frontNum].get(i).S.get(j).non_dominated--;
					if (front[frontNum].get(i).S.get(j).non_dominated == 0) {
						Q.add(front[frontNum].get(i).S.get(j));
					}
				}
			}
			frontNum++;
			for (int k = 0; k < Q.size(); k++) {
				front[frontNum].add(Q.get(k));
			}
		}

		for (int i = 0; i < front.length; i++) {
			if (front[i].size() != 0) {
				System.out.println("-------Front " + i + "-------");
				for (Path path : front[i]) {
					System.out.println("		Path" + "  " + path.pathDistance() + " " + path.pathSafety(graph) + "  "
							+ path.pathSmooth());
				}
			}
		}
		for (int i = 0; i < front.length; i++) {
			if (front[i].size() != 0)
				crowdingDistance(front[i]);
		}

		for (int i = 0; i < front.length; i++) {
			if (front[i].size() != 0) {
				for (int j = 0; j < front[i].size(); j++) {
					listPathAfterRanking.add(front[i].get(j));
				}
			}
		}

		return listPathAfterRanking;

	}

	public boolean checkDomination(Path a, Path b) {
		if (((a.pathDistance() < b.pathDistance()) && (a.pathSafety(graph) <= b.pathSafety(graph))
				&& (a.pathSmooth() <= b.pathSmooth()))
				|| ((a.pathDistance() <= b.pathDistance()) && (a.pathSafety(graph) < b.pathSafety(graph))
						&& (a.pathSmooth() <= b.pathSmooth()))
				|| ((a.pathDistance() <= b.pathDistance()) && (a.pathSafety(graph) <= b.pathSafety(graph))
						&& (a.pathSmooth() < b.pathSmooth()))) {
			return true;
		}
		return false;
	}

	public void crowdingDistance(LinkedList<Path> front) {
		crowdingDistanceWithPathDistance(front);
		crowdingDistanceWithPathSafety(front);
		crowdingDistanceWithPathSmooth(front);

		front.sort(new CrowdingDistanceComparator());

		for (int i = 0; i < front.size(); i++) {
			System.out.println(
					" Sorted Path" + "  " + front.get(i).pathDistance() + " " + front.get(i).pathSafety(graph) + "  "
							+ front.get(i).pathSmooth() + " crowding_distance: " + front.get(i).crowding_distance);
		}

		System.out.println("---------------------");

	}

	public void crowdingDistanceWithPathDistance(LinkedList<Path> front) {
		front.sort(new PathDistanceComparator());

		front.get(0).crowding_distance = 10000;
		front.get(front.size() - 1).crowding_distance = 10000;

		for (int i = 1; i < front.size() - 1; i++) {
			front.get(i).crowding_distance = front.get(i).crowding_distance
					+ (front.get(i + 1).pathDistance() - front.get(i - 1).pathDistance()) / (maxDistance - minDistance);
		}
	}

	public void crowdingDistanceWithPathSafety(LinkedList<Path> front) {
		front.sort(new PathSafetyComparator());

		front.get(0).crowding_distance = 10000;
		front.get(front.size() - 1).crowding_distance = 10000;

		for (int i = 1; i < front.size() - 1; i++) {
			front.get(i).crowding_distance = front.get(i).crowding_distance
					+ (front.get(i + 1).pathSafety(graph) - front.get(i - 1).pathSafety(graph))
							/ (maxSafety - minSafety);
		}
	}

	public void crowdingDistanceWithPathSmooth(LinkedList<Path> front) {
		front.sort(new PathSmoothComparator());

		front.get(0).crowding_distance = 10000;
		front.get(front.size() - 1).crowding_distance = 10000;

		for (int i = 1; i < front.size() - 1; i++) {
			front.get(i).crowding_distance = front.get(i).crowding_distance
					+ (front.get(i + 1).pathSmooth() - front.get(i - 1).pathSmooth()) / (maxSmooth - minSmooth);
		}
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
			System.out.print("Distance: " + POP.get(i).pathDistance() + "\nSmooth: " + POP.get(i).pathSmooth()
					+ "\nSafety: " + POP.get(i).pathSafety(graph));
			System.out.println("\n");
		}
	}

	public LinkedList<Point> getPath() {
		return pointsToVisit;
	}

	public LinkedList<Point> getPathAfterFixed() {
		return pointsToVisitAfterFixed;
	}

	class PathDistanceComparator implements Comparator<Path> {
		@Override
		public int compare(Path path1, Path path2) {
			if (path1.pathDistance() == path2.pathDistance())
				return 0;
			else if (path1.pathDistance() > path2.pathDistance())
				return 1;
			else
				return -1;
		}
	}

	class PathSafetyComparator implements Comparator<Path> {
		@Override
		public int compare(Path path1, Path path2) {
			if (path1.pathSafety(graph) == path2.pathSafety(graph))
				return 0;
			else if (path1.pathSafety(graph) > path2.pathSafety(graph))
				return 1;
			else
				return -1;
		}
	}

	class PathSmoothComparator implements Comparator<Path> {
		@Override
		public int compare(Path path1, Path path2) {
			if (path1.pathSmooth() == path2.pathSmooth())
				return 0;
			else if (path1.pathSmooth() > path2.pathSmooth())
				return 1;
			else
				return -1;
		}
	}

	class CrowdingDistanceComparator implements Comparator<Path> {
		@Override
		public int compare(Path path1, Path path2) {
			if (path1.crowding_distance == path2.crowding_distance)
				return 0;
			else if (path1.crowding_distance > path2.crowding_distance)
				return -1;
			else
				return 1;
		}
	}

	public void printLinkedList(LinkedList<Path> listPath) {
		System.out.println("-------------");
		for (int i = 0; i < listPath.size(); i++) {
			System.out.print("Path [" + i + "] = " + listPath.get(i).pathDistance() + " ; "
					+ listPath.get(i).pathSmooth() + " ; " + listPath.get(i).pathSafety(graph));
			System.out.println("\n");
		}
	}

	public void SelectionOperation(LinkedList<Path> listAfterRanking, LinkedList<Path> listPath) {

		for (int i = 0; i < NP / 2; i++) {
			listPath.add(listAfterRanking.get(i));
		}
	}

	public void CrossoverOperation(LinkedList<Path> listPathPopC, LinkedList<Path> newListPath) {
		int i = 0;
		while (i < listPathPopC.size()) {
			Point temp;
			if (i > listPathPopC.size() - 2) {
				temp = listPathPopC.get(listPathPopC.size() - 2).points[crossoverPoint];
				listPathPopC.get(listPathPopC.size() - 2).points[crossoverPoint] = listPathPopC
						.get(listPathPopC.size()-1).points[crossoverPoint];
				listPathPopC.get(listPathPopC.size()-1).points[crossoverPoint] = temp;
				break;
			}
			temp = listPathPopC.get(i).points[crossoverPoint];
			listPathPopC.get(i).points[crossoverPoint] = listPathPopC.get(i + 1).points[crossoverPoint];
			listPathPopC.get(i + 1).points[crossoverPoint] = temp;

			newListPath.add(listPathPopC.get(i));
			newListPath.add(listPathPopC.get(i + 1));
			i = i + 2;
			System.out.println("i = " + i);
		}
	}

	public NSGAII(Graph graph, Point startPoint, Point endPoint) {
		LinkedList<Path> NEWPOP = new LinkedList<Path>();
		LinkedList<Path> POPc = new LinkedList<Path>();
		LinkedList<Path> listAfterRanking = new LinkedList<Path>();
		this.graph = graph;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		getDistanceX(numY, startPoint, endPoint);
		initialize();

//		printResult();
//		List<Path> listPaths = new LinkedList<Path>(Arrays.asList(POP));
		listAfterRanking = ranking(POP);
		SelectionOperation(listAfterRanking, POPc);
		CrossoverOperation(POPc, NEWPOP);
		printLinkedList(NEWPOP);
		InvalidSolutionOperator(NEWPOP);
		getPath();
		getPathAfterFixed();

//System.out.println("distance" + particles[0].pathDistance());
	}
}
