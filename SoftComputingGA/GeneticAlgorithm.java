import java.util.*;

public class GeneticAlgorithm {
    private List<Chromosome> population;
    private FitnessFunction fitnessFunction;
    private InfeasibilityHandler infeasibilityHandler;
    
    // GA Parameters with defaults
    private int populationSize = 50;
    private int generations = 100;
    private double crossoverRate = 0.8;
    private double mutationRate = 0.1;
    private int chromosomeLength = 10;
    private ChromosomeType chromosomeType = ChromosomeType.INTEGER;
    
    // GA Operators
    private SelectionMethod selectionMethod = SelectionMethod.ROULETTE_WHEEL;
    private CrossoverMethod crossoverMethod = CrossoverMethod.ONE_POINT;
    private ReplacementStrategy replacementStrategy = ReplacementStrategy.GENERATIONAL;
    
    // Statistics
    private double bestFitness;
    private Chromosome bestIndividual;
    
    public void setPopulationSize(int size) { this.populationSize = size; }
    public void setGenerations(int gens) { this.generations = gens; }
    public void setCrossoverRate(double rate) { this.crossoverRate = rate; }
    public void setMutationRate(double rate) { this.mutationRate = rate; }
    public void setChromosomeLength(int length) { this.chromosomeLength = length; }
    public void setChromosomeType(ChromosomeType type) { this.chromosomeType = type; }
    public void setSelectionMethod(SelectionMethod method) { this.selectionMethod = method; }
    public void setCrossoverMethod(CrossoverMethod method) { this.crossoverMethod = method; }
    public void setReplacementStrategy(ReplacementStrategy strategy) { this.replacementStrategy = strategy; }
    public void setFitnessFunction(FitnessFunction function) { this.fitnessFunction = function; }
    public void setInfeasibilityHandler(InfeasibilityHandler handler) { this.infeasibilityHandler = handler; }
    
    public void run() {
        initializePopulation();
        
        for (int gen = 0; gen < generations; gen++) {
            evaluatePopulation();
            
            List<Chromosome> newPopulation = new ArrayList<>();
            
            // Apply replacement strategy
            if (replacementStrategy == ReplacementStrategy.ELITISM) {
                newPopulation.add(bestIndividual.copy());
            }
            
            while (newPopulation.size() < populationSize) {
                Chromosome parent1 = select();
                Chromosome parent2 = select();
                
                Chromosome[] offspring = crossover(parent1, parent2);
                
                for (Chromosome child : offspring) {
                    child.mutate(mutationRate);
                    if (infeasibilityHandler != null) {
                        child = infeasibilityHandler.handle(child);
                    }
                    newPopulation.add(child);
                }
            }
            
            if (replacementStrategy == ReplacementStrategy.STEADY_STATE) {
                steadyStateReplacement(newPopulation);
            } else {
                population = newPopulation;
            }
            
            if (gen % 10 == 0 || gen == generations - 1) {
                System.out.println("Generation " + gen + ": Best Fitness = " + String.format("%.2f", bestFitness));
            }
        }
    }
    
    private void initializePopulation() {
        population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(createChromosome());
        }
    }
    
    private Chromosome createChromosome() {
        switch (chromosomeType) {
            case BINARY:
                return new BinaryChromosome(chromosomeLength);
            case INTEGER:
                return new IntegerChromosome(chromosomeLength, 0, 10);
            case REAL:
                return new RealChromosome(chromosomeLength, 0.0, 10.0);
            default:
                return new IntegerChromosome(chromosomeLength, 0, 10);
        }
    }
    
    private void evaluatePopulation() {
        bestFitness = Double.NEGATIVE_INFINITY;
        for (Chromosome individual : population) {
            double fitness = fitnessFunction.evaluate(individual);
            individual.fitness = fitness;
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestIndividual = individual.copy();
            }
        }
    }
    
    private Chromosome select() {
        switch (selectionMethod) {
            case ROULETTE_WHEEL:
                return rouletteWheelSelection();
            case TOURNAMENT:
                return tournamentSelection();
            default:
                return rouletteWheelSelection();
        }
    }
    
    private Chromosome rouletteWheelSelection() {
        double totalFitness = population.stream().mapToDouble(c -> c.fitness).sum();
        double randomValue = Math.random() * totalFitness;
        double cumulativeFitness = 0;
        
        for (Chromosome individual : population) {
            cumulativeFitness += individual.fitness;
            if (cumulativeFitness >= randomValue) {
                return individual;
            }
        }
        return population.get(population.size() - 1);
    }
    
    private Chromosome tournamentSelection() {
        int tournamentSize = Math.max(2, populationSize / 10);
        Chromosome best = null;
        
        for (int i = 0; i < tournamentSize; i++) {
            Chromosome candidate = population.get((int)(Math.random() * populationSize));
            if (best == null || candidate.fitness > best.fitness) {
                best = candidate;
            }
        }
        return best;
    }
    
    private Chromosome[] crossover(Chromosome parent1, Chromosome parent2) {
        switch (crossoverMethod) {
            case ONE_POINT:
                return onePointCrossover(parent1, parent2);
            case TWO_POINT:
                return twoPointCrossover(parent1, parent2);
            case UNIFORM:
                return uniformCrossover(parent1, parent2);
            default:
                return onePointCrossover(parent1, parent2);
        }
    }
    
    private Chromosome[] onePointCrossover(Chromosome parent1, Chromosome parent2) {
        if (Math.random() > crossoverRate) {
            return new Chromosome[]{parent1.copy(), parent2.copy()};
        }
        int point = (int)(Math.random() * parent1.getLength());
        return parent1.crossover(parent2, crossoverRate);
    }
    
    private Chromosome[] twoPointCrossover(Chromosome parent1, Chromosome parent2) {
        if (Math.random() > crossoverRate) {
            return new Chromosome[]{parent1.copy(), parent2.copy()};
        }
        int point1 = (int)(Math.random() * parent1.getLength());
        int point2 = (int)(Math.random() * parent1.getLength());
        if (point1 > point2) {
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }
        return parent1.crossover(parent2, crossoverRate);
    }
    
    private Chromosome[] uniformCrossover(Chromosome parent1, Chromosome parent2) {
        if (Math.random() > crossoverRate) {
            return new Chromosome[]{parent1.copy(), parent2.copy()};
        }
        return parent1.crossover(parent2, crossoverRate);
    }
    
    private void steadyStateReplacement(List<Chromosome> newPopulation) {
        population.sort((a, b) -> Double.compare(a.fitness, b.fitness));
        int replaceCount = Math.min(newPopulation.size(), populationSize / 2);
        
        for (int i = 0; i < replaceCount; i++) {
            population.set(i, newPopulation.get(i));
        }
    }
    
    public Chromosome getBestIndividual() {
        return bestIndividual;
    }
    
    public double getBestFitness() {
        return bestFitness;
    }
    
    // Enums for GA operators
    public enum ChromosomeType { BINARY, INTEGER, REAL }
    public enum SelectionMethod { ROULETTE_WHEEL, TOURNAMENT }
    public enum CrossoverMethod { ONE_POINT, TWO_POINT, UNIFORM }
    public enum ReplacementStrategy { GENERATIONAL, ELITISM, STEADY_STATE }
}


