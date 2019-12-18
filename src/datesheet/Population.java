package datesheet;

import java.util.ArrayList;
import java.util.Collections;

public class Population {
    ArrayList<Chromosome> chromosomesList;

    Population(){
        chromosomesList = new ArrayList<>();
    }

    public void initializePopulation(int populationSize) {
        for (int i = 0; i < populationSize; i++) {
            Chromosome chromosome = new Chromosome();
            chromosome.init();
            chromosomesList.add(chromosome);
        }
    }

    public void restFitnesses() {
        for (Chromosome chromosome : chromosomesList)
            chromosome.resetFitness();
    }

    public void calculateFitnesses() {
        for (Chromosome chromosome : chromosomesList)
            chromosome.calculateFitness();
    }

    public void sortChromosomesByFitness() {
        // bubble sort
        for (int i = 0; i < chromosomesList.size() - 1; i++) {
            for (int j = 0; j < chromosomesList.size() - i - 1; j++) {
                if (chromosomesList.get(j).getFitness() > chromosomesList.get(j + 1).getFitness()) {
                    Collections.swap(chromosomesList, j, j + 1);
                }
            }
        }
    }

    public ArrayList<Chromosome> getChromosomesList() {
        return chromosomesList;
    }

    public void setChromosomesList(ArrayList<Chromosome> chromosomesList) {
        this.chromosomesList = chromosomesList;
    }

    public int getPopulationSize(){
        return chromosomesList.size();
    }

    public void printPopulation() {
        int x = 0;

        for (Chromosome chromosome : chromosomesList) {
            System.out.println("Chromosome # " + x);
            System.out.println("******************************************");
            chromosome.printChromosome();
            x++;
        }
    }
}
