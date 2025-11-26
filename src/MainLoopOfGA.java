import java.util.*;

public class MainLoopOfGA {
    private ArrayList<Integer> shortestRouteOverall;

    public MainLoopOfGA(int START_INDEX, int END_INDEX, ArrayList<Node> allNodes, int[][] adjMat) {
        OptimisedGeneticAlgorithm ga = new OptimisedGeneticAlgorithm(allNodes, adjMat);

        int routesPerGeneration = 250;
        int bestNofGeneration = 35;
        int nOfGenerations = 100;

        ArrayList<ArrayList<Integer>> allRoutesInThisGeneration = new ArrayList<>();

        //initialises routes, without any loops
        for (int i = 0; i < routesPerGeneration; i++) {
            ArrayList<Integer> randomRoute = ga.createRandomRoute4(START_INDEX, END_INDEX);
            ArrayList<Integer> routeWithNoLoops = (ArrayList<Integer>) ga.removeAllRedundantLoops(randomRoute);

            //add only valid routes
            if (routeWithNoLoops.getLast() == END_INDEX){
                allRoutesInThisGeneration.add(routeWithNoLoops);
            }
        }

        //shortest routes by distance
        allRoutesInThisGeneration.sort(Comparator.comparingDouble(ga::evaluateFitness));

        //shortest N of the generation, for initial generation
        ArrayList<ArrayList<Integer>> bestRoutesInGeneration = ArrayListHelp.sliceArrayListInteger(0, bestNofGeneration, allRoutesInThisGeneration);


        /*
            work out fitness score (higher is better) by getting the longest route this generation
            and taking each other score away from the longest route length, +1, such that the longest
            route has a 1 in however many chance of being chosen. and the shortest route has the
            greatest chance of being chosen
        */

        Random rand = new Random();

        //initialise this value
        shortestRouteOverall = allRoutesInThisGeneration.getFirst();
        ArrayList<Integer> bestRouteOfGen = new ArrayList<>();

        //Main loop of the Genetic Algorithm
        for (int j = 0; j < nOfGenerations; j++) {
            //take best N
            // combine them into say 100 routes, take N best and repeat until small
            allRoutesInThisGeneration = new ArrayList<>();

            //crosses over every single possible route between all routes in array
            for (int route1 = 0; route1 < bestNofGeneration; route1++) {
                for (int route2 = 0; route2 < bestNofGeneration; route2++) {
                    ArrayList<Integer> route = ga.crossOver3(bestRoutesInGeneration.get(route1), bestRoutesInGeneration.get(route2));

                    //mutate maybe 3% of routes
                    if (rand.nextInt(100) <= 4) {
                        route = ga.mutate2(route);
                    }

                    route = ga.removeAllRedundantLoops(route);

                    //add only valid routes
                    if (route.getLast() == END_INDEX){
                        allRoutesInThisGeneration.add(route);
                    }
                }
            }

            double longestRouteLengthOfThisGen = 0;

            for (int i = 0; i < allRoutesInThisGeneration.size(); i++) {
                if (ga.evaluateFitness(allRoutesInThisGeneration.get(i)) > longestRouteLengthOfThisGen) {
                    longestRouteLengthOfThisGen = ga.evaluateFitness(allRoutesInThisGeneration.get(i));
                }
            }

            longestRouteLengthOfThisGen = longestRouteLengthOfThisGen * 10000;

            //System.out.println("LONGEST ROUTE IS: " + longestRouteLengthOfThisGen);
            //THIS IS 0- ERROR FOUND

            //fitness scores placed into here, for roulette selection to occur
            double[] fitnessScoresOfGen = new double[allRoutesInThisGeneration.size()];

            // a route has a fitness score of at least 1 if they have the longest route, and a score of
            // LONGEST - SHORTEST for the shortest route, which should have the greatest value
            for (int i = 0; i < allRoutesInThisGeneration.size(); i++) {
                fitnessScoresOfGen[i] = longestRouteLengthOfThisGen + 1 - (ga.evaluateFitness(allRoutesInThisGeneration.get(i)) * 10000);
            }

            /*System.out.println(fitnessScoresOfGen[6]);
            System.out.println(fitnessScoresOfGen[15]);
            System.out.println(fitnessScoresOfGen[300]);
            //FITNESS SCORES ARE ALL 1*/

            double[] cumulativeFitnessScoresOfGen = new double[allRoutesInThisGeneration.size()];

            //adds the score based on the previous value, making it cumulative
            cumulativeFitnessScoresOfGen[0] = fitnessScoresOfGen[0];

            for (int i = 1; i < allRoutesInThisGeneration.size(); i++) {
                cumulativeFitnessScoresOfGen[i] = cumulativeFitnessScoresOfGen[i - 1] + fitnessScoresOfGen[i];
            }

            for (int i = 0; i < bestNofGeneration; i++) {
                //from 0 to largest value of roulette wheel
                int rouletteBall = rand.nextInt((int) cumulativeFitnessScoresOfGen[allRoutesInThisGeneration.size() - 1]);

                //binary search to find where the roulette ball can be inserted
                int index = binarySearchForRoulette(rouletteBall, cumulativeFitnessScoresOfGen, 0, cumulativeFitnessScoresOfGen.length - 1);
                bestRoutesInGeneration.add(allRoutesInThisGeneration.get(index));
            }

            //bestRoutesInGeneration = new ArrayList<ArrayList<Integer>>();
        }
        bestRouteOfGen = bestRoutesInGeneration.getFirst();

        if (ga.evaluateFitness(bestRouteOfGen) < ga.evaluateFitness(shortestRouteOverall)) {
            shortestRouteOverall = bestRouteOfGen;
        }
    }


    public ArrayList<Integer> getBestRoute(){
        return shortestRouteOverall;
    }

    private int binarySearchForRoulette(int rouletteBall, double[] rouletteWheel, int lowerBound, int upperBound){
        int mid = ((lowerBound+upperBound)/2);
        if (upperBound > lowerBound) {
            mid = (lowerBound + (upperBound - lowerBound) / 2);

            // If the element is present at the
            // middle itself
            if (rouletteWheel[mid] == rouletteBall)
                return mid;

            // If element is smaller than mid, then
            // it can only be present in left subarray
            if (rouletteWheel[mid] > rouletteBall)
                return binarySearchForRoulette(rouletteBall, rouletteWheel, lowerBound, mid - 1);

            // Else the element can only be present
            // in right subarray
            return binarySearchForRoulette(rouletteBall, rouletteWheel, mid + 1, upperBound);
        } else{
            return mid;
        }
    }
}
