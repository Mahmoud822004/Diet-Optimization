public class IntegerChromosome extends Chromosome {
    private int minValue, maxValue;
    
    public IntegerChromosome(int length, int minValue, int maxValue) {
        this.genes = new int[length];
        this.minValue = minValue;
        this.maxValue = maxValue;
        for (int i = 0; i < length; i++) {
            genes[i] = minValue + (int)(Math.random() * (maxValue - minValue + 1));
        }
    }
    
    public IntegerChromosome(int[] genes, int minValue, int maxValue) {
        this.genes = genes.clone();
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public Chromosome copy() {
        return new IntegerChromosome(genes, minValue, maxValue);
    }
    
    public int getLength() {
        return genes.length;
    }
    
    public void mutate(double mutationRate) {
        for (int i = 0; i < genes.length; i++) {
            if (Math.random() < mutationRate) {
                genes[i] = minValue + (int)(Math.random() * (maxValue - minValue + 1));
            }
        }
    }
    
    public Chromosome[] crossover(Chromosome other, double crossoverRate) {
        IntegerChromosome otherInt = (IntegerChromosome) other;
        IntegerChromosome[] kids = {new IntegerChromosome(genes.length, minValue, maxValue), 
                                    new IntegerChromosome(genes.length, minValue, maxValue)};
        
        if (Math.random() < crossoverRate) {
            int point = (int)(Math.random() * genes.length);
            for (int i = 0; i < point; i++) {
                kids[0].genes[i] = this.genes[i];
                kids[1].genes[i] = otherInt.genes[i];
            }
            for (int i = point; i < genes.length; i++) {
                kids[0].genes[i] = otherInt.genes[i];
                kids[1].genes[i] = this.genes[i];
            }
        } else {
            kids[0].genes = this.genes.clone();
            kids[1].genes = otherInt.genes.clone();
        }
        return kids;
    }
}


