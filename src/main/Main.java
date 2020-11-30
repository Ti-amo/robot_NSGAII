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
		// Tao moi truong
		GUIRobotics gui = new GUIRobotics(500, 110, 11);
		gui.generateEnvironment("obstacles.txt");

		// Doc du lieu dau vao
		Graph graph = new Graph("obstacles.txt");
		LinkedList<Point> startEndPoints = readPointData("input.txt");
		GraphDivision graphDivision = new GraphDivision(graph, startEndPoints);

		try {
			NSGAII pointsToVisit = new NSGAII(graph, startEndPoints.getFirst(), startEndPoints.getLast());
			LinkedList<Point> findPath = pointsToVisit.getPath();
			for (int i = 0; i < findPath.size() - 1; i++) {
				gui.canvas.drawLine(findPath.get(i), findPath.get(i + 1), Color.BLACK);
			}
			gui.canvas.drawLineStartToEnd(startEndPoints);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}
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
