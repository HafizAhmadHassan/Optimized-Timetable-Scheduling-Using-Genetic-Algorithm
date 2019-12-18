package datesheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import javax.sql.rowset.FilteredRowSet;

public class Day {

	String[][] daySlots;
	int[] noOfexamsInEachSlot;
	int[] noOfClashesPerSlot;
	int[] noOfStudentsGivingExamEachSlot;


	int noOfStudentsWith2ExamsThisDay;

	public static final int NO_OF_SLOTS = 2;
	public static final int MAX_EXAMS_IN_SINGLE_SLOT = 25;

	/*
	 * Acc to date sheet, there are 2 slots/day, each dept can have at most 5 exams
	 * in single slot at one time, so all depts combined can have 25 exams at max in
	 * each slot
	 */
	public Day() {
		daySlots = new String[MAX_EXAMS_IN_SINGLE_SLOT][NO_OF_SLOTS];
		for (int i = 0; i < MAX_EXAMS_IN_SINGLE_SLOT; i++) {
			daySlots[i][0] = new String("----");
			daySlots[i][1] = new String("----");
		}
		noOfexamsInEachSlot = new int[NO_OF_SLOTS];
		noOfClashesPerSlot = new int[NO_OF_SLOTS];
		noOfStudentsGivingExamEachSlot = new int[NO_OF_SLOTS];

		for (int i = 0; i < NO_OF_SLOTS; i++) {
			noOfClashesPerSlot[i] = 0;
			noOfexamsInEachSlot[i] = 0;
		}
		noOfStudentsWith2ExamsThisDay = 0;
	}

	public Day(Day day){
		this.noOfexamsInEachSlot = day.getNoOfexamsInEachSlot();
		this.noOfClashesPerSlot = day.getNoOfClashesPerSlot();
		this.noOfStudentsGivingExamEachSlot = day.getNoOfStudentsGivingExamEachSlot();
		this.noOfStudentsWith2ExamsThisDay = day.getNoOfStudentsWith2ExamsThisDay();
		this.daySlots = day.getDaySlots();
	}

	@SuppressWarnings("unlikely-arg-type")
	public void initialize(ArrayList<String> courseCodesList, ArrayList<Integer> list) {

		ArrayList<Integer> randIndexes = new ArrayList<>(list);

		for (Integer i : randIndexes){
			int randRow = getRandNum(0, MAX_EXAMS_IN_SINGLE_SLOT-1);
			int randCol = getRandNum(0, NO_OF_SLOTS-1);
			while (!daySlots[randRow][randCol].equals("----")){
				randRow = getRandNum(0, MAX_EXAMS_IN_SINGLE_SLOT-1);
				randCol = getRandNum(0, NO_OF_SLOTS-1);
			}
			daySlots[randRow][randCol] = courseCodesList.get(i);
		}

	}

	public void calculateNoOfExamsInEachSlot() {

		noOfexamsInEachSlot[0] = 0; noOfexamsInEachSlot[1] = 0;

		for (int i = 0; i < NO_OF_SLOTS; i++) {
			for (int j = 0; j < MAX_EXAMS_IN_SINGLE_SLOT; j++) {
				if (!daySlots[j][i].equals("----"))
					noOfexamsInEachSlot[i]++;
			}
		}
	}

	public int[] getNoOfDirectClashesForThisDay() {
		return noOfClashesPerSlot;
	}

	public int getTotalDirectClashesForThisDay(){
		return noOfClashesPerSlot[0] + noOfClashesPerSlot[1];
	}

	public int[] calculateNoOfDirectClashesForThisDay() {

		StudentsNcoursesDataStore studentsNcoursesDataStore = StudentsNcoursesDataStore.getInstance();

		calculateNoOfExamsInEachSlot();
		noOfClashesPerSlot[0] = 0; noOfClashesPerSlot[1] = 0;

		for (int i = 0; i < NO_OF_SLOTS; i++) {

			if (noOfexamsInEachSlot[i] != 0) {

				//Allocate space only for rows in this slot which have exams in them

				ArrayList<HashMap<String, Student>> hashMapsList = new ArrayList<>();

				for (int j = 0; j < MAX_EXAMS_IN_SINGLE_SLOT; j++) {
					if (!daySlots[j][i].equals("----")) {
                        HashMap<String, Student> hashMap = new HashMap<>(studentsNcoursesDataStore.getCoursesHashMap().get(daySlots[j][i]).
                                getStudentsEnrolledHashMap());
                        hashMapsList.add(hashMap);
					}
				}

				//This code snippet calculates clashes by comparing no of courses in same slot
				//e.g if a slot has 3 courses then 1<>2, 1<>3 and then 2<3>

                for (int x=0; x< hashMapsList.size()-1; x++){
                    Iterator iterator = hashMapsList.get(x).entrySet().iterator();
                    for (int y=x+1; y< hashMapsList.size(); y++){
                        while(iterator.hasNext()){
                            HashMap.Entry entry = (HashMap.Entry) iterator.next();
                            String key = (String) entry.getKey();
                            if (hashMapsList.get(y).containsKey(key))
                                noOfClashesPerSlot[i]++;
                        }
                    }
                }
			}
		}
		return noOfClashesPerSlot;

	}

