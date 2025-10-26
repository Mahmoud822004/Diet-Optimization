public class DietCaseStudy {
    public static void main(String[] args) {
        // Define the fitness function
        FitnessFunction fitnessFunction = new FitnessFunction() {
            @Override
            public double evaluate(Chromosome individual) {
                // Diet problem: minimize cost, need >= 2000 calories, >= 50 protein
                double[][] foods = {{2.5, 165, 31}, {0.5, 130, 2.7}, {0.8, 103, 8}, {1.2, 155, 13}, {0.7, 55, 5}};
                
                double cost = 0, calories = 0, protein = 0;
                for (int i = 0; i < 5; i++) {
                    cost += individual.genes[i] * foods[i][0];
                    calories += individual.genes[i] * foods[i][1];
                    protein += individual.genes[i] * foods[i][2];
                }
                
                if (calories >= 2000 && protein >= 50) {
                    return 1000 - cost; // Higher fitness for lower cost
                } else {
                    return 1000 - cost - 1000; // Penalty for infeasible
                }
            }
        };
        
        // Define infeasibility handler
        InfeasibilityHandler infeasibilityHandler = new InfeasibilityHandler() {
            @Override
            public Chromosome handle(Chromosome individual) {
                // Add cheapest food (Rice) until constraints met
                double[][] foods = {{2.5, 165, 31}, {0.5, 130, 2.7}, {0.8, 103, 8}, {1.2, 155, 13}, {0.7, 55, 5}};
                int[] genes = individual.genes.clone();
                
                double calories = 0, protein = 0;
                for (int i = 0; i < 5; i++) {
                    calories += genes[i] * foods[i][1];
                    protein += genes[i] * foods[i][2];
                }
                
                while ((calories < 2000 || protein < 50) && genes[1] < 10) {
                    genes[1]++;
                    calories += foods[1][1];
                    protein += foods[1][2];
                }
                
                return new IntegerChromosome(genes, 0, 10);
            }
        };
        
        // Configure and run the GA
        GeneticAlgorithm ga_engine = new GeneticAlgorithm();
        ga_engine.setPopulationSize(50);
        ga_engine.setChromosomeLength(5);
        ga_engine.setChromosomeType(GeneticAlgorithm.ChromosomeType.INTEGER);
        ga_engine.setFitnessFunction(fitnessFunction);
        ga_engine.setInfeasibilityHandler(infeasibilityHandler);
        
        ga_engine.setCrossoverRate(0.8);
        ga_engine.setMutationRate(0.1);
        ga_engine.setGenerations(30);
        ga_engine.setSelectionMethod(GeneticAlgorithm.SelectionMethod.ROULETTE_WHEEL);
        ga_engine.setCrossoverMethod(GeneticAlgorithm.CrossoverMethod.ONE_POINT);
        ga_engine.setReplacementStrategy(GeneticAlgorithm.ReplacementStrategy.ELITISM);
        
        ga_engine.run();
        
        // Get the best solution
        Chromosome best = ga_engine.getBestIndividual();
        System.out.println("\n=== Results ===");
        System.out.println("Best Fitness: " + String.format("%.2f", ga_engine.getBestFitness()));
        System.out.println("Best Solution: " + java.util.Arrays.toString(best.genes));
        
        // Show detailed results
        String[] foods = {"Chicken", "Rice", "Milk", "Eggs", "Broccoli"};
        double[] costs = {2.5, 0.5, 0.8, 1.2, 0.7};
        double[][] nutrition = {{165, 31}, {130, 2.7}, {103, 8}, {155, 13}, {55, 5}};
        
        double totalCost = 0, totalCalories = 0, totalProtein = 0;
        for (int i = 0; i < 5; i++) {
            System.out.println(foods[i] + ": " + best.genes[i] + " servings");
            totalCost += best.genes[i] * costs[i];
            totalCalories += best.genes[i] * nutrition[i][0];
            totalProtein += best.genes[i] * nutrition[i][1];
        }
        System.out.println("Total Cost: $" + String.format("%.2f", totalCost));
        System.out.println("Total Calories: " + String.format("%.1f", totalCalories));
        System.out.println("Total Protein: " + String.format("%.1f", totalProtein) + "g");
    }
}
