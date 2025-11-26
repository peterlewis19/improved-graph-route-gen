import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileHandler {
    //1. read road names, parallel to the nodes they accompany |❎|
    //2. read coords for each node, in order to make the map |❎|
    //3. read adjacency matrix for each node |❌|

    //do I make an adjacency matrix data structure or do i add neighbours to a node
    // adding neighbours to a node, will be called experiment 1 THIS IS TOO HARD NVM
    // add neighbours using a 2d array
    private static int numOfNodes;

    //adds all Nodes from the input file
    public static ArrayList<Node> readNodesFromFile(String fileName){
        //node will contain the coordinates, and the name of the node
        ArrayList<Node> allNodes = new ArrayList<>();
        String line = "";
        numOfNodes = 0;

        try (
                FileReader fr = new FileReader(fileName);
                BufferedReader br = new BufferedReader(fr);
        ) {
            line = br.readLine();

            //until it runs out of lines, read the data from it
            while (line != null){
                allNodes.add(getNodeData(line));
                line=br.readLine();

                //avoids looping through file unnecessarily, as
                // adjacency Matrix and the node data should have the same length
                numOfNodes++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return allNodes;
    }

    //gets the data of a Node given its line in the file
    private static Node getNodeData(String rawLine) {
        // starts off "51.4683, 0.1212, Rochester Avenue"
        //splitLine = ["51.4683", "0.1212", "Rochester Avenue"]
        String[] splitLine = rawLine.split(",");
        //latString, lonString = "51.4683", "0.1212"
        String latString = splitLine[0];
        String lonString = splitLine[1];
        String roadName = splitLine[2];

        //turns the strings of "51.4683" to 51.4683, for example
        if (latString.startsWith("\uFEFF")) {
            latString = latString.substring(1);
        }

        if (lonString.startsWith("\uFEFF")) {
            lonString = lonString.substring(1);
        }
        double lat = Double.parseDouble(latString);
        double lon = Double.parseDouble(lonString);
        double[] coords = new double[]{lat,lon};

        return new Node(coords,roadName);
    }

    public static int[][] getMatrixData(String fileName){
        int[][] adjMat = new int[numOfNodes][numOfNodes];
        String line = "";

        try (
                FileReader fr = new FileReader(fileName);
                BufferedReader br = new BufferedReader(fr);
        ) {
            line = br.readLine();

            //as it is symmetrical across diagonal
            // increase column to start from each time round
            int columnCount = 0;
            int currentColumn = 0;

            int rowCount = 0;
            //goes until each line of the file has been parsed
            while (line != null){
                //goes through each value in the line
                for (int i=columnCount; i < (int)((line.length()+1)/2); i++){
                    currentColumn = (2*i);
                    //each line is (2*numOfNodes)-1 long bc of commas (0,0,0,....,0,1,0,1,0)
                    int value = Integer.parseInt(line.substring(currentColumn,currentColumn+1));

                    //there is an edge between the line given to this method and currentColumn if value is 1
                    adjMat[rowCount][i] = value;
                    adjMat[i][rowCount] = value;
                    //bc of the symmetry
                }

                line=br.readLine();
                rowCount++;
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }


        return adjMat;
    }

    public static String[] getNodeNameArray(String fileName){
        String[] namesOfAllNodes = new String[numOfNodes];
        String line = "";
        int count = 0;

        try (
                FileReader fr = new FileReader(fileName);
                BufferedReader br = new BufferedReader(fr);
        ) {
            line = br.readLine();

            //until it runs out of lines, read the data from it
            while (line != null){
                namesOfAllNodes[count] = getNodeName(line);

                //for each new line
                line=br.readLine();
                count++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return namesOfAllNodes;
    }

    private static String getNodeName(String rawLine){
        // starts off "51.4683, 0.1212, Rochester Avenue"
        //splitLine = ["51.4683", "0.1212", "Rochester Avenue"]
        String[] splitLine = rawLine.split(",");

        String roadName = splitLine[2];

        return roadName;
    }

}

