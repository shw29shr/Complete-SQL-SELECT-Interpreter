package indexhelpers;

import helpers.SelectExecutorHelper;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.util.List;

/**
 * This class has helpers required for building and maintaing indexes.
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 *
 */
public class BPlusTreeIndexHelper {

    public static Integer[] computeKeys(String indexAttribute, Expression selCondition) {
        if (selCondition == null) return null;
        List<Expression> conditions = SelectExecutorHelper.decomposeAnds(selCondition);
        Integer[] keysArray = new Integer[2];
        for (Expression expr : conditions) {
            Expression left = ((BinaryExpression) expr).getLeftExpression();
            Expression right = ((BinaryExpression) expr).getRightExpression();

            String attribute;
            Integer val;
            if (left instanceof Column) {
                attribute = left.toString();
                val = Integer.parseInt(right.toString());
            } else {
                attribute = right.toString();
                val = Integer.parseInt(left.toString());
            }
            if (attribute.indexOf('.') != -1)
                attribute = attribute.split("\\.")[1];
            if (!indexAttribute.equals(attribute)) continue;

            if (expr instanceof GreaterThan) {
                if (keysArray[0] == null) {
                    keysArray[0] = val + 1;
                } else {
                    keysArray[0] = Math.max(keysArray[0], val + 1);
                }
            } else if (expr instanceof GreaterThanEquals) {
                if (keysArray[0] == null) {
                    keysArray[0] = val;
                } else {
                    keysArray[0] = Math.max(keysArray[0], val);
                }
            } else if (expr instanceof MinorThan) {
                if (keysArray[1] == null) {
                    keysArray[1] = val;
                } else {
                    keysArray[1] = Math.min(keysArray[1], val);
                }
            } else if (expr instanceof MinorThanEquals) {
                if (keysArray[1] == null) {
                    keysArray[1] = val + 1;
                } else {
                    keysArray[1] = Math.min(keysArray[1], val + 1);
                }
            } else if (expr instanceof EqualsTo) {
                keysArray[0] = val;
                keysArray[1] = val;
            }
        }
        return keysArray;
    }
}
