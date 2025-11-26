import java.util.ArrayList;

public class Node {
    private final double[] coords;
    private final String roadName;
    //private ArrayList<Integer> neighbours;

    public Node(double[] coords, String roadName){
        this.coords = coords;
        this.roadName = roadName;
    }

    public double[] getCoords(){
        return coords;
    }

    public double getX(){
        return coords[0];
    }

    public double getY(){
        return coords[1];
    }

    public String toString(){
        double[] coords = getCoords();
        double x = coords[0];
        double y = coords[1];

        String strX = Double.toString(x);
        String strY = Double.toString(y);

        return strX + "," + strY;
    }

    //returns the distance to another node
    public double distanceTo(Node matilda){
        double distance;

        double changeInX = Math.abs(coords[0] - matilda.getCoords()[0]);
        double changeInY = Math.abs(coords[1] - matilda.getCoords()[1]);

        //use pythagorean theorem to work out the distance diagonally
        distance = Math.sqrt(Math.pow(changeInX,2) + Math.pow(changeInY,2));

        return distance;
    }

    public String getRoadName(){
        return roadName;
    }


}
