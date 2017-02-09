package operators;
//a
import expvisitors.ExpressionEvaluator;
import expvisitors.JoinVisitor;
import tuplecomparators.SortMergeTupleComparator;
import models.Tuple;
import net.sf.jsqlparser.expression.Expression;

import java.util.List;

/**
 * Perform join, based on sorting the tables in question, this will be done based on the
 * user input
 *
 * @author Saarthak Chandra - sc2776
 * Shweta Shrivastava - ss3646
 * Vikas P Nelamangala - vpn6
 */

public class SortMergeJoinOperator extends JoinOperator {
    int partitionIndex = 0;
    int curRightIndex = 0;
    Expression joincondition;
    JoinVisitor joinVisitor;
    Tuple lefttuple;
    Tuple righttuple;
    SortMergeTupleComparator sortMergeTupleComp = null;
    List<Integer> leftAttributesOrder = null;
    List<Integer> rightAttributesOrder = null;


    /**
     *
     * @param leftChild
     * 					table in the left of the join condition
     * @param joinCondition
     * 					condition to join on
     * @param rightChild
     * 					table in the right of the join condition
     * @param leftAttributesOrder
     * 					order of integers
     * @param rightAttributesOrder
     * 					order of integers
     */
    public SortMergeJoinOperator(Operator leftChild, Expression joinCondition, Operator rightChild, List<Integer> leftAttributesOrder, List<Integer> rightAttributesOrder) {
        super(leftChild, rightChild, joinCondition);
        this.joincondition = joinCondition;
        this.leftAttributesOrder = leftAttributesOrder;
        this.rightAttributesOrder = rightAttributesOrder;
        sortMergeTupleComp = new SortMergeTupleComparator(leftAttributesOrder, rightAttributesOrder);
        this.joinVisitor = new JoinVisitor(leftChild.getSchema(), rightChild.getSchema());
        lefttuple = leftChild.getNextTuple();
        righttuple = rightChild.getNextTuple();
    }

    /**
     * Get the next tuple which satisfied the join condition
     */
    @Override
    public Tuple getNextTuple() {
        while(lefttuple != null && righttuple != null) {
            if(sortMergeTupleComp.compare(lefttuple, righttuple) < 0) {
                lefttuple = leftchild.getNextTuple();
                continue;
            }
            if(sortMergeTupleComp.compare(lefttuple, righttuple) > 0) {
                righttuple = rightchild.getNextTuple();
                curRightIndex++;
                partitionIndex = curRightIndex;
                continue;
            }
            Tuple resultTuple = null;
            if (joincondition == null || ExpressionEvaluator.evaluateJoinExpression(lefttuple, righttuple, joincondition, joinVisitor)) {
                resultTuple = concatenateTuples(lefttuple, righttuple);
            }
            righttuple = rightchild.getNextTuple();
            curRightIndex++;
            if(righttuple == null || sortMergeTupleComp.compare(lefttuple, righttuple) != 0) {
                lefttuple = leftchild.getNextTuple();
                ((SortOperator)rightchild).reset(partitionIndex);
                curRightIndex = partitionIndex;
                righttuple = rightchild.getNextTuple();
            }

            if(resultTuple != null)
                return resultTuple;
        }
        return null;
    }

    /**
     * We need not implement this function, since
     * getNextTuple gives us the next matching tuple
     */
    @Override
    protected void gotoNext() {
        throw new UnsupportedOperationException();
    }

    /**
     * For printing the operator in plan
     * @return String form for printing in query plan
     */
    @Override
    public String print() {
        String expression = (joincondition != null) ? joincondition.toString() : "";
        return ("SMJ[" + expression + "]");
    }

}
