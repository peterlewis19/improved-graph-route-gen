import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RouteCanvas extends JComponent {

    private final ArrayList<Node> allNodes;
    private static ArrayList<Integer> generatedRoute;

    public RouteCanvas(ArrayList<Integer> generatedRoute, ArrayList<Node> allNodes){
        RouteCanvas.generatedRoute = generatedRoute;
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

        double HORIZONTAL_SCALE_FACTOR = 22000;
        double VERTICAL_SCALE_FACTOR = 21000;
        double Y_OFFSET = -0.11;
        double X_OFFSET = -51.44;

        // Set rendering hints for smooth lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set line color and thickness
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(4)); // Line thickness = 2

        /*int startX = 0;
        int startY = 0;

        int endX=0;
        int endY = 0;*/


        //draw the route assuming lines are connected
        for (int i = 0; i < generatedRoute.size()-1; i++){
            int startY = (int) ((X_OFFSET + allNodes.get(generatedRoute.get(i)).getX()) *HORIZONTAL_SCALE_FACTOR);
            int startX = (int) ((Y_OFFSET + allNodes.get(generatedRoute.get(i)).getY()) *VERTICAL_SCALE_FACTOR);

            int endY = (int) ((X_OFFSET + allNodes.get(generatedRoute.get(i+1)).getX()) *HORIZONTAL_SCALE_FACTOR);
            int endX = (int) ((Y_OFFSET + allNodes.get(generatedRoute.get(i+1)).getY()) *VERTICAL_SCALE_FACTOR);

            System.out.print(allNodes.get(generatedRoute.get(i)).getRoadName()+ " to ");

            g2d.drawLine(startX, 650-startY, endX, 650-endY);
        }

        //System.out.print(allNodes.get(generatedRoute.get(generatedRoute.size()-1)).getRoadName());
        //System.out.println("START COORD: "+startX+","+(650-startY)+" AND END COORD: "+endX+","+(650-endY));

        //System.out.println("DRAWING ROUTE");

    }

    public void setRoute(ArrayList<Integer> route){
        generatedRoute = route;
    }

    public ArrayList<Integer> getRoute(){
        return generatedRoute;
    }


}

