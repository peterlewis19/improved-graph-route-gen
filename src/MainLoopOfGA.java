import java.util.*;

public class MainLoopOfGA {
    private ArrayList<Integer> bestRoute;
    public MainLoopOfGA(int START_INDEX, int END_INDEX, ArrayList<Node> allNodes, int[][] adjMat){
        GeneticAlgorithm ga = new GeneticAlgorithm(allNodes, adjMat);

        int routesPerGeneration = 50;
        int bestNofGeneration = 15;
        int nOfGenerations = 20;

        ArrayList<ArrayList<Integer>> allRoutesInThisGeneration = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> allRoutesInThisGenerationWithNoOneOffLoops = new ArrayList<ArrayList<Integer>>();

        //initialises routes
        for (int i = 0; i < routesPerGeneration; i++) {
            //System.out.println("RANDOMISING A ROUTE...");
            ArrayList<Integer> randomRoute = ga.createRandomRoute3(START_INDEX, END_INDEX);
            //now perfect
            ArrayList<Integer> routeWithNoLoops = ga.removeRedundantLoops(randomRoute);

            System.out.println("Routes have been addedd");
            allRoutesInThisGeneration.add(routeWithNoLoops);
        }

        //shortest routes by distance
        allRoutesInThisGeneration.sort(Comparator.comparingDouble(ga::evaluateFitness));

        //shortest N of the generation
        ArrayList<ArrayList<Integer>> bestRoutesInGeneration = ArrayListHelp.sliceArrayListInteger(0, bestNofGeneration, allRoutesInThisGeneration);


        /*
            work out fitness score (higher is better) by getting the longest route this generation
            and taking each other score away from the longest route length, +1, such that the longest
            route has a 1 in however many chance of being chosen. and the shortest route has the
            greatest chance of being chosen
        */

        Random rand = new Random();




        //Main loop of the Genetic Algorithm
        for (int j = 0; j < nOfGenerations; j++) {
            //take best N
            // combine them into say 100 routes, take N best and repeat until small

            ArrayList<ArrayList<Integer>> currentGenerationOfRoutes = new ArrayList<ArrayList<Integer>>();
            //bestRoutesInGeneration = new ArrayList<ArrayList<Integer>>();

            //crosses over every single possible route between all routes in array
            for (int route1 = 0; route1 < bestNofGeneration; route1++) {
                for (int route2 = 0; route2 < bestNofGeneration; route2++) {
                    ArrayList<Integer> route = ga.crossOver3(bestRoutesInGeneration.get(route1), bestRoutesInGeneration.get(route2));

                    //mutate maybe 3% of routes
                    if (rand.nextInt(100) <= 4) {
                        route = ga.mutate2(route);
                    }

                    route = ga.removeRedundantMoves(route);
                    currentGenerationOfRoutes.add(route);
                }
            }
            Collections.sort(currentGenerationOfRoutes, Comparator.comparingDouble(ga::evaluateFitness));


            int longestRouteLengthOfThisGen = (int)(ga.evaluateFitness(allRoutesInThisGeneration.getLast()));

            //fitness scores placed into here, for roulette selection to occur
            int[] fitnessScoresOfGen = new int[allRoutesInThisGeneration.size()];

            // a route has a fitness score of at least 1 if they have the longest route, and a score of
            // LONGEST - SHORTEST for the shortest route, which should have the greatest value
            for (int i=0; i < allRoutesInThisGeneration.size(); i++){
                fitnessScoresOfGen[i] = longestRouteLengthOfThisGen + 1 - (int)ga.evaluateFitness(allRoutesInThisGeneration.get(i));
            }

            int[] cumulativeFitnessScoresOfGen = new int[allRoutesInThisGeneration.size()];

            //adds the score based on the previous value, making it cumulative
            cumulativeFitnessScoresOfGen[0] = fitnessScoresOfGen[0];

            for (int i=1; i < allRoutesInThisGeneration.size(); i++) {
                cumulativeFitnessScoresOfGen[i] = cumulativeFitnessScoresOfGen[i - 1] + fitnessScoresOfGen[i];
            }


            for (int i=0; i < bestNofGeneration; i++){
                int rouletteBall = rand.nextInt(cumulativeFitnessScoresOfGen[allRoutesInThisGeneration.size()-1]);

                //binary search to find where the roulette ball can be inserted
                int index = binarySearchForRoulette(rouletteBall, cumulativeFitnessScoresOfGen, 0, cumulativeFitnessScoresOfGen.length-1);
                bestRoutesInGeneration.add(allRoutesInThisGeneration.get(index));
            }

            //bestRoutesInGeneration = new ArrayList<ArrayList<Integer>>();
        }
        bestRoute = bestRoutesInGeneration.get(0);
    }


    public ArrayList<Integer> getBestRoute(){
        return bestRoute;
    }

    private int binarySearchForRoulette(int rouletteBall, int[] rouletteWheel, int lowerBound, int upperBound){
        int mid = (int)((lowerBound+upperBound)/2);
        if (upperBound > lowerBound) {
            mid = (int) (lowerBound + (upperBound - lowerBound) / 2);

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
