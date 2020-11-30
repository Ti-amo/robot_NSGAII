package util;

import algorithm.NSGAII;

public class Path {
	public static int n;
	public double[] pointy;
	public Point[] points;
	public NSGAII NSGAII;
	
	public Path(int no) {
		n = no;
		pointy = new double[n];
		points = new Point[n];
	}
	
	public static Point convertPointytoPoints(double pointy, double pointx, Point start, Point end) {
		Point p;
		double x, y;
		double temp1, temp2, phi;
		temp1 = end.x - start.x;
		temp2 = end.y - start.y;
		phi = Math.atan(temp2/temp1);
//		System.out.println("phi: " + Math.toDegrees(phi));
		x = Math.cos(phi)*pointx - Math.sin(phi)*pointy + start.x;
		y = Math.sin(phi)*pointx + Math.cos(phi)*pointy + start.y;
		p = new Point(x, y);
//	 me	System.out.println(x + ", " + y);
		return p;
	}
	
	public double pathDistance() {
		double dis = 0;
		double a2, b2, distance;
		a2 = Math.pow(Math.abs(NSGAII.startPoint.x - NSGAII.endPoint.x), 2);
	    b2 = Math.pow(Math.abs(NSGAII.startPoint.y - NSGAII.endPoint.y), 2);
	    distance = Math.sqrt(a2 + b2);
		for (int i = 0; i < n-1; i++) {
			dis += Math.sqrt(Math.pow(distance/(n+1), 2) + Math.pow(pointy[i+1]-pointy[i], 2));
		}
		dis += Math.sqrt(Math.pow(distance/(n+1), 2) + Math.pow(NSGAII.startPoint.y-pointy[0], 2));
		dis += Math.sqrt(Math.pow(distance/(n+1), 2) + Math.pow(NSGAII.endPoint.y-pointy[n-1], 2));
		return dis;
	}
	
	public double pathSmooth() {
		double S = 0, temp = 0;
		double[] ang = new double[n];
		double a, b, c;
		for (int i = 0; i < n; i++) {
			if (i == 0) {
				a = Math.pow(points[i].x-NSGAII.startPoint.x,2) + Math.pow(points[i].y-NSGAII.startPoint.y,2);
				b = Math.pow(points[i].x-points[i+1].x,2) + Math.pow(points[i].y-points[i+1].y,2);
				c = Math.pow(points[i+1].x-NSGAII.startPoint.x,2) + Math.pow(points[i+1].y-NSGAII.startPoint.y,2);
				ang[i] = Math.toDegrees(Math.acos( (a+b-c) / Math.sqrt(4*a*b) ));
				if (ang[i] != ang[i]) {
					if ((a+b-c) / Math.sqrt(4*a*b) < -1) {
						ang[i] = Math.toDegrees(Math.acos(-1));
					}
					else if ((a+b-c) / Math.sqrt(4*a*b) > 1) {
						ang[i] = Math.toDegrees(Math.acos(1));
					}
				}
			}
			else if (i == n-1) {
				a = Math.pow(points[i].x-points[i-1].x,2) + Math.pow(points[i].y-points[i-1].y,2);
				b = Math.pow(points[i].x-NSGAII.endPoint.x,2) + Math.pow(points[i].y-NSGAII.endPoint.y,2);
				c = Math.pow(NSGAII.endPoint.x-points[i-1].x,2) + Math.pow(NSGAII.endPoint.y-points[i-1].y,2);
				ang[i] = Math.toDegrees(Math.acos( (a+b-c) / Math.sqrt(4*a*b) ));
				if (ang[i] != ang[i]) {
					if ((a+b-c) / Math.sqrt(4*a*b) < -1) {
						ang[i] = Math.toDegrees(Math.acos(-1));
					}
					else if ((a+b-c) / Math.sqrt(4*a*b) > 1) {
						ang[i] = Math.toDegrees(Math.acos(1));
					}
				}
			}
			else {
				a = Math.pow(points[i].x-points[i-1].x,2) + Math.pow(points[i].y-points[i-1].y,2);
				b = Math.pow(points[i].x-points[i+1].x,2) + Math.pow(points[i].y-points[i+1].y,2);
				c = Math.pow(points[i+1].x-points[i-1].x,2) + Math.pow(points[i+1].y-points[i-1].y,2);
				ang[i] = Math.toDegrees(Math.acos( (a+b-c) / Math.sqrt(4*a*b) ));
				if (ang[i] != ang[i]) {
					if ((a+b-c) / Math.sqrt(4*a*b) < -1) {
						ang[i] = Math.toDegrees(Math.acos(-1));
					}
					else if ((a+b-c) / Math.sqrt(4*a*b) > 1) {
						ang[i] = Math.toDegrees(Math.acos(1));
					}
				}
			}
		}
		for (int i = 0; i < n; i++) {
			temp += ang[i];
		}
		temp = temp/n;
		S = 180 - temp;
		return S;
	}
	
