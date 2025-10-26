public abstract class Chromosome {
    public int[] genes;
    public double fitness;
    
    public abstract Chromosome copy();
    public abstract int getLength();
    public abstract void mutate(double mutationRate);
    public abstract Chromosome[] crossover(Chromosome other, double crossoverRate);
    
    public String toString() {
        return java.util.Arrays.toString(genes);
    }
}
