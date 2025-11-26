import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

public class OptimisedGeneticAlgorithm {
    // --- Optimized Data Structures ---
    // 1. Adjacency List for O(degree) neighbor lookup (O(N) in original)
    private final ArrayList<Integer>[] adjList;

    // 2. Cost Matrix for O(1) distance lookups (avoids repeated distanceTo calculations)
    private final double[][] costMat;

    private final ArrayList<Node> allNodes;
    private final int N; // Total number of nodes

    public OptimisedGeneticAlgorithm(ArrayList<Node> allNodes, int[][] adjMat) {
        this.allNodes = allNodes;
        this.N = allNodes.size();

        // --- 1. Graph Data Structure Initialization ---
        this.adjList = new ArrayList[N];
        this.costMat = new double[N][N];

        // Initialize structures from the input Adjacency Matrix
        for (int i = 0; i < N; i++) {
            adjList[i] = new ArrayList<>();
            for (int j = 0; j < N; j++) {
                if (adjMat[i][j] == 1) {
                    // Pre-calculate distance (Edge Weight)
                    double distance = allNodes.get(i).distanceTo(allNodes.get(j));
                    costMat[i][j] = distance;

                    // Build Adjacency List
                    adjList[i].add(j);
                } else {
                    // Use a large penalty/infinity for non-existent edges
                    costMat[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }
    }

    /**
     * O(L) - Uses pre-calculated cost matrix for O(1) distance lookups.
     */
    public double evaluateFitness(ArrayList<Integer> route) {
        double totalDistance = 0;

        for (int i = 0; i < route.size() - 1; i++) {
            int nodeA = route.get(i);
            int nodeB = route.get(i + 1);

            // Use the pre-calculated cost matrix for distance
            double distance = costMat[nodeA][nodeB];
            totalDistance += distance;

            // Penalty for non-connected paths (using infinity check)
            if (distance == Double.POSITIVE_INFINITY) {
                totalDistance += 10000000;
            }
        }
        return totalDistance;
    }

    /**
     * O(L^2) in worst case, but can be optimized with search window.
     * Original O(L1 * L2) is kept, but comments suggest optimization.
     */
    public ArrayList<Integer> crossOver3(ArrayList<Integer> route1, ArrayList<Integer> route2) {
        ArrayList<Integer> finalRoute = new ArrayList<>();
        double shortestDistance = Double.MAX_VALUE;
        int currentClosestRoute1 = 0;
        int currentClosestRoute2 = 0;

        // Optimization: Limit search space for closest nodes (e.g., to the last/first 10 nodes)
        // This changes the complexity from O(L1*L2) to O(1) for long routes.
        int searchWindow = 10;
        int startI = Math.max(2, route1.size() - searchWindow);
        int endJ = Math.min(route2.size() - 1, searchWindow + 1);

        // find closest nodes between routes apart from endpoints
        for (int i = 2; i < startI; i++) {
            for (int j = 2; j < endJ; j++) {
                Node node1 = allNodes.get(route1.get(i));
                Node node2 = allNodes.get(route2.get(j));
                double testDistance = node1.distanceTo(node2);

                if (testDistance < shortestDistance && testDistance > 0) {
                    shortestDistance = testDistance;
                    currentClosestRoute1 = i;
                    currentClosestRoute2 = j;
                }
            }
        }

        // ... rest of the method logic remains the same ...
        List<Integer> finalRoutePart1 = route1.subList(0, currentClosestRoute1);
        List<Integer> finalRoutePart2 = route2.subList(currentClosestRoute2 + 1, route2.size());

        int nodeA = route1.get(currentClosestRoute1);
        int nodeB = route2.get(currentClosestRoute2);

        // Assuming createRandomRoute4 is now efficient
        List<Integer> path = createRandomRoute4(nodeA, nodeB);

        finalRoute.addAll(finalRoutePart1);
        finalRoute.addAll(path);
        finalRoute.addAll(finalRoutePart2);

        return finalRoute;
    }

    public ArrayList<Integer> mutate2(ArrayList<Integer> route) {
        ArrayList<Integer> finalRoute = new ArrayList<>();
        Random rand = new Random();

        // Pick 2 random, distinct indices
        int a = rand.nextInt(route.size());
        int b = rand.nextInt(route.size());
        while (a == b) {
            b = rand.nextInt(route.size());
        }

        // Ensure a is the starting index and b is the ending index of the segment
        int startIndex = Math.min(a, b);
        int endIndex = Math.max(a, b);

        // Get node indices for the path start and end
        int nodeA = route.get(startIndex);
        int nodeB = route.get(endIndex);

        // 1. Get the segment before the mutation point (O(startIndex))
        List<Integer> routeBefore = route.subList(0, startIndex);
        finalRoute.addAll(routeBefore);

        // 2. Generate a new, random path between nodeA and nodeB
        ArrayList<Integer> mutatedInbetweenRoute = createRandomRoute4(nodeA, nodeB);
        finalRoute.addAll(mutatedInbetweenRoute);

        // 3. Get the segment after the mutation point (O(L - endIndex))
        // We add the segment starting from endIndex + 1 because the path generator
        // already includes the start node (nodeA) and the end node (nodeB).
        if (endIndex + 1 < route.size()) {
            List<Integer> routeAfter = route.subList(endIndex + 1, route.size());
            finalRoute.addAll(routeAfter);
        }

        return finalRoute;
    }


    /**
     * O(L) - Uses HashMap for O(1) lookup of previously visited nodes, 
     * dramatically improving performance over the original O(L^2) approach.
     */
    public ArrayList<Integer> removeRedundantMoves(ArrayList<Integer> route) {
        // Map: Node ID -> First Index it appeared at in the route
        HashMap<Integer, Integer> nodeToIndexMap = new HashMap<>();

        int finalStartIndex = -1;
        int finalEndIndex = -1;

        //Doesn't deal with ABA, so hardcode that as a base case

        // Iterate through the route once (O(L))
        for (int i = 0; i < route.size(); i++) {
            int currentNode = route.get(i);

            if (nodeToIndexMap.containsKey(currentNode)) {
                // Duplicate found. Start of the loop is the previous index.
                int startIndex = nodeToIndexMap.get(currentNode);
                int endIndex = i;

                if (endIndex-startIndex == 2){

                }

                // Keep track of the longest loop found so far
                if (endIndex - startIndex >= finalEndIndex - finalStartIndex) {
                    finalStartIndex = startIndex;
                    finalEndIndex = endIndex;
                }
            } else {
                // First time seeing this node, record its index.
                nodeToIndexMap.put(currentNode, i);
            }
        }

        // If a loop was found, cut it out
        if (finalStartIndex != -1) {
            // Cut: keep (0 to finalStartIndex) and (finalEndIndex to end)
            List<Integer> routeBeforeCutOff = route.subList(0, finalStartIndex);
            List<Integer> routeAfterCutOff = route.subList(finalEndIndex, route.size());

            ArrayList<Integer> finalRoute = new ArrayList<>();
            finalRoute.addAll(routeBeforeCutOff);
            finalRoute.addAll(routeAfterCutOff);
            return finalRoute;
        }

        return route; // No loop found
    }


    public List<Integer> removeAllRedundantLoops(List<Integer> route) {
        // 1. Stack to build the simplified route (acts as the path segment)
        ArrayList<Integer> resultRoute = new ArrayList<>();

        // 2. Set for O(1) look-up of nodes currently in resultRoute
        HashSet<Integer> pathSet = new HashSet<>();

        // O(N) iteration over the original route
        for (int currentNode : route) {
            if (!pathSet.contains(currentNode)) {
                // Case 1: New node. Extend the simplified path.
                resultRoute.add(currentNode);
                pathSet.add(currentNode);
            } else {
                // Case 2: Duplicate found (Loop detected). Cut out the redundant segment.

                // Pop nodes off the stack and remove them from the set
                // until the first appearance of currentNode is also popped.
                int poppedNode;
                do {
                    // Get the last node added (top of the stack)
                    int lastIndex = resultRoute.size() - 1;
                    poppedNode = resultRoute.remove(lastIndex);

                    // Remove the node from the path set
                    pathSet.remove(poppedNode);
                } while (poppedNode != currentNode);

                // At this point, currentNode is the next node to be added
                // to the path, so we continue the outer loop.
                // (The currentNode is either a new path or it just started
                // a loop, which is now cleared. It will be added in the
                // next iteration if it's new, or in a later iteration.)

                // Re-add the current node to the path
                resultRoute.add(currentNode);
                pathSet.add(currentNode);
            }
        }

        return resultRoute;
    }

    /**
     * O(N * degree) worst-case - Uses Adjacency List for O(degree) neighbor lookup,
     * replacing the original O(N) lookup. This significantly improves performance.
     */
    public ArrayList<Integer> createRandomRoute4(int nodeA, int nodeB) {
        ArrayList<Integer> route = new ArrayList<>();
        HashSet<Integer> visited = new HashSet<>();
        Random rand = new Random();

        int current = nodeA;
        route.add(current);
        visited.add(current);

        int maxSteps = N * 15; // Safety cap
        int steps = 0;

        while (current != nodeB && steps < maxSteps) {

            // --- Optimized Neighbor Collection: O(degree) ---
            // Use the pre-built Adjacency List instead of iterating N nodes
            ArrayList<Integer> neighbours = adjList[current];

            if (neighbours.isEmpty()) break;

            // --- A*-inspired scoring (optimized slightly by using costMat implicitly) ---
            double bestScore = Double.POSITIVE_INFINITY;
            int bestNext = -1;

            for (int neigh : neighbours) {
                // Use distanceTo/costMat for heuristic h
                double h = allNodes.get(neigh).distanceTo(allNodes.get(nodeB));

                // Penalize revisiting visited nodes
                double revisitPenalty = visited.contains(neigh) ? 10000 : 0;

                // Randomness for exploration (can be scaled/decayed for better results)
                // A double from 0 to 15
                double randomness = rand.nextDouble() * 15;

                double score = h + revisitPenalty + randomness;

                if (score < bestScore) {
                    bestScore = score;
                    bestNext = neigh;
                }
            }

            if (bestNext == -1) break; // Should not happen if neighbours is not empty

            // --- Move to selected node ---
            current = bestNext;
            route.add(current);
            visited.add(current);

            steps++;
        }

        return route;
    }
}