package operators;

import expvisitors.ExpressionEvaluator;
import expvisitors.SelectVisitor;
import helpers.Helpers;
import helpers.SelectExecutorHelper;
import models.Tuple;
import net.sf.jsqlparser.expression.Expression;

/**
 * Class for Select Operator
 * The SelectOperator will only be created when query has a WHERE condition in it
 * SelectOperator extends the UnaryOperator and has one child which is the ScanOperator
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class SelectOperator extends UnaryOperator {

    private Expression selectCondition = null;
    private SelectVisitor selVisitor = null;

    /**
     * Constructor for Select operator
     *
     * @param child           The Scan operator child object of Select operator
     * @param selectCondition The Expression object which forms the select condition
     */
    public SelectOperator(ScanOperator child, Expression selectCondition) {
        super(child);
        this.selectCondition = selectCondition;
        selVisitor = new SelectVisitor(child.getSchema());
    }

    /**
     * Get the next tuple of the table which satisfies the given select condition
     * Create a select visitor and call the select evaluator function to get the result of the expression
     *
     * @return The next tuple which satisfies the select condition
     */
    @Override
    public Tuple getNextTuple() {
        Tuple currentTuple = null;
        while((currentTuple = child.getNextTuple()) != null) {
            if(selectCondition == null)
                return currentTuple;
            if(ExpressionEvaluator.evaluateSelectExpression(currentTuple, selectCondition, selVisitor))
                return currentTuple;
        }
        return null;
    }

    /**
     * For printing the operator in plan
     * @return String form for printing in query plan
     */
    @Override
    public String print() {
        String expression = (selectCondition != null) ? selectCondition.toString() : "";
        return ("Select[" + expression + "]");
    }
}

