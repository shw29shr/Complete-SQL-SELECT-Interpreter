package helpers;

import expvisitors.SelectVisitor;
import models.Tuple;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * File with helpers needed by SelectExecutor, which builds the expression tree
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class SelectExecutorHelper {

    /**
     * Get table name from the input FromItem type
     *
     * @param fromString String that contains the table name, in the from part of an
     *                expression
     * @return First table name, that we get by splitting based on space
     */
    public static String getSingleTableName(FromItem fromString) {
        String returnTable = fromString.toString().split(" ")[0];
        return returnTable;
    }


    /**
     * Decompose an AND expression recursively into a list of
     * binary expressions.
     *
     * @param exp the expression
     * @return a list of basic expressions
     */
    public static List<Expression> decomposeAnds(Expression exp) {
        List<Expression> ret = new ArrayList<>();
        while (exp instanceof AndExpression) {
            AndExpression and = (AndExpression) exp;
            ret.add(and.getRightExpression());
            exp = and.getLeftExpression();
        }
        ret.add(exp);

        Collections.reverse(ret);
        return ret;
    }

    public static boolean checkValidComparison(Expression exp) {
        if (exp == null) return false;
        return (exp instanceof EqualsTo) ||
                (exp instanceof MinorThan) ||
                (exp instanceof MinorThanEquals) ||
                (exp instanceof GreaterThan) ||
                (exp instanceof GreaterThanEquals);
    }
}
