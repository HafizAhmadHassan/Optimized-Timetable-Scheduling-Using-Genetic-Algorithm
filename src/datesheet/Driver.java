package datesheet;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Driver {

    public static void main(String[] args) {
        // TODO Auto-generated method stub


        StudentsNcoursesDataStore studentsNcoursesDataStore =
                StudentsNcoursesDataStore.getInstance();
        studentsNcoursesDataStore.populateLists();
        studentsNcoursesDataStore.getCoursesOffered();
        studentsNcoursesDataStore.writeCourseNamesToFile();
        studentsNcoursesDataStore.findStudentsRegisteredInEachCourse();

        System.out.println("Please Specify If You Want To Set Priority of Courses. (Y/N) ");
        Scanner scanner = new Scanner(System.in);
        char ch = '0';
        ArrayList<Priority> prioritiesList = new ArrayList<>();

        ch = scanner.next().charAt(0);

        while (ch == 'y' || ch == 'Y'){
            System.out.println("Enter Course Code and Day Range in Format: \"SS123 3 7\", Valid Day Range is 0-17. ");
            scanner.nextLine();
            String input = scanner.nextLine();

            String[] strings = input.split(" ");

            if (!studentsNcoursesDataStore.getCoursesHashMap().containsKey(strings[0]))
                System.out.println("Course NOT FOUND!");
            else if (Integer.parseInt(strings[1]) < 0 || Integer.parseInt(strings[2]) > Chromosome.CHROMOSOME_SIZE - 1)
                System.out.println("Invalid Range of Days!");
            else if (Integer.parseInt(strings[1]) == Integer.parseInt(strings[2]))
                System.out.println("Day1 MUST Be Smaller Than Day2!");
            else {
                studentsNcoursesDataStore.getCoursesHashMap().get(strings[0]).
                        setPriority(new Priority(studentsNcoursesDataStore.getCoursesHashMap().get(strings[0]),
                        Integer.parseInt(strings[1]), Integer.parseInt(strings[2])));
            }

            System.out.println("Press Y To Set Priority of Another Course, Press Any Key To Proceed.");
            ch = scanner.next().charAt(0);
        }

        Population population = new Population();
        population.initializePopulation(20);
        //population.printPopulation();

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

        /*System.out.println("########################################################################################################");
        System.out.println("########################################################################################################");*/

        writeTimeToFile("STARTED AT: ");

        System.out.println("IterationNo: " + 0);
        population = geneticAlgorithm.evlove(population);

        population.calculateFitnesses();
        population.sortChromosomesByFitness();
        population.printPopulation();

        //To terminate while loop on keypress
        InputStreamReader inputStream = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStream);
        Chromosome prevChromosome = new Chromosome(); prevChromosome.init(null);
        
        int i=1;
        while (population.getChromosomesList().get(0).getFitness() > 20) {
            System.out.println("IterationNo: " + i);
            population = geneticAlgorithm.evlove(population);
            population.restFitnesses();
            population.calculateFitnesses();
			population.sortChromosomesByFitness();
            System.out.println("Fitness: " +  population.getChromosomesList().get(0).getFitness());
            System.out.println("2 Exams: " + population.getChromosomesList().get(0)
            		.getNoOfStudentsWith2ExamsPerDay() + "	Clashes: " +
                    population.getChromosomesList().get(0).getNoOfDirectClashes() + "	    > 1200: "
                    + population.getChromosomesList().get(0).getNoOfDaysWithOver1200StudentsAnySlot());
            System.out.println("====================================================================");
            i++;

            prevChromosome = new Chromosome(population.getChromosomesList().get(0));

            try { if (bufferedReader.ready()) { String s = bufferedReader.readLine(); break; } }
            catch (IOException e) { e.printStackTrace(); }
        }

        prevChromosome.assignNamesToCourses(); prevChromosome.printChromosome();
        /*population.getChromosomesList().get(0).assignNamesToCourses();
        population.getChromosomesList().get(0).printChromosome();*/

        writeTimeToFile("ENDED AT: ");

    }

    public static void writeTimeToFile(String string){
        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter("Start_Finish_Time.txt", true);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.

            bufferedWriter.write(string + new Date());
            bufferedWriter.newLine();

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println(
                    "Error writing to file.");
            // Or we could just do this:
            ex.printStackTrace();
        }
    }
}