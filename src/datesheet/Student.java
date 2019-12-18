package datesheet;


public class Student {
	String serialNo;
	String rollNo;
	String name;
	String degree;
	String courseId;
	String courseName;
	int id;
	
	public Student() {
		// TODO Auto-generated constructor stub
		id = 0;
	}
	
	public Student(String serialNo, String rollNo, String name, String degree,
			String courseId, String courseName, int id) {
		super();
		this.serialNo = serialNo;
		this.rollNo = rollNo;
		this.name = name;
		this.degree = degree;
		this.courseId = courseId;
		this.courseName = courseName;
		this.id = id;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getRollNo() {
		return rollNo;
	}
	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Student [serialNo=" + serialNo + ", rollNo=" + rollNo + ", name=" + name + ", degree=" + degree
				+ ", courseId=" + courseId + ", courseName=" + courseName + ", id=" + id + "]";
	}

	
	
	

}
