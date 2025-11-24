import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    private final int[][] adjMat;
    private final ArrayList<Node> allNodes;

    public GeneticAlgorithm(ArrayList<Node> allNodes, int[][] adjMat){
        this.adjMat = adjMat;
        this.allNodes = allNodes;
    }

    public double evaluateFitness(ArrayList<Integer> route){
        double totalDistance = 0;

        for (int i=0; i< route.size()-1; i++){
            totalDistance = totalDistance + allNodes.get(route.get(i)).distanceTo(allNodes.get(route.get(i+1)));


            //CANT FIX BUG OF LEAVING PATHS, so heavily penalise it
            if (adjMat[route.get(i)][route.get(i+1)]!= 1){
                totalDistance = totalDistance + 10000000;
            }
        }

        return totalDistance;
    }

    public ArrayList<Integer> crossOver3(ArrayList<Integer> route1, ArrayList<Integer> route2) {
        ArrayList<Integer> finalRoute = new ArrayList<>();
        double shortestDistance = Double.MAX_VALUE;
        int currentClosestRoute1 = 0;
        int currentClosestRoute2 = 0;

        //find closest nodes between routes apart from endpoints
        for (int i = 2; i < route1.size() ; i++) {
            for (int j = 2; j < route2.size() ; j++) {
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

        //slice segments with no overlap
        List<Integer> finalRoutePart1 = route1.subList(0, currentClosestRoute1); // Excludes connection point
        List<Integer> finalRoutePart2 = route2.subList(currentClosestRoute2 + 1, route2.size()); // Excludes connection point

        // Get node indices for connection points
        int nodeA = route1.get(currentClosestRoute1);
        int nodeB = route2.get(currentClosestRoute2);

        List<Integer> path = createRandomRoute4(nodeA, nodeB);

        // Combine segments
        finalRoute.addAll(finalRoutePart1);
        finalRoute.addAll(path);
        finalRoute.addAll(finalRoutePart2);

        return finalRoute;
    }

    public ArrayList<Integer> mutate2(ArrayList<Integer> route){
        ArrayList<Integer> finalRoute = new ArrayList<Integer>();

        //turn an index of route into an index of allNodes

        Random rand = new Random();
        //pick 2 indexes, draw a path between them
        int a = rand.nextInt(0,route.size());
        int b = rand.nextInt(0,route.size());

        //find value of route[index], look for that value in allNodes, get that index#
        int valueOfRouteGetA = route.get(a); //this is index of value in unconnected graph
        int valueOfRouteGetB = route.get(b);

        finalRoute.addAll(ArrayListHelp.sliceArrayList(0, a,route));

        ArrayList<Integer> mutatedInbetweenRoute = createRandomRoute4(valueOfRouteGetA,valueOfRouteGetB);
        finalRoute.addAll(mutatedInbetweenRoute);
        finalRoute.addAll(ArrayListHelp.sliceArrayList(b+1,route.size(), route));

        return finalRoute;
    }

    public boolean contains(ArrayList<Integer> route, Integer target){
        boolean found = false;
        int count = 0;

        while (count < route.size() && !found){
            if (route.get(count).equals(target)){
                found = true;
            }

            count++;
        }

        return found;
    }

    public int getIndex(ArrayList<Integer> route, Integer target){
        boolean found = false;
        int count = 0;

        while (count < route.size() && !found){
            if (route.get(count).equals(target)){
                found = true;
            } else {

                count++;
            }
        }

        return count;
    }

    public ArrayList<Integer> removeRedundantMoves(ArrayList<Integer> route){
        // go through route, find the number of times that a route goes through a point
        // if it goes through a node twice, cut it in between those
        // finds the biggest redundant part and cuts it.
        ArrayList<Integer> nodesVisited = new ArrayList<>();
        int startIndex =0;
        int endIndex=0;

        int finalStartIndex = 0;
        int finalEndIndex = 0;

        for (int i=0; i < route.size(); i++){
            if (contains(nodesVisited, route.get(i))){
                startIndex = getIndex(nodesVisited, route.get(i));
                endIndex = i;

                if (endIndex - startIndex >= finalEndIndex - finalStartIndex){
                    finalStartIndex = startIndex;
                    finalEndIndex = endIndex;
                }
            } else{
                nodesVisited.add(route.get(i));
            }
        }

        ArrayList<Integer> routeBeforeCutOff = ArrayListHelp.sliceArrayList(0, finalStartIndex, route);// route.subList(0, finalStartIndex+1);
        ArrayList<Integer> routeAfterCutOff = ArrayListHelp.sliceArrayList(finalEndIndex, route.size(), route);//.subList(finalEndIndex+1, route.size());

        ArrayList<Integer> finalRoute = new ArrayList<>();
        finalRoute.addAll(routeBeforeCutOff);
        finalRoute.addAll(routeAfterCutOff);

        return finalRoute;
    }

    //cuts a route when a node occurs twice
    public ArrayList<Integer> removeRedundantLoops(ArrayList<Integer> route) {
        boolean loopFound = true;
        ArrayList<Integer> currentRoute = route;

        while (loopFound) {
            ArrayList<Integer> nodesVisited = new ArrayList<>();
            int startIndex = -1;
            int endIndex = -1;

            int count = 0;
            int node = currentRoute.get(count);

            while (nodesVisited.contains(node)){
                if (nodesVisited.contains(node)) {
                    startIndex = nodesVisited.indexOf(node);
                    endIndex = count;
                                    }
                nodesVisited.add(node);
            }

            //indexes have been found
            if (startIndex != -1 && endIndex != -1) {
                //keep everything before startIndex+1 and after endIndex
                ArrayList<Integer> newRoute = new ArrayList<>();
                newRoute.addAll(currentRoute.subList(0, startIndex+1));
                newRoute.addAll(currentRoute.subList(endIndex+1, currentRoute.size()));
                currentRoute = newRoute;
            } else {
                loopFound = false; // No more loops
            }
        }
        return currentRoute;
    }

    //randomly walks until it reaches the destination, not visiting any node twice unless it has to
    /*public ArrayList<Integer> createRandomRoute3(int nodeA, int nodeB) {
        ArrayList<Integer> routeWalked = new ArrayList<>();
        HashSet<Integer> visitedNodes = new HashSet<>();
        Random rand = new Random();

        int currentNode = nodeA;
        routeWalked.add(currentNode);
        visitedNodes.add(currentNode);

        int maxSteps = adjMat.length * adjMat.length; // safety cutoff
        int steps = 0;

        while (currentNode != nodeB && steps < maxSteps) {
            ArrayList<Integer> neighbours = new ArrayList<>();

            // collect unvisited neighbors
            for (int j = 0; j < adjMat.length; j++) {
                if (adjMat[currentNode][j] == 1 && !visitedNodes.contains(j)) {
                    neighbours.add(j);
                }
            }

            // if no unvisited neighbors allow revisiting
            if (neighbours.isEmpty()) {
                for (int j = 0; j < adjMat.length; j++) {
                    if (adjMat[currentNode][j] == 1 && j != currentNode) {
                        neighbours.add(j);
                    }
                }
            }

            // pick a random neighbor, regardless of visited or not
            int nextNode = neighbours.get(rand.nextInt(neighbours.size()));

            routeWalked.add(nextNode);
            visitedNodes.add(nextNode); // we still mark it visited even if backtracking
            currentNode = nextNode;

            steps++;
        }

        if (currentNode != nodeB) {
            System.out.println("Random walk stopped without reaching " + nodeB);
        }

        return routeWalked;
    }*/

    public ArrayList<Integer> createRandomRoute4(int nodeA, int nodeB) {
        ArrayList<Integer> route = new ArrayList<>();
        HashSet<Integer> visited = new HashSet<>();
        Random rand = new Random();

        int current = nodeA;
        route.add(current);
        visited.add(current);

        // safety cap (much lower than n*n)
        int maxSteps = adjMat.length * 4;

        int steps = 0;
        while (current != nodeB && steps < maxSteps) {

            // --- Collect neighbors ---
            ArrayList<Integer> neighbours = new ArrayList<>(8);
            int n = adjMat.length;

            for (int j = 0; j < n; j++) {
                if (adjMat[current][j] == 1 && j != current) {
                    neighbours.add(j);
                }
            }

            if (neighbours.isEmpty()) break;

            // --- A*-inspired scoring ---
            double bestScore = Double.POSITIVE_INFINITY;
            int bestNext = neighbours.get(0);

            for (int neigh : neighbours) {

                double h = allNodes.get(neigh).distanceTo(allNodes.get(nodeB)); // heuristic
                double revisitPenalty = visited.contains(neigh) ? 10000 : 0;
                double randomness = rand.nextDouble() * 10.0;  // exploration

                double score = h + revisitPenalty + randomness;

                if (score < bestScore) {
                    bestScore = score;
                    bestNext = neigh;
                }
            }

            // --- Move to selected node ---
            current = bestNext;
            route.add(current);
            visited.add(current);

            steps++;
        }

        return route;
    }




}
