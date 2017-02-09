package operators;

import net.sf.jsqlparser.expression.Expression;

/**
 * This is an implementation of tuple nested loop join,
 * where tuples are compared iteratively
 *
 * @author Saarthak Chandra - sc2776
 * Shweta Shrivastava - ss3646
 * Vikas P Nelamangala - vpn6
 */

public class TupleNestedJoinOperator extends JoinOperator {

    /**
     * Construct a join operator.
     *
     * @param leftChild  the leftchild operator
     * @param rightChild the rightchild operator
     * @param joinCondition   the join condition
     */
    public TupleNestedJoinOperator(Operator leftChild, Operator rightChild, Expression joinCondition) {
        super(leftChild, rightChild, joinCondition);
        leftTuple = leftChild.getNextTuple();
        rightTuple = rightChild.getNextTuple();
    }

    /**
     * A concrete implementation, which when dictates logic to
     * get tuples for the join.
     */
    @Override
    protected void gotoNext() {
        // Now scan the table on the right iteratively by keeping the left
        // table fixed.
        // Reset the table on the right as soon as the end is reached
        if (leftTuple != null)
        // Keep checking the RHS table.
        {
            if (rightTuple != null)
                rightTuple = rightchild.getNextTuple();

            if (rightTuple == null) {
                leftTuple = leftchild.getNextTuple();
                rightchild.reset();
                rightTuple = rightchild.getNextTuple();
            }
        }
    }

    /**
     * For printing the operator in plan
     * @return String form for printing in query plan
     */
    @Override
    public String print() {
        String expression = (joinCondition != null) ? joinCondition.toString() : "";
        return ("TNLJ[" + expression + "]");
    }

}

