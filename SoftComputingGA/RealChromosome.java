public class RealChromosome extends Chromosome {
    private double[] genes;
    private double minValue, maxValue;
    
    public RealChromosome(int length, double minValue, double maxValue) {
        super();
        this.genes = new double[length];
        this.minValue = minValue;
        this.maxValue = maxValue;
        for (int i = 0; i < length; i++) {
            genes[i] = minValue + Math.random() * (maxValue - minValue);
        }
    }
    
    public RealChromosome(double[] genes, double minValue, double maxValue) {
        super();
        this.genes = genes.clone();
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public Chromosome copy() {
        return new RealChromosome(genes, minValue, maxValue);
    }
    
    public int getLength() {
        return genes.length;
    }
    
    public void mutate(double mutationRate) {
        for (int i = 0; i < genes.length; i++) {
            if (Math.random() < mutationRate) {
                // Gaussian perturbation
                double perturbation = (Math.random() - 0.5) * 0.1 * (maxValue - minValue);
                genes[i] = Math.max(minValue, Math.min(maxValue, genes[i] + perturbation));
            }
        }
    }
    
    public Chromosome[] crossover(Chromosome other, double crossoverRate) {
        RealChromosome otherReal = (RealChromosome) other;
        RealChromosome[] kids = {new RealChromosome(genes.length, minValue, maxValue), 
                                 new RealChromosome(genes.length, minValue, maxValue)};
        
        if (Math.random() < crossoverRate) {
            int point = (int)(Math.random() * genes.length);
            for (int i = 0; i < point; i++) {
                kids[0].genes[i] = this.genes[i];
                kids[1].genes[i] = otherReal.genes[i];
            }
            for (int i = point; i < genes.length; i++) {
                kids[0].genes[i] = otherReal.genes[i];
                kids[1].genes[i] = this.genes[i];
            }
        } else {
            kids[0].genes = this.genes.clone();
            kids[1].genes = otherReal.genes.clone();
        }
        return kids;
    }
    
    public double getGene(int index) {
        return genes[index];
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genes.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(String.format("%.2f", genes[i]));
        }
        return "[" + sb.toString() + "]";
    }
}


