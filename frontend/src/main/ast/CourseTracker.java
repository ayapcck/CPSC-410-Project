package ast;
import visitor.Visitor;

import java.util.Map;

public class CourseTracker extends SheetType {
    public Map<String, CourseDetailBlock> courses;

    public CourseTracker(Map<String, CourseDetailBlock> courses) {
        this.courses = courses;
    }

    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}