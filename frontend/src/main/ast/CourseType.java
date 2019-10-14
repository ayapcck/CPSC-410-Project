package ast;

import visitor.Visitor;

public class CourseType extends Type {

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        String course = (String) other;
        String[] coursesplit = course.split(" ");
        if (coursesplit.length != 2)
            return false;
        String dept = coursesplit[0].toUpperCase();
        String id = coursesplit[1];
        if (dept.length() <= 4 && dept.length() >= 2) {
            for (int i = 0; i < dept.length(); i++) {
                if (dept.charAt(i) < 'A' || dept.charAt(i) > 'Z') return false;
            }
        } else {
            return false;
        }
        if (id.length() != 3) return false;
        for (int i = 0; i < id.length(); i++) {
            if (id.charAt(i) < '0' || id.charAt(i) > '9') return false;
        }

        return true;
    }

}