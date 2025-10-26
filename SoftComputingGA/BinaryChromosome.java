public class BinaryChromosome extends Chromosome {
    private boolean[] genes;
    
    public BinaryChromosome(int length) {
        super();
        this.genes = new boolean[length];
        for (int i = 0; i < length; i++) {
            genes[i] = Math.random() < 0.5;
        }
    }
    
    public BinaryChromosome(boolean[] genes) {
        super();
        this.genes = genes.clone();
    }
    
    public Chromosome copy() {
        return new BinaryChromosome(genes);
    }
    
    public int getLength() {
        return genes.length;
    }
    
    public void mutate(double mutationRate) {
        for (int i = 0; i < genes.length; i++) {
            if (Math.random() < mutationRate) {
                genes[i] = !genes[i]; // Bit flip
            }
        }
    }
    
    public Chromosome[] crossover(Chromosome other, double crossoverRate) {
        BinaryChromosome otherBinary = (BinaryChromosome) other;
        BinaryChromosome[] kids = {new BinaryChromosome(genes.length), new BinaryChromosome(genes.length)};
        
        if (Math.random() < crossoverRate) {
            int point = (int)(Math.random() * genes.length);
            for (int i = 0; i < point; i++) {
                kids[0].genes[i] = this.genes[i];
                kids[1].genes[i] = otherBinary.genes[i];
            }
            for (int i = point; i < genes.length; i++) {
                kids[0].genes[i] = otherBinary.genes[i];
                kids[1].genes[i] = this.genes[i];
            }
        } else {
            kids[0].genes = this.genes.clone();
            kids[1].genes = otherBinary.genes.clone();
        }
        return kids;
    }
    
    public boolean getGene(int index) {
        return genes[index];
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean gene : genes) {
            sb.append(gene ? "1" : "0");
        }
        return sb.toString();
    }
}


