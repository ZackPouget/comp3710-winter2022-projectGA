import java.util.ArrayList;
import java.util.Comparator;

public class GeneticAlgorithm {

    public final static int CHOICE_COUNT = 3;
    public final static int POPULATION_SIZE = 100;
    public final static int GENERATION_ITERATIONS = 100;
    public final static int GENERATIONS = 10000;
    public final static int CHROMOSOME_SIZE = (int) Math.pow(2, CHOICE_COUNT * 2);
    public final static double MUTATION_CHANCE = 0.001;

    public static int score(boolean isPlayerOne, boolean playerOneCooperates, boolean playerTwoCooperates) {
        if (playerOneCooperates == playerTwoCooperates) {
            return playerOneCooperates ? 2 : 1;
        } else {
            return isPlayerOne == playerTwoCooperates ? 3 : 0;
        }
    }

    public class Individual implements Comparable<Individual> {
        public boolean[] chromosome = new boolean[CHROMOSOME_SIZE];
        public int score = 0;

        public Individual() {
            for (int i = 0; i < chromosome.length; i++) {
                chromosome[i] = Math.random() > 0.5;
            }
        }

        public boolean makeChoice(boolean[] opponentChoices, boolean[] myChoices) {
            int digitValue = CHROMOSOME_SIZE / 2;
            int index = 0;
            
            for (boolean b : opponentChoices) {
                if (b) {
                    index += digitValue;
                }
                digitValue /= 2;
            }

            for (boolean b : myChoices) {
                if (b) {
                    index += digitValue;
                }
                digitValue /= 2;
            }

            return chromosome[index];
        }

        @Override
        public int compareTo(GeneticAlgorithm.Individual o) {
            return this.score < o.score ? 1 : -1;
        }

        @Override
        public String toString() {
            return "My score is " + score;
        }
        
    }

    ArrayList<Individual> population = new ArrayList<Individual>();

    public GeneticAlgorithm() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(new Individual());   
        }
    }

    public Individual breed(Individual a, Individual b, int crossover) {
        Individual c = new Individual();

        for (int i = 0; i < crossover; i++) {
            c.chromosome[i] = a.chromosome[i];
        }

        for (int i = crossover; i < CHROMOSOME_SIZE; i++) {
            c.chromosome[i] = b.chromosome[i];
        }

        return c;
    }

    public void mutate(Individual individual) {
        for (int i = 0; i < individual.chromosome.length; i++) {
            if (Math.random() < MUTATION_CHANCE) {
                individual.chromosome[i] = !individual.chromosome[i];
            }
        }
    }

    public void evolveGeneration() {
        for (int i = 0; i < population.size(); i++) {
            for (int j = i + 1; j < population.size(); j++) {
                boolean[] iChoices = new boolean[CHOICE_COUNT];
                boolean[] jChoices = new boolean[CHOICE_COUNT];

                for (int k = 0; k < GENERATION_ITERATIONS; k++) {
                    boolean iChoice = population.get(i).makeChoice(jChoices, iChoices);
                    boolean jChoice = population.get(j).makeChoice(iChoices, jChoices);

                    population.get(i).score += score(true, iChoice, jChoice);
                    population.get(j).score += score(false, jChoice, iChoice);

                    for (int l = 0; l < CHOICE_COUNT - 1; l++) {
                        iChoices[l + 1] = iChoices[l];
                        jChoices[l + 1] = jChoices[l];
                    }

                    iChoices[0] = iChoice;
                    jChoices[0] = jChoice;
                }                
            }
        }

        population.sort(new Comparator<Individual>() {
            @Override
            public int compare(GeneticAlgorithm.Individual o1, GeneticAlgorithm.Individual o2) {
                return o1.compareTo(o2);
            }
        });

        ArrayList<Individual> nextPopulation = new ArrayList<Individual>();

        for (int i = 1; i < population.size(); i += 2) {
            int crossover = (int) Math.floor(Math.random() * CHOICE_COUNT);
            nextPopulation.add(breed(population.get(i - 1), population.get(i), crossover));
            nextPopulation.add(breed(population.get(i), population.get(i - 1), crossover));
        }

        for (Individual individual : nextPopulation) {
            mutate(individual);
        }
    }

    public Individual getBestIndividual() {
        for (int i = 0; i < population.size(); i++) {
            for (int j = i + 1; j < population.size(); j++) {
                for (int k = 0; k < GENERATION_ITERATIONS; k++) {
                    boolean[] iChoices = new boolean[CHOICE_COUNT];
                    boolean[] jChoices = new boolean[CHOICE_COUNT];

                    boolean iChoice = population.get(i).makeChoice(jChoices, iChoices);
                    boolean jChoice = population.get(j).makeChoice(iChoices, jChoices);

                    population.get(i).score += score(true, iChoice, jChoice);
                    population.get(j).score += score(false, jChoice, iChoice);
                }                
            }
        }

        population.sort(new Comparator<Individual>() {
            @Override
            public int compare(GeneticAlgorithm.Individual o1, GeneticAlgorithm.Individual o2) {
                return o1.compareTo(o2);
            }
        });

        return population.get(0);
    }

    public static void main(String[] args) {
        GeneticAlgorithm GA = new GeneticAlgorithm();

        for (int i = 0; i < GENERATIONS; i++) {
            GA.evolveGeneration();
        }

        boolean[] bestChromosome = GA.getBestIndividual().chromosome;

        for (boolean b : bestChromosome) {
            if (b)
                System.out.print('1');
            else 
                System.out.print('0');
        }
    }
    
}
