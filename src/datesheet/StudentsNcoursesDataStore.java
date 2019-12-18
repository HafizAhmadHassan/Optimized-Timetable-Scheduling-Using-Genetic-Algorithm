package datesheet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/*HashMaps are maintained for constant time search operations. Students list along with HashMap is maintained to find students registered in each course.
* Students list contains multiple entries of same student with different courses, as provided in the excel file. So its simpler to iterate through students
* list and find students registered in a particular course. Only having a HashMap wasn't enough since it removes duplicate entries, so student object with duplicate
* roll numbers but different courses would not have been saved. Student and Course HashMaps are later used to iterate/search through students at different points in
 * fitness calculation etc to speed up the process.*/

public class StudentsNcoursesDataStore {

	Scanner scanner;

	ArrayList<Student> studentsList;

	//Students objects stored against roll numbers
	HashMap<String, Student> studentsHashMap = new HashMap<>();

	//Course objects stored against course codes
	HashMap<String, Course> coursesHashMap = new HashMap<>();

	private int noOfStudents = 0;
	private int noOfCourses = 0;

	String buffer;
	int size = 0;

	private static StudentsNcoursesDataStore studentsNcoursesDataStore = null;

	private StudentsNcoursesDataStore() {
		scanner = null;
		studentsList = new ArrayList<>();
		coursesHashMap = new HashMap<>();
	}

	public static StudentsNcoursesDataStore getInstance() {
		if (studentsNcoursesDataStore == null) {
			studentsNcoursesDataStore = new StudentsNcoursesDataStore();
			return studentsNcoursesDataStore;
		}

		return studentsNcoursesDataStore;
	}

	public void populateLists() {
		File file = new File("Students.txt");
		if (file.exists()) {
			//read from file
			readStudents(file);


		} else {
			//readFromExcelFile
			readFromExcelFile();

		}
	}


