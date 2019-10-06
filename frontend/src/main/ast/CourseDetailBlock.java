package ast;

import visitor.Visitor;

import java.util.Map;

public class CourseDetailBlock extends Block {
    public Map<String, ExamDetailBlock> examDetails;
    public float goalGrade;


    public CourseDetailBlock(Map<String, ExamDetailBlock> examDetails, int goalGrade) {
        this.examDetails = examDetails;
        this.goalGrade = goalGrade;
    }
    @Override
    public <R> R accept(Visitor<R> v) {
        return null;
    }
}
