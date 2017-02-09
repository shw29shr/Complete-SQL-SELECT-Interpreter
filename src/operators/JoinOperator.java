package operators;

import expvisitors.ExpressionEvaluator;
import expvisitors.JoinVisitor;
import helpers.Helpers;
import helpers.JoinHelper;
import models.Tuple;
import net.sf.jsqlparser.expression.Expression;

/**
 * Class for Join Operator Join Operator is created when query has more than one
 * table which are being joined by a given join condition Join Operator extends
 * the Binary operator class and has two child operators
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public abstract class JoinOperator extends BinaryOperator {

    Tuple leftTuple = null, rightTuple = null;
    Expression joinCondition = null;
    JoinVisitor joinVisitor = null;

    /**
     * Constructor for Join operator Takes the left and right child operators of
     * the join Extracts the next tuple and schema for either child operator and
     * stores it
     *
     * @param leftChild     The operator which represents the left child of the join
     *                      operator
     * @param joinCondition The expression which represents the Join condition
     * @param rightChild    The operator which represents the right child of the join
     *                      operator
     */
    protected JoinOperator(Operator leftChild, Operator rightChild, Expression joinCondition) {
        super(leftChild, rightChild);
        this.joinCondition = joinCondition;
        joinVisitor = new JoinVisitor(leftChild.getSchema(), rightChild.getSchema());
    }

    /**
     * Concatenate two tuples
     *
     * @param tuple1 First tuple for concatenation
     * @param tuple2 Second tuple for concatenation
     * @return The concatenated tuple
     */
    protected Tuple concatenateTuples(Tuple tuple1, Tuple tuple2) {
        int size1 = tuple1.getSize();
        int size2 = tuple2.getSize();
        int[] fields = new int[size1 + size2];
        int k = 0;
        int x = 0;
        while (x < size1) {
            fields[k] = tuple1.values[x];
            x++;
            k++;
        }
        int m = 0;
        while (m < size2) {
            fields[k] = tuple2.values[m];
            m++;
            k++;
        }
        return new Tuple(fields);
    }

    /**
     * Implementing the tuple-nested loop If join condition is null, simply take
     * cross product. Else evaluate the associated join condition first; if
     * true, only then concatenate the tuples
     *
     * @return The joined tuple
     */
    @Override
    public Tuple getNextTuple() {
        Tuple joinedTuple = null;
        while(leftTuple != null && rightTuple != null){
            if (joinCondition == null || ExpressionEvaluator.evaluateJoinExpression(leftTuple, rightTuple, joinCondition, joinVisitor))
                joinedTuple = concatenateTuples(leftTuple, rightTuple);
            gotoNext();
            if (joinedTuple != null)
                return joinedTuple;
        }
        return null;
    }
    
    protected abstract void gotoNext();


}
