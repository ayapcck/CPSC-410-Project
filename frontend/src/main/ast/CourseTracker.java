package ast;
import visitor.Visitor;

public class CourseTracker extends SheetType {
    public CourseTrackerBlock courses;

    public CourseTracker(CourseTrackerBlock courses) {
        this.courses = courses;
    }

    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}