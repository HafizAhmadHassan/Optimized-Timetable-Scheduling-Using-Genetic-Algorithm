package datesheet;

public class Priority {

    Course course;
    int rangeDay1;
    int rangeDay2;

    Priority(){}

    public Priority(Course course, int rangeDay1, int rangeDay2) {
        this.course = course;
        this.rangeDay1 = rangeDay1;
        this.rangeDay2 = rangeDay2;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getRangeDay1() {
        return rangeDay1;
    }

    public void setRangeDay1(int rangeDay1) {
        this.rangeDay1 = rangeDay1;
    }

    public int getRangeDay2() {
        return rangeDay2;
    }

    public void setRangeDay2(int rangeDay2) {
        this.rangeDay2 = rangeDay2;
    }

    @Override
    public String toString() {
        return "Priority{" +
                "course=" + course +
                ", rangeDay1=" + rangeDay1 +
                ", rangeDay2=" + rangeDay2 +
                '}';
    }
}
