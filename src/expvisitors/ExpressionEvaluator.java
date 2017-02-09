package expvisitors;

import models.Tuple;
import net.sf.jsqlparser.expression.Expression;

/**
 * Class which evaluates the given select and join expressions
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class ExpressionEvaluator {

    /**
     * This function is called in the Select Operator to check if the current tuple satisfies the select condition
     * The Select Visitor carries with itself the information about the tuple and its schema
     *
     * @param selectCondition Expression which represents the select condition
     * @param selVisitor      Visitor generated by the SelectOperator which we use to evaluate result
     * @return The result of the given expression evaluation - True or False
     */
    public static boolean evaluateSelectExpression(Tuple tuple, Expression selectCondition, SelectVisitor selVisitor) {
        selVisitor.updateCurrentTuple(tuple);
        selectCondition.accept(selVisitor);
        return selVisitor.getExpressionEvaluationResult();
    }

    /**
     * This function is called in the Join Operator to evaluate the join of two tuples for given join condition
     * and returns the expression evaluation result.
     * The Join Visitor carries with itself the information about the two tuples and their schemas
     *
     * @param tuple1 the left tuple of the join expression
     * @param tuple2 the right tuple of the join expression
     * @param joinCondition Expression which represents the condition on which the two tuples have to be tested
     * @param joinVisitor   Visitor generated by the JoinOperator which we use to evaluate result
     * @return The result of the given expression evaluation - True or False
     */
    public static boolean evaluateJoinExpression(Tuple tuple1, Tuple tuple2, Expression joinCondition, JoinVisitor joinVisitor) {
        joinVisitor.updateLeftRightTuples(tuple1, tuple2);
        joinCondition.accept(joinVisitor);
        return joinVisitor.getExpressionEvaluationResult();
    }

}