	public void readFromExcelFile(){

		FileInputStream fileInputStream;

		{
			try {
				fileInputStream = new
						FileInputStream(new File("Exam Data-Spring 2018-Mid-1.1.xlsx"));

				XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

				XSSFSheet sheet = workbook.getSheetAt(0);

				FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

				int cellCount = 0, maxCells = 6;
				boolean firstRow = true;

				int id = 0;

				for (Row row : sheet) {

					if (!firstRow) {
						Student student = new Student();
						student.setSerialNo(String.valueOf((int)row.getCell(cellCount).getNumericCellValue()));
						cellCount++;
						student.setRollNo(row.getCell(cellCount).getStringCellValue());
						cellCount++;
						student.setName(row.getCell(cellCount).getStringCellValue());
						cellCount++;
						student.setDegree(row.getCell(cellCount).getStringCellValue());
						cellCount++;
						student.setCourseId(row.getCell(cellCount).getStringCellValue());
						cellCount++;
						student.setCourseName(row.getCell(cellCount).getStringCellValue());
						student.setId(id);
						if (student.getDegree().equals("BBA") || student.getDegree().equals("BS(AF)") ||
								student.getDegree().equals("BS(CS)") || student.getDegree().equals("BS(CV)")
								|| student.getDegree().equals("BS(EE)")) {

							studentsList.add(student);
							studentsHashMap.put(student.getRollNo(), student); id++;
							writeStudentToFile("Students.txt", student);
							//System.out.println(student.toString());
							if (!student.getCourseName().contains("Lab") && !student.getCourseName().contains("Project")
									&& !student.getCourseName().contains("Thesis")) {
								Course course = new Course();
								course.setCourseCode(student.getCourseId());
								course.setCourseName(student.getCourseName());
								coursesHashMap.put(course.getCourseCode(), course);
							}
						} else
							break;
						cellCount = 0;
					}
					firstRow = false;
				}
				noOfCourses = coursesHashMap.size(); noOfStudents = studentsHashMap.size();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	//47 labs, 6 projects
	
		public void writeCourseNamesToFile() {
			try {
	            // Assume default encoding.
	            FileWriter fileWriter =
	                new FileWriter("Courses Offered.txt");

	            // Always wrap FileWriter in BufferedWriter.
	            BufferedWriter bufferedWriter =
	                new BufferedWriter(fileWriter);

	            // Note that write() does not automatically
	            // append a newline character.

				HashMap<String, Course> tempHashMap = new HashMap<>(coursesHashMap);

				Iterator iterator = tempHashMap.entrySet().iterator();
				while (iterator.hasNext()){
					HashMap.Entry entry = (HashMap.Entry) iterator.next();
					bufferedWriter.write(entry.getKey().toString());
					bufferedWriter.newLine();
					iterator.remove();
				}

	            // Always close files.
	            bufferedWriter.close();
	        }
	        catch(IOException ex) {
	            System.out.println(
	                "Error writing to file '"
	                + "Courses Offered.txt" + "'");
	            // Or we could just do this:
	            ex.printStackTrace();
	        }
			
		}

		public void writeStudentToFile(String fileName, Student student){
			try {
				// Assume default encoding.
				FileWriter fileWriter =
						new FileWriter(fileName, true);

				// Always wrap FileWriter in BufferedWriter.
				BufferedWriter bufferedWriter =
						new BufferedWriter(fileWriter);

				// Note that write() does not automatically
				// append a newline character.

				bufferedWriter.write(student.getSerialNo() + "		");
				bufferedWriter.write(student.getRollNo() + "		");
				bufferedWriter.write(student.getName() + "		");
				bufferedWriter.write(student.getDegree() + "		");
				bufferedWriter.write(student.getCourseId() + "		");
				bufferedWriter.write(student.getCourseName() + "		");
				bufferedWriter.write(student.getId() + "		");
				bufferedWriter.newLine();

				// Always close files.
				bufferedWriter.close();
			}
			catch(IOException ex) {
				System.out.println(
						"Error writing to file '"
								+ "Courses Offered.txt" + "'");
				// Or we could just do this:
				ex.printStackTrace();
			}
		}
		
		/*This function removes duplicates from courses list generated while
		 * reading students and gives complete list of courses in which students 
		 * are enrolled and which are to be scheduled for examination.*/	
		public void getCoursesOffered() {

			noOfCourses = coursesHashMap.size();

			System.out.println("COURSES: " + noOfCourses);
		}
		
		/* This function reads BS students from "Student Course" sheet and 
		 * populates students and courses list.
	*/	
		public void readStudents(File file) {
			try {
				scanner = new Scanner(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//int id = 0;

			if (scanner != null) {
				buffer = scanner.nextLine();
				int studentId = 0;
				while (scanner.hasNextLine()) {
					buffer = scanner.nextLine();

					String[] splitString = buffer.split("		");

					Student student = new Student(splitString[0], splitString[1], splitString[2],
							splitString[3], splitString[4], splitString[5], Integer.valueOf(splitString[6]));

					if (splitString[3].equals("BBA") || splitString[3].equals("BS(AF)")
							|| splitString[3].equals("BS(CS)") || splitString[3].equals("BS(CV)")
							|| splitString[3].equals("BS(EE)")) {
						studentsList.add(student);
						studentsHashMap.put(student.getRollNo(), student);

						if (!splitString[5].contains("Lab") && !splitString[5].contains("Project") &&
								!splitString[5].contains("Thesis")) {
							Course course = new Course();
							course.setCourseCode(student.getCourseId());
							course.setCourseName(student.getCourseName());
							coursesHashMap.put(course.getCourseCode(), course);
						}

					}
				}
			}
			noOfCourses = coursesHashMap.size(); noOfStudents = studentsHashMap.size();
		}
		
		public void findStudentsRegisteredInEachCourse() {

			HashMap<String, Course> tempHashMap = new HashMap<>(coursesHashMap);
			Iterator iterator = tempHashMap.entrySet().iterator();
			while (iterator.hasNext()) {
				HashMap.Entry entry = (HashMap.Entry) iterator.next();
				Course course = (Course) entry.getValue();

				for (Student student : studentsList) {
					if (student.getCourseId().equals(course.getCourseCode()))
						course.getStudentsEnrolledHashMap().put(student.getRollNo(), student);
				}

				try {
					FileWriter fileWriter =
							new FileWriter("Course Files/" + course.getCourseCode() + ".txt");

					BufferedWriter bufferedWriter =
							new BufferedWriter(fileWriter);

					bufferedWriter.write(course.getCourseCode() + "		" + course.getCourseName());
					bufferedWriter.newLine();
					bufferedWriter.write("==========================================");
					bufferedWriter.newLine();

					Iterator studentsIterator = course.getStudentsEnrolledHashMap().entrySet().iterator();
					while (studentsIterator.hasNext()) {
						HashMap.Entry hashMapEntry = (HashMap.Entry) studentsIterator.next();
						Student student = (Student) hashMapEntry.getValue();
						bufferedWriter.write(student.getId() + "	" + student.getRollNo() + "	" + student.getName());
						bufferedWriter.newLine();
					}

					bufferedWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(
							"Error writing to file '"
									+ course + ".txt");
					e.printStackTrace();
				}
				iterator.remove();
			}
		}


		public ArrayList<Student> getStudentsList() {
			return studentsList;
		}

		public HashMap<String, Course> getCoursesHashMap() {
			return coursesHashMap;
		}

		public int getNoOfStudents() {
			return noOfStudents;
		}

		public int getNoOfCourses() {
			return noOfCourses;
		}

		public Scanner getScanner() {
			return scanner;
		}

		public String getBuffer() {
			return buffer;
		}

		public int getSize() {
			return size;
		}

		public static StudentsNcoursesDataStore getStudentsNcoursesDataStore() {
			return studentsNcoursesDataStore;
		}

		public void setCoursesHashMap(HashMap<String, Course> coursesHashMap) {
			this.coursesHashMap = coursesHashMap;
		}

		
		
		
		/*A helper function that verifies the number of courses mentioned in
		 * "DateSheet" sheet in given excel sheet, with the ones read from "Student
		 * Course" excel sheet"
	*/	
		/*public void officialCourses() {
			
			ArrayList<String> officialList = new ArrayList<>();
			
			file = new File("C:\\Users\\AhsanZ\\Desktop\\Courses Offered Official.txt");
			try {
				scanner = new Scanner(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (scanner != null) {
				buffer = scanner.nextLine();
				while(scanner.hasNextLine()) {
					buffer = scanner.nextLine();
					
					System.out.println("String Read:	");
					System.out.println(buffer);
					
					
					String[] splitString = buffer.split("	");
					
					//System.out.println("STRING:	");
					//System.out.println(Arrays.toString(splitString));
					
					String string = splitString[2] + " " + splitString[3];
					
					officialList.add(string);
					System.out.println("STRING:	" + string);
				}
				System.out.println("SIZE:	" + officialList.size());

			}
			
			System.out.println("==================================================");

			boolean flag = false;
			for (String course : officialList) {
				if (!courseCodesList.contains(course)) {
					System.out.println("Missing Course: " + course);
					flag = true;
				}
			}
			if (!flag)
				System.out.println("All Courses Match! No Missing Courses Found!");

			
		}*/

}
