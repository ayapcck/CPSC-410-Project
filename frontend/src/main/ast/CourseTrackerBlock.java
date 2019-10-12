package ast;

import visitor.Visitor;
import java.util.Map;

public class CourseTrackerBlock extends Block {
    public Map<String, CourseDetailBlock> coursesInformation;

    public CourseTrackerBlock(Map<String, CourseDetailBlock> coursesInfo) {
        coursesInformation = coursesInfo;
    }
    @Override
    public <R> R accept(Visitor<R> v) {
        return null;
    }
}
