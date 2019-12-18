package datesheet;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class GeneticAlgorithm {

	public static double MUTATION_RATE = 0.30;
	public static final int TOURNAMENT_POPULATION_SIZE = 4;
	public static int ELITISM = 2;
	public static final double  CROSSOVER_RATE = 0.9;

	GeneticAlgorithm(){
	}


	public Population evlove(Population population) {

		return mutatePopulation(crossoverPopulation(population));
	}

	public Population crossoverPopulation(Population population){
		Population crossedOverPopulation = new Population();
		for (int i=0; i< ELITISM; i++){
			crossedOverPopulation.getChromosomesList().add(population.getChromosomesList().get(i));
		}
		for (int i = ELITISM; i< population.getChromosomesList().size(); i++){

			if (CROSSOVER_RATE > Math.random()) {
				Chromosome parent1 = selectTournamentPopulation(population).getChromosomesList().get(0);
				Chromosome parent2 = selectTournamentPopulation(population).getChromosomesList().get(0);
				crossedOverPopulation.getChromosomesList().add(crossoverChromosomes(parent1, parent2));
			}else
				crossedOverPopulation.getChromosomesList().add(population.getChromosomesList().get(i));
		}
		return crossedOverPopulation;
	}

	public Chromosome crossoverChromosomes(Chromosome parent1, Chromosome parent2){

		Chromosome newChromosome = new Chromosome();
		newChromosome.init(null);

		ArrayList<String> courseCodesList = new ArrayList<>(StudentsNcoursesDataStore.getInstance().getCoursesHashMap().keySet());
		for (int i=0; i< courseCodesList.size()/2; i++){
			int[] coursePosition = parent1.getCourseIndex(courseCodesList.get(i));
			newChromosome.getDays()[coursePosition[0]].getDaySlots()[coursePosition[1]][coursePosition[2]]
					= courseCodesList.get(i);
		}

		for (int i=courseCodesList.size()/2; i< courseCodesList.size(); i++){
			int[] coursePosition = parent2.getCourseIndex(courseCodesList.get(i));
			int j= coursePosition[0], k = coursePosition[1], l = coursePosition[2];

			Day[] days = newChromosome.getDays();
			String[][] daySlots = days[j].getDaySlots();
			String s = daySlots[k][l];

			while (!s.equals("----")){
				j = getRandNum(0, Chromosome.CHROMOSOME_SIZE-1);
				k = getRandNum(0, Day.MAX_EXAMS_IN_SINGLE_SLOT-1);
				l = getRandNum(0, Day.NO_OF_SLOTS-1);
				daySlots = days[j].getDaySlots();
				s = daySlots[k][l];
			}
			days[j].getDaySlots()[k][l]
					= courseCodesList.get(i);
		}
		return newChromosome;
	}

	public Population mutatePopulation(Population population){
		Population mutatedPopulation = new Population();
		for (int i=0; i< ELITISM; i++){
			mutatedPopulation.getChromosomesList().add(population.getChromosomesList().get(i));
		}
		for (int i = ELITISM; i< population.getChromosomesList().size(); i++){
			Chromosome chromosome = population.getChromosomesList().get(i);
			if (MUTATION_RATE > Math.random())
				mutateChromosome(chromosome);
			mutatedPopulation.getChromosomesList().add(chromosome);
		}
		return mutatedPopulation;
	}

	public void mutateChromosome(Chromosome chromosome){
		ArrayList<String> courseCodesList = new ArrayList<>(StudentsNcoursesDataStore.getInstance()
				.getCoursesHashMap().keySet());
		int noOfCoursesToMove = (int) (MUTATION_RATE * courseCodesList.size());
		ArrayList<String> coursesToMove = new ArrayList<>();

		for (int i=0; i< noOfCoursesToMove; i++){
			int rand = getRandNum(0, courseCodesList.size()-1);
			while (coursesToMove.contains(courseCodesList.get(rand)))
				rand = getRandNum(0, courseCodesList.size()-1);
			coursesToMove.add(courseCodesList.get(rand));
		}

		for (int i=0; i< noOfCoursesToMove; i++) {

			if (!(StudentsNcoursesDataStore.getInstance().getCoursesHashMap().get(coursesToMove.get(i)).
					getPriority() == null)) {
				mutateAccToPriority(chromosome, coursesToMove.get(i));
			} else {

				chromosome.removeCourse(coursesToMove.get(i));
				int randDay1 = getRandNum(0, Chromosome.CHROMOSOME_SIZE - 1);
				int randRow1 = getRandNum(0, Day.MAX_EXAMS_IN_SINGLE_SLOT - 1);
				int randCol1 = getRandNum(0, Day.NO_OF_SLOTS - 1);

				while (!chromosome.getDays()[randDay1].getDaySlots()[randRow1][randCol1].equals("----")) {
					randDay1 = getRandNum(0, Chromosome.CHROMOSOME_SIZE - 1);
					randRow1 = getRandNum(0, Day.MAX_EXAMS_IN_SINGLE_SLOT - 1);
					randCol1 = getRandNum(0, Day.NO_OF_SLOTS - 1);
				}

				chromosome.getDays()[randDay1].getDaySlots()[randRow1][randCol1] = coursesToMove.get(i);
			}
		}
	}

	private void mutateAccToPriority(Chromosome chromosome, String courseToMove){
		int r1 = StudentsNcoursesDataStore.getInstance().getCoursesHashMap().get(courseToMove)
				.getPriority().getRangeDay1();
		int r2 = StudentsNcoursesDataStore.getInstance().getCoursesHashMap().get(courseToMove)
				.getPriority().getRangeDay2();

		int randDay1, randRow1, randCol1;

		/*If the course is not in required range, find the position of course in chromosome and
		swap some course from given range with the course that is to be brought in given range.*/
		if (chromosome.getCourseIndexInRange(r1, r2, courseToMove) == null){
			int[] missingCoursePosition = chromosome.getCourseIndex(courseToMove);

			randDay1 = getRandNum(r1, r2 - 1);
			randRow1 = getRandNum(0, Day.MAX_EXAMS_IN_SINGLE_SLOT - 1);
			randCol1 = getRandNum(0, Day.NO_OF_SLOTS - 1);

			String tempCourse = chromosome.getDays()[randDay1].getDaySlots()[randRow1][randCol1];
			chromosome.getDays()[randDay1].getDaySlots()[randRow1][randCol1] = chromosome.getDays()
					[missingCoursePosition[0]].getDaySlots()[missingCoursePosition[1]][missingCoursePosition[2]];
			chromosome.getDays()
					[missingCoursePosition[0]].getDaySlots()[missingCoursePosition[1]][missingCoursePosition[2]]
					= tempCourse;
		}else {
			int[] coursePosition = chromosome.getCourseIndex(courseToMove);

			randDay1 = getRandNum(r1, r2 - 1);
			randRow1 = getRandNum(0, Day.MAX_EXAMS_IN_SINGLE_SLOT - 1);
			randCol1 = getRandNum(0, Day.NO_OF_SLOTS - 1);

			String tempCourse = chromosome.getDays()[randDay1].getDaySlots()[randRow1][randCol1];
			chromosome.getDays()[randDay1].getDaySlots()[randRow1][randCol1] = chromosome.getDays()
					[coursePosition[0]].getDaySlots()[coursePosition[1]][coursePosition[2]];
			chromosome.getDays()
					[coursePosition[0]].getDaySlots()[coursePosition[1]][coursePosition[2]] = tempCourse;

		}

	}

	public Population selectTournamentPopulation(Population population) {

		Population tournamentPopulation = new Population();
		for (int i = 0; i < TOURNAMENT_POPULATION_SIZE; i++) {
			tournamentPopulation.getChromosomesList().
					add(population.getChromosomesList().get(getRandNum(0, population.getPopulationSize() - 1)));
		}
		tournamentPopulation.sortChromosomesByFitness();
		return tournamentPopulation;
	}

	public int getRandNum(int min, int max) {
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		return randomNum;
	}

}
