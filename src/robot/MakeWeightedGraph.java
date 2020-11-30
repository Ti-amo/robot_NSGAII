package robot;

import java.util.LinkedList;

public class MakeWeightedGraph {
	LinkedList<Point> points;
	int n;
	double[][] array;
	
	public MakeWeightedGraph(Graph myGraph, LinkedList<Point> points, LinkedList<Point> midPoints, LinkedList<LineNode> lines) {
		this.points = points;
		n = points.size();
		this.array = new double[n][n];
		for(int i = 0; i < n; i++) 
			for(int j = i; j < n; j++) {
				if(i == j) 
					array[i][j] = 0;
				else {
					AStar findPath = new AStar(myGraph, midPoints, lines, points.get(i), points.get(j));
					array[i][j] = findPath.length;
					array[j][i] = findPath.length;
				}
			}
	}

}
