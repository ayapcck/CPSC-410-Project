package ast;

import visitor.Visitor;

public class MonthType extends Type {

    String[] months = { "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December" };

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        String otherMonth = (String) other;
        for (String m : months) {
            if (otherMonth.toLowerCase().equals(m.toLowerCase()))
                return true;
        }
        return false;
    }

}
