package robot;
import java.util.*;

public class GraphDivision {
	LinkedList<Point> midPoints = new LinkedList<Point>();
	LinkedList<LineNode> MAKLINK = new LinkedList<LineNode>();
	LinkedList<LineNode> lines = new LinkedList<LineNode>(); // dung de luu tru cac doan da noi 
	LinkedList<Point> visit = new LinkedList<Point>();

	// return if m, n same side or not with line p1p2
	public boolean SameSide(Point p1, Point p2, Point m, Point n) {
		double a = p1.y - p2.y;
		double b = p2.x - p1.x;
		double c = -a*p1.x - b*p1.y;
		
		// 4 points on same line 
		if(a*m.x + b*m.y + c == 0 && a*n.x + b*n.y + c == 0) return false;
		
		if((a*m.x + b*m.y + c)*(a*n.x + b*n.y + c) <= 0) return false;
		return true;			
	}
	
	// Check if angle between obstacles satisfied not greater than 180
    public boolean AngleCheck(Graph G, int i, int k, Point p) {
    	Point x = G.obstacles[i].point[k];
    	Point y = p;
    	Point z = G.obstacles[i].point[(k + 1) % (G.obstacles[i].cornum)];
    	Point t = G.obstacles[i].point[(k + G.obstacles[i].cornum - 1) % (G.obstacles[i].cornum)];
    	
    	return SameSide(x, y, z, t) == false;
    }
    
	public GraphDivision(Graph myGraph, LinkedList<Point> points){			
		this.visit = points;
		
		for(int i = 0; i < myGraph.num; i++) {
			for(int k = 0; k < myGraph.obstacles[i].cornum; k++) {				
				
					LinkedList<LineNode> list = new LinkedList<LineNode>(); // danh sach cac doan thang noi tu dinh
					
					// create the lines to the working space boundary walls (0-100,0-100)
					Point a = new Point(myGraph.obstacles[i].point[k].x, 0);						
					Point b = new Point(myGraph.obstacles[i].point[k].x, 100);										
					Point c = new Point(0, myGraph.obstacles[i].point[k].y);
					Point d = new Point(100, myGraph.obstacles[i].point[k].y);
					
					LineNode[] arr = new LineNode[4];
					arr[0] = new LineNode(myGraph.obstacles[i].point[k], a);
					arr[1] = new LineNode(myGraph.obstacles[i].point[k], b);
					arr[2] = new LineNode(myGraph.obstacles[i].point[k], c);
					arr[3] = new LineNode(myGraph.obstacles[i].point[k], d);
					
					// add to list
					for(int m = 0; m < 4; m++) {
						if(CollisionDetection.graphLine2(myGraph, arr[m].pt1, arr[m].pt2) == 2) {
							list.addLast(arr[m]);
						}					
					}					
				
				// Check the lines to another obstacles
				for(int j = 0; j < myGraph.num; j++) {		
					if(i == j) continue;
						
					for(int l = 0; l < myGraph.obstacles[j].cornum; l++) {
						if(CollisionDetection.graphLine2(myGraph, myGraph.obstacles[i].point[k], myGraph.obstacles[j].point[l]) == 4) {
							LineNode x = new LineNode(myGraph.obstacles[i].point[k], myGraph.obstacles[j].point[l]);
							list.addFirst(x);
						}					
					}
				}
				
				// Arrange lines by length ascending order
				Collections.sort(list, (m, n) -> m.getLength() < n.getLength() ? -1 : 1);
				
				LineNode[] candidates = new LineNode[2];
				for(int m = 0; m < list.size(); m++) {
					if(list.get(m).doInSet(lines) && AngleCheck(myGraph, i, k, list.get(m).pt2)) break;
					else if(list.get(m).doIntersectSet(lines) == false) {
						if(AngleCheck(myGraph, i, k, list.get(m).pt2)) {
							LineNode lineToCheck = list.get(m);									
							lines.addFirst(lineToCheck);
							Point midPoint = new Point((lineToCheck.pt1.x + lineToCheck.pt2.x)/2.0,(lineToCheck.pt1.y + lineToCheck.pt2.y)/2.0);						
							midPoints.addLast(midPoint);
//							System.out.println("Obs " + i + " cor " + k + " line " + m + " (" + lineToCheck.pt1.x + ", " + lineToCheck.pt1.y + "), (" + lineToCheck.pt2.x + ", " + lineToCheck.pt2.y + ")");
							break;
						}							
									
						Point left = myGraph.obstacles[i].point[(k + 1) % myGraph.obstacles[i].cornum];
											
						if(candidates[0] == null && candidates[1] == null) {
							if(list.get(m).pt1.greaterThan180(list.get(m).pt2, left, myGraph.obstacles[i])) {
									candidates[1] = list.get(m);
							}
							else {
									candidates[0] = list.get(m);
							}
						}
						else if(!(candidates[0] != null && candidates[1] != null)) {
							if(list.get(m).pt1.greaterThan180(list.get(m).pt2, left, myGraph.obstacles[i])) {
								if(candidates[1] == null) candidates[1] = list.get(m);
							}
							else {
								if(candidates[0] == null) candidates[0] = list.get(m);
							}
						}						
						
						if(candidates[0] != null && candidates[1] != null){										
							if(list.get(m).pt1.greaterThan180(list.get(m).pt2, left, myGraph.obstacles[i])) {
								if(candidates[0].pt1.greaterThan180(candidates[0].pt2, list.get(m).pt2, myGraph.obstacles[i]) == false)
									candidates[1] = list.get(m);
							}
							else {
								if(candidates[1].pt1.greaterThan180(candidates[1].pt2, list.get(m).pt2, myGraph.obstacles[i]) == false)
									candidates[0] = list.get(m);
							}
							
							if(candidates[0].pt1.greaterThan180(candidates[0].pt2, candidates[1].pt2, myGraph.obstacles[i]) == false) {
								for(int n = 0; n < 2; n++) {
									lines.addLast(candidates[n]);
									Point midPoint = new Point((candidates[n].pt1.x + candidates[n].pt2.x)/2.0,(candidates[n].pt1.y + candidates[n].pt2.y)/2.0);
									midPoints.addLast(midPoint);
								}
								break;
							}
						}
						
					}					
				}				
			}							
		}		
		
		// Add points to visit in MAKLINK
		for(Point p: visit) {
			if(p.isDuplicate(midPoints) == false) 
				midPoints.add(p);
		}
		
		// Create MAKLINK graph
		for(int i = 0; i < midPoints.size() - 1; i++) {						
			for(int j = i + 1; j < midPoints.size(); j++) {
				if(CollisionDetection.graphLine2(myGraph, midPoints.get(i), midPoints.get(j)) == 0) {
					int check = 1;
					LineNode line = new LineNode(midPoints.get(i), midPoints.get(j));					
					
					// check if intersect with existed lines
					for(LineNode k: MAKLINK) {
						if(line.doIntersect(k)) {
							check = 0;
							break;
						}
					}
					
					for(Point p: visit) {
						if(p.isOnSegment(line)) {
							check = 0;
							break;
						}
					}
					
					for(LineNode l: lines) {
						if(l.doIntersect(line) && CollisionDetection.graphLine2(myGraph, line.pt1, line.pt2) == 0) {
							check = 2;
							break;
						}
					}
				
					if(check == 1) {
						MAKLINK.addLast(line);
					}
				}
			}		
		}
		

	}
}