	public double disPointSeg(Point S0, Point S1, Point A) {
		double px = S1.x - S0.x;
        double py = S1.y - S0.y;
        double temp = (px*px) + (py*py);
        double u = ((A.x - S0.x) * px + (A.y - S0.y) * py) / (temp);
        if(u > 1){
            u = 1;
        }
        else if(u < 0){
            u = 0;
        }
        double x = S0.x + u * px;
        double y = S0.y + u * py;
        double dx = x - A.x;
        double dy = y - A.y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        return dist;
	}
	
//	public double pathSafety(Graph g) {
//		double[] Sa = new double[n+1];
//		double d, safety = 0;
//		for (int i = 0; i < n + 1; i++) {
//			Sa[i] = 10000;
//			for (int j = 0; j < g.num; j++) {
//				for (int h = 0; h < g.obstacles[j].cornum; h++) {
//					if (i == 0) {
//						d = disPointSeg(NSGAII.startPoint, points[i], g.obstacles[j].point[h]);
//					}
//					else if (i == n) {
//						d = disPointSeg(points[i-1], NSGAII.endPoint, g.obstacles[j].point[h]);
//					}
//					else {
//						d = disPointSeg(points[i-1], points[i], g.obstacles[j].point[h]);
//					}
//					if (d < Sa[i]) {
//						Sa[i] = d;
//					}
//				}
//			}
//		}
//		safety = Sa[0];
//		for (int i = 0; i < n + 1; i++) {
//			if (safety > Sa[i]) {
//				safety = Sa[i];
//			}
//		}
//		for (int j = 0; j < g.num; j++) {
//			for (int h = 0; h < g.obstacles[j].cornum; h++) {
//				if (h == g.obstacles[j].cornum - 1) {
//					for (int i = 0; i < n; i++) {
//						d = disPointSeg(g.obstacles[j].point[h], g.obstacles[j].point[0], points[i]);
//						if (d < safety) {
//							safety = d;
//						}
//					}
//					d = disPointSeg(g.obstacles[j].point[h], g.obstacles[j].point[0], NSGAII.startPoint);
//					if (d < safety) {
//						safety = d;
//					}
//					d = disPointSeg(g.obstacles[j].point[h], g.obstacles[j].point[0], NSGAII.endPoint);
//					if (d < safety) {
//						safety = d;
//					}
//				}
//				else {
//					for (int i = 0; i < n; i++) {
//						d = disPointSeg(g.obstacles[j].point[h], g.obstacles[j].point[h+1], points[i]);
//						if (d < safety) {
//							safety = d;
//						}
//					}
//					d = disPointSeg(g.obstacles[j].point[h], g.obstacles[j].point[h+1], NSGAII.startPoint);
//					if (d < safety) {
//						safety = d;
//					}
//					d = disPointSeg(g.obstacles[j].point[h], g.obstacles[j].point[h+1], NSGAII.endPoint);
//					if (d < safety) {
//						safety = d;
//					}
//				}
//			}
//		}
//		safety = Math.exp(-safety);
//		return safety;
//	}
}
