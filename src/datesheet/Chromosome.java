package datesheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class Chromosome {

	Day[] days;
	private int noOfDirectClashes;
	private int noOfStudentsWith2ExamsPerDay;
	private int noOfDaysWithOver1200StudentsAnySlot;

	private int fitness;

	public static final int CHROMOSOME_SIZE = 18;


	public Chromosome() {
		days = new Day[CHROMOSOME_SIZE];
		noOfDirectClashes = 0;
		noOfStudentsWith2ExamsPerDay = 0;
		noOfDaysWithOver1200StudentsAnySlot = 0;
		fitness = 0;
	}

	public Chromosome(Chromosome chromosome){
		this.noOfDirectClashes = chromosome.getNoOfDirectClashes();
		this.noOfStudentsWith2ExamsPerDay = chromosome.getNoOfStudentsWith2ExamsPerDay();
		this.noOfDaysWithOver1200StudentsAnySlot = chromosome.getNoOfDaysWithOver1200StudentsAnySlot();
		this.fitness = chromosome.getFitness();
		this.days = new Day[CHROMOSOME_SIZE];
		for (int i=0; i< CHROMOSOME_SIZE; i++){
			this.days[i] = new Day(chromosome.getDays()[i]);
		}
	}

	public int getRandNum(int min, int max) {
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		return randomNum;
	}

	public void init(String s){
		for (int i=0; i< CHROMOSOME_SIZE; i++){
			days[i] = new Day();
		}
	}

	public void init() {

		StudentsNcoursesDataStore store = StudentsNcoursesDataStore.getInstance();

		//KeySet of coursesHashMap is converted to list of strings which actually is a list of course codes
		ArrayList<String> courseCodesList = new ArrayList<>(store.getCoursesHashMap().keySet());


		ArrayList<Integer> daysInitialized = new ArrayList<>();

		boolean firstTime = true;

		//a random day is selected acc to chromosome size and is initialized with random courses out of courseCodesList
		// , process is repeated till all days are initialized
		for (int i=0; i< CHROMOSOME_SIZE; i++) {
			int randNum = getRandNum(0, CHROMOSOME_SIZE - 1);
			while (daysInitialized.contains(randNum))
				randNum = getRandNum(0, CHROMOSOME_SIZE - 1);
			days[randNum] = new Day();
			daysInitialized.add(randNum);

			//indexes to be taken out of courseCodesList
			ArrayList<Integer> randomInts = new ArrayList<>();

			//no of indexes to be taken out
			int noOfCourses = 0;

			if (firstTime) {
				noOfCourses = courseCodesList.size() % 10;
				firstTime = false;
			}else {
				if (courseCodesList.size() < 10)
					noOfCourses = courseCodesList.size();
				else {
					noOfCourses = 10;
				}
			}

			for (int j=0; j< noOfCourses; j++) {

				int rand = getRandNum(0, courseCodesList.size()-1);
				while(randomInts.contains(rand)) {
					rand = getRandNum(0, courseCodesList.size()-1);
				}
				randomInts.add(rand);
			}
			days[randNum].initialize(courseCodesList, randomInts);
			//days[randNum].calculateNoOfExamsInEachSlot();
			randomInts.sort(null);
			Collections.reverse(randomInts);
			for (Integer integer : randomInts) {
				courseCodesList.remove(integer.intValue());
			}
		}

		System.out.print("");
	}

	public int getFitness() {
		return fitness;
	}

	public void calculateFitness() {
		calculateNoOfDirectClashes();
		calculateNoOfStudentsWith2ExamsPerDay();
		calculateNoOfStudentsGivingExamInEachSlot();
		fitness = noOfDirectClashes + noOfStudentsWith2ExamsPerDay;
	}

	public void resetFitness() {
		fitness = 0;
		this.noOfDirectClashes = 0;
		this.noOfStudentsWith2ExamsPerDay = 0;
		this.noOfDaysWithOver1200StudentsAnySlot = 0;
	}

	public int getNoOfDaysWithOver1200StudentsAnySlot() {
		return noOfDaysWithOver1200StudentsAnySlot;
	}

	private void calculateNoOfDirectClashes() {
		int[] clashes;
		int slot1 = 0, slot2 = 0;
		for (int i=0; i<CHROMOSOME_SIZE; i++) {
			clashes = days[i].calculateNoOfDirectClashesForThisDay();
			slot1 = slot1 + clashes[0];
			slot2 = slot2 + clashes[1];
		}

		noOfDirectClashes = slot1+slot2;
	}

	private void calculateNoOfStudentsWith2ExamsPerDay(){

		noOfStudentsWith2ExamsPerDay = 0;

		for (Day day : days){
			noOfStudentsWith2ExamsPerDay = noOfStudentsWith2ExamsPerDay + day.calculateNoOfStudentsHaving2ExamsOnThisDay();
		}
	}

	private void calculateNoOfStudentsGivingExamInEachSlot(){
		noOfDaysWithOver1200StudentsAnySlot = 0;
		for (Day day : days){
			int[] slots = day.calculateNoOfStudentsGivingExamInEachSlot();
			if (slots[0] > 1200 || slots[1] > 1200)
				noOfDaysWithOver1200StudentsAnySlot++;
		}
	}

	//used during crossover and mutation
	public int[] getCourseIndex(String course) {
        int[] coursePosition = new int[3];
        for (int k = 0; k < CHROMOSOME_SIZE; k++) {
            for (int i = 0; i < Day.NO_OF_SLOTS; i++) {
                for (int j = 0; j < Day.MAX_EXAMS_IN_SINGLE_SLOT; j++) {
                    if (days[k].getDaySlots()[j][i].equals(course)) {
                        coursePosition[0] = k; //day
                        coursePosition[1] = j; //row
                        coursePosition[2] = i; //col
                        break;
                    }
                }
            }
        }
        return coursePosition;
    }

    public int[] getCourseIndexInRange(int rangeDay1, int rangeDay2, String course){
        int[] coursePosition = new int[3];
        boolean found = false;
        for (int k=rangeDay1; k< rangeDay2 + 1; k++){
            for (int i=0; i< Day.NO_OF_SLOTS; i++){
                for (int j=0; j< Day.MAX_EXAMS_IN_SINGLE_SLOT; j++){
                    if (days[k].getDaySlots()[j][i].equals(course)){
                        coursePosition[0] = k; //day
                        coursePosition[1] = j; //row
                        coursePosition[2] = i; //col
                        found = true;
                        break;
                    }
                }
            }
        }
        if (found)
            return  coursePosition;
        else return null;
    }

	//used during crossover and mutation
	public void removeCourse(String course){
		for(int i=0; i< CHROMOSOME_SIZE; i++){
			days[i].removeCourse(course);
		}
	}

	public void assignNamesToCourses() {
		for (int i=0;i< CHROMOSOME_SIZE; i++) {
			days[i].assignCourseNames();
		}
	}

	public static int getChromosomeSize() {
		return CHROMOSOME_SIZE;
	}

	public void printChromosome() {
		for (int i=0;i< CHROMOSOME_SIZE; i++) {
			System.out.println("DAY " + i + "====>	Clashes:	" +  Arrays.toString(days[i].
					getNoOfClashesPerSlot()));
			days[i].printDay();
			System.out.println("===========================================================");
		}
		System.out.println("No of Direct Clashes: " + noOfDirectClashes + " | ");
		System.out.print("No of Students With 2 Exams/Day: " + noOfStudentsWith2ExamsPerDay + " | ");
		System.out.println("No of Days Exceeding 1200 Students: " + noOfDaysWithOver1200StudentsAnySlot + " | ");
		System.out.println("#############################################################################################################");
	}


	public Day[] getDays() {
		return days;
	}


	public void setDays(Day[] days) {
		this.days = days;
	}


	public int getNoOfDirectClashes() {
		return noOfDirectClashes;
	}


	public void setNoOfDirectClashes(int noOfDirectClashes) {
		this.noOfDirectClashes = noOfDirectClashes;
	}


	public int getNoOfStudentsWith2ExamsPerDay() {
		return noOfStudentsWith2ExamsPerDay;
	}


	public void setNoOfStudentsWith2ExamsPerDay(int noOfStudentsWith2ExamsPerDay) {
		this.noOfStudentsWith2ExamsPerDay = noOfStudentsWith2ExamsPerDay;
	}
}
