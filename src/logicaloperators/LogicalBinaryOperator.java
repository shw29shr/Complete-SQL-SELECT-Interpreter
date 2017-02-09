package logicaloperators;

import java.io.IOException;

/**
 * Logical Binary Operator abstract class for operators which will have two children
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public abstract class LogicalBinaryOperator extends LogicalOperator {

    public LogicalOperator left = null, right = null;

    /**
     * Constructor for Logical Binary Operator
     * @param leftChild the left child.
     * @param rightChild the right child.
     *
     * */
    protected LogicalBinaryOperator(LogicalOperator leftChild, LogicalOperator rightChild) {
        this.left = leftChild;
        this.right = rightChild;
    }

}
