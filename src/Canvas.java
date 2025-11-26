import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

//import static java.lang.Math.abs;
//import static java.lang.Math.min;

public class Canvas extends JComponent {

    private final ArrayList<Node> allNodes;
    private final int[][] adjMatrix;
    //private final String[] allRoadNames;
    //private ArrayList<Integer> generatedRoute;

    public Canvas(ArrayList<Node> allNodes, int[][] adjMatrix){
        this.adjMatrix = adjMatrix;
        this.allNodes = allNodes;
        //this.allRoadNames = allRoadNames;
        //this.generatedRoute = generatedRoute;

        //need to normalise the coordinates from 51.3xxxx to 51.5xxxx into the range 0 to 600
        /* FOR THE Y COORDINATES
        (int)((coord - 51.3) * 1000 * 3)
        (int)((coord + Y_OFFSET) * VERTICAL_SCALE_FACTOR)
        e.g 51.46575 -> 0.16575 * 1000 * 3 = 497.25
        * */

        // and for the X from 0.0xxxx to 0.2xxxxx into range from 0 to 800
        /* FOR THE X COORDINATES
        (int)(coord * 1000 * 4)
        (int)((coord + X_OFFSET) * HORIZONTAL_SCALE_FACTOR)
        e.g 0.097112 -> 0.097112 * 1000 * 4 = 388.448
        * */
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

        final double HORIZONTAL_SCALE_FACTOR = 22000;
        final double VERTICAL_SCALE_FACTOR = 21000;
        final double Y_OFFSET = -0.11;
        final double X_OFFSET = -51.44;

        //ArrayList<String> displayedRoadNames = new ArrayList<>();

        // Set rendering hints for smooth lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set line color and thickness
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(3)); // Line thickness = 2

        //just drawing dots, so start and end X and Y will be the same
        for (Node allNode : allNodes) {

            int startY = (int) ((X_OFFSET + allNode.getX()) * HORIZONTAL_SCALE_FACTOR);
            int startX = (int) ((Y_OFFSET + allNode.getY()) * VERTICAL_SCALE_FACTOR);

            g.drawLine(startX, 650 - startY, startX, 650 - startY);
            //g.drawString(allRoadNames[i], startX, 650-startY);
        }



        //draws lines based on an adjacency matrix
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));

        Stroke roadStroke = new BasicStroke(2);
        //Stroke  roadNameStroke = new BasicStroke(1);

        //this traverses only the part above the line of symmetry
        for (int i = 0; i < adjMatrix.length; i++){
            for (int j = i; j < adjMatrix[0].length; j++){
                g2d.setColor(Color.RED);
                g2d.setStroke(roadStroke);

                //if the row and column share an edge
                if (adjMatrix[i][j] >= 1){
                    int startY = (int) ((X_OFFSET + allNodes.get(i).getX()) *HORIZONTAL_SCALE_FACTOR);
                    int startX = (int) ((Y_OFFSET + allNodes.get(i).getY()) *VERTICAL_SCALE_FACTOR);

                    int endY = (int) ((X_OFFSET + allNodes.get(j).getX()) *HORIZONTAL_SCALE_FACTOR);
                    int endX = (int) ((Y_OFFSET + allNodes.get(j).getY()) *VERTICAL_SCALE_FACTOR);

                    g.drawLine(startX, 650-startY, endX, 650-endY);

                    /*g2d.setColor(Color.BLACK);
                    g2d.setStroke(roadNameStroke);
                    g.setFont(new Font("Arial", Font.PLAIN, 12));*/

                    //draw the roadname halfway down the road
                    /*if (!displayedRoadNames.contains(allRoadNames[i])) {
                        AffineTransform initialTransform = ((Graphics2D) g).getTransform();

                        //rotate around startX, startY
                        double angleToRotate = Math.toRadians(0) + Math.atan(((double) abs(endY - startY) /abs(endX-startX)));
                        g2d.rotate(angleToRotate,startX,650-startY);

                        g.drawString(allRoadNames[i],startX, 650-startY);
                        //g.drawString(allRoadNames[i], (abs(startX - endX) / 2) + min(startX, endX), 650-((abs(startY - endY) / 2)+min(startY, endY)));

                        displayedRoadNames.add(allRoadNames[i]);
                        g2d.setTransform(initialTransform);
                    }*/

                }
            }
        }
    }
}