	public void removeCourse(String course){
		for (int i=0; i< NO_OF_SLOTS; i++){
			for (int j =0; j< MAX_EXAMS_IN_SINGLE_SLOT; j++){
				if (daySlots[j][i].equals(course)) {
					daySlots[j][i] = "----";
					break;
				}
			}
		}
	}

	public void setNoOfexamsInEachSlot(int[] noOfexamsInEachSlot) {
		this.noOfexamsInEachSlot = noOfexamsInEachSlot;
	}

	public void setNoOfClashesPerSlot(int[] noOfClashesPerSlot) {
		this.noOfClashesPerSlot = noOfClashesPerSlot;
	}

	public int getNoOfStudentsWith2ExamsThisDay() {
		return noOfStudentsWith2ExamsThisDay;
	}

	public void setNoOfStudentsWith2ExamsThisDay(int noOfStudentsWith2ExamsThisDay) {
		this.noOfStudentsWith2ExamsThisDay = noOfStudentsWith2ExamsThisDay;
	}

	public int calculateNoOfStudentsHaving2ExamsOnThisDay(){

		StudentsNcoursesDataStore store = StudentsNcoursesDataStore.getInstance();

		noOfStudentsWith2ExamsThisDay = 0;

		for (int i=0; i< NO_OF_SLOTS; i++){
			for (int j=0; j< MAX_EXAMS_IN_SINGLE_SLOT; j++){
				if (!daySlots[j][i].equals("----")){
                    HashMap<String, Student> hashMap = new HashMap<>(store.getCoursesHashMap().get(daySlots[j][i]).
                            getStudentsEnrolledHashMap());

					for (int k=i+1; k< NO_OF_SLOTS; k++){
						for (int l=0; l<MAX_EXAMS_IN_SINGLE_SLOT; l++){
							if (!daySlots[l][k].equals("----")){
                                HashMap<String, Student> hashMap2 = new HashMap<>(store.getCoursesHashMap().get(daySlots[l][k]).
                                        getStudentsEnrolledHashMap());
								Iterator iterator = hashMap.entrySet().iterator();
								while (iterator.hasNext()){
								    HashMap.Entry entry = (HashMap.Entry) iterator.next();
									String key = (String) entry.getKey();
								    if (hashMap2.containsKey(key))
								        noOfStudentsWith2ExamsThisDay++;
                                }
							}
						}
					}
				}
			}
		}
		return noOfStudentsWith2ExamsThisDay;
	}

	public int[] calculateNoOfStudentsGivingExamInEachSlot(){
		HashMap<String,Course> coursesHashMap = StudentsNcoursesDataStore.getInstance().getCoursesHashMap();

		noOfStudentsGivingExamEachSlot[0] = 0; noOfStudentsGivingExamEachSlot[1] = 0;
		for (int i=0; i< NO_OF_SLOTS; i++){
			for (int j=0; j< MAX_EXAMS_IN_SINGLE_SLOT; j++){
				if (!daySlots[j][i].equals("----")){
					noOfStudentsGivingExamEachSlot[i] = noOfStudentsGivingExamEachSlot[i] +
							coursesHashMap.get(daySlots[j][i]).getStudentsEnrolledHashMap().size();
				}
			}
		}
		return noOfStudentsGivingExamEachSlot;
	}

	public void assignCourseNames() {
		HashMap<String, Course> hashMap = StudentsNcoursesDataStore.getInstance().getCoursesHashMap();
		for (int i = 0; i < MAX_EXAMS_IN_SINGLE_SLOT; i++) {
			if (!daySlots[i][0].equals("----")) {
				daySlots[i][0] = daySlots[i][0] + " -- " + hashMap.get(daySlots[i][0]).getCourseName();
			}
			if (!daySlots[i][1].equals("----")) {
				daySlots[i][1] = daySlots[i][1] + " -- " + hashMap.get(daySlots[i][1]).getCourseName();
			}
		}
	}

	public int getRandNum(int min, int max) {
		int randNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		return randNum;
	}

	public Day(String[][] daySlots) {
		super();
		this.daySlots = daySlots;
	}

	public String[][] getDaySlots() {
		return daySlots;
	}

	public void setDaySlots(String[][] daySlots) {
		this.daySlots = daySlots;
	}

	public int[] getNoOfStudentsGivingExamEachSlot() {
		return noOfStudentsGivingExamEachSlot;
	}

	public int[] getNoOfexamsInEachSlot() {
		return noOfexamsInEachSlot;
	}

	public int[] getNoOfClashesPerSlot() {
		return noOfClashesPerSlot;
	}

	public static int getNoOfSlots() {
		return NO_OF_SLOTS;
	}

	public static int getMaxExamsInSingleSlot() {
		return MAX_EXAMS_IN_SINGLE_SLOT;
	}

	public void printDay() {
		System.out.print("Total Exams In Each Slot: ");
		System.out.println(Arrays.toString(noOfexamsInEachSlot));
		System.out.print("Students in Each Slot: ");
		System.out.println(Arrays.toString(noOfStudentsGivingExamEachSlot));
		System.out.println("=========================================================");

		for (int i = 0; i < MAX_EXAMS_IN_SINGLE_SLOT; i++) {
			for (int j = 0; j < NO_OF_SLOTS; j++) {
				System.out.print(daySlots[i][j] + "			");
			}
			System.out.println();
			System.out.println("__________________________________________________________");
		}
	}
}
