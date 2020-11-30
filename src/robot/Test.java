package robot;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Test {
	public static void main(String[] args) throws IOException {   	
		GUIRobotics gui = new GUIRobotics(500, 110, 11);
        gui.generateEnvironment("obstacles.txt");
		Graph test_graph = new Graph("obstacles.txt");
		Scanner scan = new Scanner(new File("input.txt"));
		LinkedList<Point> pointsToVisit = new LinkedList<Point>();
		LinkedList<Point> result = new LinkedList<Point>();
		
		//startPoint + endPoint
		double x = scan.nextDouble();
		while(x != -1) {			
			double y = scan.nextDouble();
			pointsToVisit.addLast(new Point(x, y));
			x = scan.nextDouble();
		}
		scan.close();
		
		// otherPoint
		try {
			GraphDivision graphDivision = new GraphDivision(test_graph, pointsToVisit);
			MakeWeightedGraph makeGraph = new MakeWeightedGraph(test_graph, pointsToVisit, graphDivision.midPoints, graphDivision.MAKLINK);
//			for(Point pt: graphDivision.midPoints) {
//				pt.printPoint();
//			}
//			for(LineNode line: graphDivision.lines) {
//				line.printLine();
//			}
			TSPBaB TSP = new TSPBaB(makeGraph.array, pointsToVisit);
			System.out.println("Path to follow:");
			for(int i = 0; i < TSP.answer.size() - 1; i++) {
				AStar findPath = new AStar(test_graph, graphDivision.midPoints, graphDivision.MAKLINK, TSP.answer.get(i), TSP.answer.get(i + 1));
				for(int j = 0; j < findPath.path.size() - 1; j++) {
					Point p = findPath.path.get(j);
					p.printPoint();
					result.addLast(p);
				}
			}
			TSP.answer.getLast().printPoint();
			result.addLast(TSP.answer.getLast());
//			System.out.printf("Length: %.2f\n", TSP.cost);
			ArrayList<Obstacle.Point> points = new ArrayList<>();
			for(Point pt: result) {
				Obstacle.Point point = new Obstacle.Point(pt.x, pt.y);
				points.add(point);
			}
//				
//			
//			File f = new File("obstacles_result.txt");
//			FileWriter fw = new FileWriter(f);
//			fw.write("Path to follow:\n");
//			for(Point p: result) {
//				fw.write("(" + p.x + ", " + p.y + ") ");
//			}
//			fw.write("\n-1");
//			fw.close();
//	        gui.canvas.drawLines(points, pointsToVisit);
			gui.canvas.drawLineStartToEnd(pointsToVisit);
	        Thread.sleep(1000);
		}
		catch(Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}
		System.out.println("End!");
	}
}
