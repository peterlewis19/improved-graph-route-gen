import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        /*
        * These .csv files contain real road data from sidcup to bexleyheath,
        * Meaning they are actually useful. They are in a slightly different format
        * so have to work out how to parse them precisely.
        * */

        ArrayList<Node> allNodes = FileHandler.readNodesFromFile("small_node_data_clean.csv");
        int[][] adjacencyMatrix = FileHandler.getMatrixData("small_adj_matrix.csv");
        String[] allNodeNames = FileHandler.getNodeNameArray("small_node_data_clean.csv");

        GUI graphics = new GUI(allNodes, adjacencyMatrix, allNodeNames);

    }
}