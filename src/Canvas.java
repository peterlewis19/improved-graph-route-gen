import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Canvas extends JComponent {

    private ArrayList<Node> allNodes;
    private int[][] adjMatrix;
    private double HORIZONTAL_SCALE_FACTOR = 22000;
    private double VERTICAL_SCALE_FACTOR = 21000;
    private double Y_OFFSET = -0.11;
    private double X_OFFSET = -51.44;
    //private ArrayList<Integer> generatedRoute;

    public Canvas(ArrayList<Node> allNodes, int[][] adjMatrix){
            //, ArrayList<Integer> generatedRoute){
        this.adjMatrix = adjMatrix;
        this.allNodes = allNodes;
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

        // Set rendering hints for smooth lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set line color and thickness
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3)); // Line thickness = 2

        //just drawing dots, so start and end X and Y will be the same
        for (int i=0; i < allNodes.size(); i++){

            int startY = (int) ((X_OFFSET + allNodes.get(i).getX()) *HORIZONTAL_SCALE_FACTOR);
            int startX = (int) ((Y_OFFSET + allNodes.get(i).getY()) *VERTICAL_SCALE_FACTOR);

            int endY = startY;
            int endX = startX;

            g.drawLine(startX, 650-startY, endX, 650-endY);
            //g.drawString(String.valueOf(i),startX,startY);
        }


        //draws a line between teh 2 closest points

        //draws lines based on an adjacency matrix
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));

        //this traverses only the part above the line of symmetry
        for (int i = 0; i < adjMatrix.length; i++){
            for (int j = i; j < adjMatrix[0].length; j++){
                //if the row and column share an edge
                if (adjMatrix[i][j] >= 1){
                    int startY = (int) ((X_OFFSET + allNodes.get(i).getX()) *HORIZONTAL_SCALE_FACTOR);
                    int startX = (int) ((Y_OFFSET + allNodes.get(i).getY()) *VERTICAL_SCALE_FACTOR);

                    int endY = (int) ((X_OFFSET + allNodes.get(j).getX()) *HORIZONTAL_SCALE_FACTOR);
                    int endX = (int) ((Y_OFFSET + allNodes.get(j).getY()) *VERTICAL_SCALE_FACTOR);

                    /*System.out.println("Drawing a line from "+startX+" To "+endX);
                    System.out.println("The coords were "+allNodes.get(i).getX()+ " to "+allNodes.get(j).getX());
                    System.out.println("After step 1: "+(allNodes.get(i).getX()+X_OFFSET)+ " to "+(allNodes.get(j).getX()+X_OFFSET));
                    System.out.println("After step 2: "+((allNodes.get(i).getX()+X_OFFSET)*HORIZONTAL_SCALE_FACTOR)+" to "+ ((allNodes.get(j).getX()+X_OFFSET)*HORIZONTAL_SCALE_FACTOR));
*/
                    g.drawLine(startX, 650-startY, endX, 650-endY);
                }
            }
        }
    }
}

