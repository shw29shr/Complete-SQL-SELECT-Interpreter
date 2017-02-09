package operators;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * BinaryOperator abstract class for operators which will have two children
 * Currently only Join implements this
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public abstract class BinaryOperator extends Operator {
    public Operator leftchild, rightchild; // the binary operator has leftchild and rightchild child.

    /**
     * Constructor for Binary Operator
     * Also add schemas of both children in the corresponding schema object
     *
     * @param leftchild  Operator which is the left child
     * @param rightchild Operator which is the right child
     */
    protected BinaryOperator(Operator leftchild, Operator rightchild) {
        this.leftchild = leftchild;
        this.rightchild = rightchild;
        addSchemas();
    }

    /**
     * Method called by the constructor to add schemas of children to getSchema object
     */
    protected void addSchemas() {
        schema = new ArrayList<>(leftchild.getSchema());
        schema.addAll(rightchild.getSchema());
    }

    /**
     * Method to reset left and right child read heads
     */
    @Override
    public void reset() {
        leftchild.reset();
        rightchild.reset();
    }

    /**
     * Get the schema for the operator
     *
     * @return Schema object
     */
    public List<String> getSchema() {
        return schema;
    }

    @Override
    public void printQueryPlan(PrintStream printStream, int hyphen) {
        printHyphens(printStream, hyphen);
        printStream.println(print());
        leftchild.printQueryPlan(printStream, hyphen + 1);
        rightchild.printQueryPlan(printStream, hyphen + 1);
    }
}
