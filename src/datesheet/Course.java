package datesheet;


import java.util.ArrayList;
import java.util.HashMap;

public class Course {
	String courseCode;
	String courseName;
	HashMap<String, Student> studentsEnrolledHashMap;
	Priority priority;


	public Course() {
		// TODO Auto-generated constructor stub
		courseCode = null;
		courseName = null;
		studentsEnrolledHashMap = new HashMap<>();
		priority = null;
	}

	public Course(String courseCode, String courseName, HashMap<String, Student> studentsEnrolledHashMap) {
		this.courseCode = courseCode;
		this.courseName = courseName;
		this.studentsEnrolledHashMap = studentsEnrolledHashMap;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public HashMap<String, Student> getStudentsEnrolledHashMap() {
		return studentsEnrolledHashMap;
	}

	public void setStudentsEnrolledHashMap(HashMap<String, Student> studentsEnrolledHashMap) {
		this.studentsEnrolledHashMap = studentsEnrolledHashMap;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}
}
