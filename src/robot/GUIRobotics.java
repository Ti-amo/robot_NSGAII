package robot;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class GUIRobotics {

    private Frame mainFrame;
    private Panel controlPanel;
    MyCanvas canvas;
    private static int size = 500;
    private static double range = 150;
    private static int numOfRange = 10;


    public GUIRobotics(int size, double range, int numOfRange) {
        this.size = size;
        this.range = range;
        this.numOfRange = numOfRange;
        prepareGUI();
    }

    private void prepareGUI() {
        mainFrame = new Frame("GUI for Robot Path Planning");
        mainFrame.setSize(size + 60, size + 60);
        mainFrame.setResizable(false);
        mainFrame.setLayout(new GridLayout(1, 1));
        mainFrame.setLocationRelativeTo(null);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        controlPanel = new Panel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(controlPanel);
        mainFrame.setVisible(true);
    }

    void generateEnvironment(String obtacles_file) {
        canvas = new MyCanvas();

        controlPanel.add(canvas);

        // draw Obtacles
        ArrayList<Obstacle> obstacles = Obstacle.getObtacles(obtacles_file);
        int count = 1;

        for (Obstacle obstacle : obstacles) {
            for (int i = 0; i < obstacle.points.size() - 1; i++) {
                canvas.drawLine(obstacle.points.get(i), obstacle.points.get(i + 1));
            }
            canvas.drawLine(obstacle.points.get(0), obstacle.points.get(obstacle.points.size() - 1));
        }        
        

        // draw Oxy
        Graphics2D g2 = (Graphics2D) canvas.getGraphics();
        g2.drawLine(MyCanvas.OX, MyCanvas.OY, MyCanvas.OY, MyCanvas.OY);
        g2.drawLine(MyCanvas.OX, MyCanvas.OY, MyCanvas.OX, MyCanvas.OX);
        g2.drawString("O", MyCanvas.OX - 10, MyCanvas.OY + 10);
        g2.drawString("x", MyCanvas.OY + 5, MyCanvas.OY);
        g2.drawString("y", MyCanvas.OX, MyCanvas.OX);

        // draw gird
        float[] dash1 = {2f, 0f, 2f};
        BasicStroke bs1 = new BasicStroke(1,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,
                1.0f,
                dash1,
                2f);
        g2.setStroke(bs1);
        for (int i = 0; i < numOfRange; i++) {
            g2.draw(new Line2D.Double(MyCanvas.OX, MyCanvas.OY - size / range * 10 * (i + 1),
                    MyCanvas.OY, MyCanvas.OY - size / range * 10 * (i + 1)));

            g2.draw(new Line2D.Double(MyCanvas.OX + size / range * 10 * (i + 1), MyCanvas.OY,
                    MyCanvas.OX + size / range * 10 * (i + 1), MyCanvas.OX));

            g2.drawString(String.valueOf(range / numOfRange * (i + 1)),
                    (int) (MyCanvas.OX + size / range * 10 * (i + 1) - 10),
                    MyCanvas.OY + 10);

            g2.drawString(String.valueOf(range / numOfRange * (i + 1)),
                    MyCanvas.OX,
                    (int) (MyCanvas.OY - size / range * 10 * (i + 1)));
        }


        mainFrame.setVisible(true);
    }

    static class MyCanvas extends Canvas {

        static int OX = 10;
        static int OY = size - OX;

        private double alpha = size / range;

        public MyCanvas() {
            setSize(size, size);
        }

        public void drawLine(Obstacle.Point p1, Obstacle.Point p2) {
            Graphics2D g2 = (Graphics2D) getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.red);
            g2.draw(new Line2D.Double(OX + p1.x * alpha, OY - p1.y * alpha, OX + p2.x * alpha, OY - p2.y * alpha));
        }
        
        public void drawLineStartToEnd(LinkedList<Point> pointsToVisit) {
            Graphics2D g2 = (Graphics2D) getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.blue);
            g2.draw(new Line2D.Double(OX + pointsToVisit.getFirst().x * alpha, OY - pointsToVisit.getFirst().y * alpha, OX + pointsToVisit.getLast().x * alpha, OY - pointsToVisit.getLast().y * alpha));
        }

        public void drawLines(ArrayList<Obstacle.Point> points, LinkedList<Point> pointsToVisit) {
            Graphics2D g2 = (Graphics2D) getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLUE);
            for (int i = 0; i < points.size() - 1; i++) {
                g2.draw(new Line2D.Double(OX + points.get(i).x * alpha, OY - points.get(i).y * alpha, OX + points.get(i + 1).x * alpha , OY - points.get(i + 1).y * alpha));
            }
            
            g2.setColor(Color.RED);
            for (int i = 0; i < points.size(); i++) {
            	Point pt = new Point(points.get(i).x, points.get(i).y);
            	if(pt.indexInSet(pointsToVisit) == -1) {
            		g2.setColor(Color.BLACK);
                	g2.fill(new Ellipse2D.Double(OX + points.get(i).x * alpha - 3, OY - points.get(i).y * alpha - 3, 6, 6));
                	g2.setColor(Color.RED);
            	}
            	else
            		g2.fill(new Ellipse2D.Double(OX + points.get(i).x * alpha - 4, OY - points.get(i).y * alpha - 4, 8, 8));
            }
        }
        
        public void drawLinesWithMiddle(Obstacle.Point pt1, Obstacle.Point pt2) {
            Graphics2D g2 = (Graphics2D) getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLUE);
            g2.draw(new Line2D.Double(OX + pt1.x * alpha, OY - pt1.y * alpha, OX + pt2.x * alpha, OY - pt2.y * alpha));

            
            g2.setColor(Color.RED);
            Point pt = new Point((pt1.x + pt2.x)/2, (pt1.y + pt2.y)/2);             
            g2.fill(new Ellipse2D.Double(OX + pt.x * alpha - 3, OY - pt.y * alpha - 3, 6, 6));
        }
        
        public void drawLinesWithoutMiddle(Obstacle.Point pt1, Obstacle.Point pt2) {
            Graphics2D g2 = (Graphics2D) getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLUE);
            g2.draw(new Line2D.Double(OX + pt1.x * alpha, OY - pt1.y * alpha, OX + pt2.x * alpha, OY - pt2.y * alpha));
        }

    }

//, LinkedList<Point> pointsToVisit
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        GUIRobotics gui = new GUIRobotics(500, 110, 11);
        gui.generateEnvironment("obstacles.txt");

        System.setIn(new FileInputStream("maklink.txt"));
        Scanner sc = new Scanner(System.in);

        while (sc.hasNext()) {
            sc.nextLine();

            // read path planning
            String numbers[] = sc.nextLine().replaceAll(",", "")
                    .replaceAll("\\(", "")
                    .replaceAll("\\)", "").split("\\s+");

            ArrayList<Obstacle.Point> points = new ArrayList<>();
            for (int i = 0; i < numbers.length / 2; i++) {
                points.add(new Obstacle.Point(Double.parseDouble(numbers[2 * i]), Double.parseDouble(numbers[2 * i + 1])));
            }

//          gui.canvas.drawLines(points, pointsToVisit);
            for(int i = 0; i < points.size()/2; i++) {
            	gui.canvas.drawLinesWithMiddle(points.get(2*i), points.get(2*i + 1));
            }
            Thread.sleep(1000);

            sc.nextLine();
        }
        sc.close();

    }

}