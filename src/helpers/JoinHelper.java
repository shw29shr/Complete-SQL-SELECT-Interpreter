package helpers;

/**
 * File with helpers needed by Sort Merge Join
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

import expvisitors.JoinVisitor;
import models.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JoinHelper {
    /**
     * Method to process join conditions checking columns whether it is present in the inner or outer schema.
     * It keeps track of inner and outer indices.
     * @param exp An expression to be evaluated
     * @param outerSchema List holding columns present in Outer Schema
     * @param innerSchema List holding columns present in Inner Schema
     * @param outerIndexes List holding outer index
     * @param innerIndexes List holding innere index
     * @return A join expression
     */
    public static Expression evaluateJoinConditions(Expression exp, List<String> outerSchema, List<String> innerSchema, List<Integer> outerIndexes, List<Integer> innerIndexes) {
        outerIndexes.clear();
        innerIndexes.clear();
        if(exp == null) return null;

        List<Expression> andExpressionList = extractAndsList(exp);
        List<Expression> eqsList = new ArrayList<Expression>();
        List<Expression> remainingList = new ArrayList<Expression>();

        for(Expression e : andExpressionList) {
            if(!(e instanceof EqualsTo)) {
                remainingList.add(e);
                continue;
            }

            EqualsTo et = (EqualsTo) e;
            String lhs = et.getLeftExpression().toString();
            String rhs = et.getRightExpression().toString();

            boolean lhsInOuterSchema = outerSchema.contains(lhs);
            boolean rhsInOuterSchema = outerSchema.contains(rhs);
            boolean lhsInInnerSchema = innerSchema.contains(lhs);
            boolean rhsInInnerSchema = innerSchema.contains(rhs);

            if((lhsInOuterSchema && rhsInOuterSchema) || (lhsInInnerSchema && rhsInInnerSchema) || !(lhsInOuterSchema && rhsInInnerSchema || rhsInOuterSchema && lhsInInnerSchema)) {
                remainingList.add(e);
                continue;
            }

            if(rhsInOuterSchema) {
                et = new EqualsTo(et.getRightExpression(), et.getLeftExpression());
                String temp = lhs;
                lhs = rhs;
                rhs = temp;
            }

            int outIndex = SortHelper.getAttributePosition(lhs, outerSchema);
            int inIndex = SortHelper.getAttributePosition(rhs, innerSchema);
            outerIndexes.add(outIndex);
            innerIndexes.add(inIndex);
            eqsList.add(et);
        }

        eqsList.addAll(remainingList);

        if(eqsList.isEmpty()) return null;

        Expression joinExpression = eqsList.get(0);
        for(int i = 1; i < eqsList.size(); i++) {
            joinExpression = new AndExpression(joinExpression, eqsList.get(i));
        }

        return joinExpression;
    }

    /**
     * Decompose a chain of AND expressions into individual and expressions and return a list.
     *
     * @param exp An expression containing AND expressions
     * @return List of AND expressions
     */
    public static List<Expression> extractAndsList(Expression exp) {
        List<Expression> andsList = new ArrayList<Expression>();
        while(exp instanceof AndExpression) {
            AndExpression and = (AndExpression) exp;
            andsList.add(and.getRightExpression());
            exp = and.getLeftExpression();
        }
        andsList.add(exp);
        Collections.reverse(andsList);
        return andsList;
    }

}
