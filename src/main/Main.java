package main;

import java.awt.Color;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

import algorithm.NSGAII;
import graph.GraphDivision;
import gui.GUIRobotics;
import util.Graph;
import util.Point;

public class Main {
	public static void main(String[] args) throws IOException {
		final long startTime = System.currentTimeMillis();	

		// Tao moi truong
		GUIRobotics gui = new GUIRobotics(1000, 100, 10);
		gui.generateEnvironment("obstacles.txt");

		// Doc du lieu dau vao
		Graph graph = new Graph("obstacles.txt");
		LinkedList<Point> startEndPoints = readPointData("input.txt");
		GraphDivision graphDivision = new GraphDivision(graph, startEndPoints);

		try {
			NSGAII pointsToVisit = new NSGAII(graph, startEndPoints.getFirst(), startEndPoints.getLast());
//			LinkedList<Point> findPathAfterFixed = pointsToVisit.getPath();
//
//			for (int i = 0; i < findPath.size() - 1; i++) {
//				gui.canvas.drawLine(findPath.get(i), findPath.get(i + 1), Color.BLACK);
//			}
			LinkedList<Point> findPathAfterFixed = pointsToVisit.getPathAfterFixed();
			for (int i = 0; i < (findPathAfterFixed.size() - 1); i++) {
				gui.canvas.drawLine(findPathAfterFixed.get(i), findPathAfterFixed.get(i + 1), Color.ORANGE);
			}
			gui.canvas.drawLineStartToEnd(startEndPoints);
			System.out.println("Path Distance: " + pointsToVisit.pathDistance);
			System.out.println("Path Smooth: " + pointsToVisit.pathSmooth);
			System.out.println("Path Safety: " + pointsToVisit.pathSafety);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}
		final long endTime = System.currentTimeMillis();
		 
		System.out.println("Total execution time: " + (endTime - startTime));
		System.out.println("End!");
	}

	public static LinkedList<Point> readPointData(String filename) throws IOException {
		Scanner scan = new Scanner(new File(filename));
		LinkedList<Point> pointsToVisit = new LinkedList<Point>();
		double x = scan.nextDouble();
		while (x != -1) {
			double y = scan.nextDouble();
			pointsToVisit.addLast(new Point(x, y));
			x = scan.nextDouble();
		}
		scan.close();

		return pointsToVisit;
	}
}
