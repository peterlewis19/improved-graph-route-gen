import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RouteCanvas extends JComponent {

    private ArrayList<Node> allNodes;
    private int[][] adjMatrix;
    private double HORIZONTAL_SCALE_FACTOR = 9000;
    private double VERTICAL_SCALE_FACTOR = 8000;
    private double Y_OFFSET = -0.06;
    private double X_OFFSET = -51.41;
    private static ArrayList<Integer> generatedRoute;

    public RouteCanvas(ArrayList<Integer> generatedRoute, ArrayList<Node> allNodes){
        this.generatedRoute = generatedRoute;
        this.allNodes = allNodes;
    }

    //adjacency matrix, for drawing the connections of nodes????

    /*
    0000000
    0001000
    0000000
    0100000
    0000000
    0000000
    0000000

    this shows that the only edge is between Nodes 1 and 3
    notice the line of symmetry along the diagonal
    so only need to read on diagonal half of it

    for (int i = 0; i < adjMatrix; i++){
        for (int j = i; j < adjMatrix; j++){
            //this traverses only the part above the line of symmetry
        }
    }
    *
    * */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Set rendering hints for smooth lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set line color and thickness
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2)); // Line thickness = 2

        g2d.setColor(Color.BLACK);

        //draw the route assuming lines are connected
        for (int i = 0; i < generatedRoute.size()-1; i++){
            int startY = (int) ((X_OFFSET + allNodes.get(i).getX()) *HORIZONTAL_SCALE_FACTOR);
            int startX = (int) ((Y_OFFSET + allNodes.get(i).getY()) *VERTICAL_SCALE_FACTOR);

            int endY = (int) ((X_OFFSET + allNodes.get(i+1).getX()) *HORIZONTAL_SCALE_FACTOR);
            int endX = (int) ((Y_OFFSET + allNodes.get(i+1).getY()) *VERTICAL_SCALE_FACTOR);

            g2d.drawLine(startX, startY, endX, endY);
        }

        System.out.println("DRAWING ROUTE");

    }

    public void setRoute(ArrayList<Integer> route){
        generatedRoute = route;
    }

    public ArrayList<Integer> getRoute(){
        return generatedRoute;
    }


}

