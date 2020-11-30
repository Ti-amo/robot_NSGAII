package robot;


public class CollisionDetection {
	static int INF = 10000;
	// Given three collinear points p, q, r,  check q in line pr?
    static boolean onSegment(Point p, Point q, Point r)  
    { 
        if (q.getX() <= Math.max(p.getX(), r.getX()) && 
            q.getX() >= Math.min(p.getX(), r.getX()) && 
            q.getY() <= Math.max(p.getY(), r.getY()) && 
            q.getY() >= Math.min(p.getY(), r.getY())) 
        { 
            return true; 
        } 
        return false; 
    } 
  
    // To find orientation of ordered triplet (p, q, r). 
    // 0 --> p, q and r are collinear, 1 --> Clockwise, 2 --> Counterclockwise 
    static int orientation(Point p, Point q, Point r)  
    { 
    	double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) 
                - (q.getX() - p.getX()) * (r.getY() - q.getY()); 
    	
        if (val == 0)  
        { 
            return 0; // colinear 
        } 
        return (val > 0) ? 1 : 2; // clock or counterclock wise 
    } 
  
    //true if line segment 'p1q1' and 'p2q2' intersect. 
    static boolean doIntersect(Point p1, Point q1, Point p2, Point q2)  
    { 
        // Find the four orientations needed for general and special cases 
        int o1 = orientation(p1, q1, p2); 
        int o2 = orientation(p1, q1, q2); 
        int o3 = orientation(p2, q2, p1); 
        int o4 = orientation(p2, q2, q1); 
  
        // General case 
        if (o1 != o2 && o3 != o4) { return true; } 
  
        // Special Cases 
        // p1, q1 and p2 are collinear and p2 lies on segment p1q1 
        if (o1 == 0 && onSegment(p1, p2, q1)) { return true; } 
  
        // p1, q1 and p2 are collinear and q2 lies on segment p1q1 
        if (o2 == 0 && onSegment(p1, q2, q1)) { return true; } 
  
        // p2, q2 and p1 are collinear and p1 lies on segment p2q2 
        if (o3 == 0 && onSegment(p2, p1, q2)) { return true; } 
  
        // p2, q2 and q1 are collinear and q1 lies on segment p2q2 
        if (o4 == 0 && onSegment(p2, q1, q2)) { return true; } 
  
        // Or else
        return false;  
    } 
     
    //true if the point p lies inside the polygons
    static boolean polyPoint(Polygons poly, Point p) 
    { 
        // There must be at least 3 vertices in polygon[] 
        if (poly.cornum < 3) { return false; } 
  
        // Create a point for line segment from p to infinite 
        Point extreme = new Point(INF, p.getY()); 
  
        // Count intersections of the above line with sides of polygon 
        int count = 0, i = 0; 
        do 
        { 
            int next = (i + 1) % poly.cornum; 
  
            // Check if the line 'p' to 'extreme' intersects with the line  
            // 'poly.point[i]' to 'poly.point[next]' 
            if (doIntersect(poly.point[i], poly.point[next], p, extreme))  
            { 
                // If the point 'p' is collinear with line 'i-next', if it does return true
                if (orientation(poly.point[i], p, poly.point[next]) == 0) 
                { 
                    return onSegment(poly.point[i], p, poly.point[next]); 
                } 
  
                count++; 
            } 
            i = next; 
        } while (i != 0); 
  
        // Return true if count is odd, false otherwise 
        return (count % 2 == 1); // Same as (count%2 == 1) 
    } 
    
    static boolean polyLine(Polygons poly, Point p, Point q) {
    	int cornum = poly.cornum;
    	int i = 0;
    	if ((polyPoint(poly, p) == true) || (polyPoint(poly, q) == true)) {
    		return true;
    	}
    	i = 0;
    	do {
    		int next = (i + 1) % cornum; 
    		if (doIntersect(poly.point[i], poly.point[next], p, q)) {
    			return true;
    		}
    		i = next;
    	} while (i != 0);
    	return false;
    }
    
    static boolean polyPoly(Polygons poly1, Polygons poly2) {
    	int num1 = poly1.cornum;
    	int num2 = poly2.cornum;
    	int i = 0;
    	i = 0;
    	do {
    		int next = (i + 1) % num1; 
    		if (polyLine(poly2, poly1.point[i], poly1.point[next]) == true) {
    			return true;
    		}
    		i = next;
    	} while (i != 0);
    	i = 0;
    	do {
    		int next = (i + 1) % num2; 
    		if (polyLine(poly1, poly2.point[i], poly2.point[next]) == true) {
    			return true;
    		}
    		i = next;
    	} while (i != 0);
    	return false;
    }
    
    static boolean graphPoint (Graph g, Point p) {
    	for (int i = 0; i < g.num; i++) {
    		if (polyPoint(g.obstacles[i], p) == true) {
    			return true;
    		}
    	}
    	return false;
    }
    
    static boolean graphLine (Graph g, Point p, Point q) {
    	for (int i = 0; i < g.num; i++) {
    		if (polyLine(g.obstacles[i], p, q) == true) {
    			return true;
    		}
    	}
    	return false;
    }
    
    static int polyLine2(Polygons poly, Point p, Point q) {
    	int cornum = poly.cornum;
    	int i = 0, j = 0;
    	do {
    		int next = (i + 1) % cornum; 
    		if (doIntersect(poly.point[i], poly.point[next], p, q)) {
    			j++;
    		}
    		i = next;
    	} while (i != 0);
    	return j;
    }
    
    static int graphLine2(Graph g, Point p, Point q) {
    	int result = 0;
    	for (int i = 0; i < g.num; i++) {
    		result += polyLine2(g.obstacles[i], p, q); 
    		}
    	return result;
    }

	public static double numGraphLine(Graph g, Point point, Point startPoint) {
		int result = 0;
    	for (int i = 0; i < g.num; i++) {
    		result += polyLine2(g.obstacles[i], point, startPoint); 
    		}
    	return result;
	}
    
}
