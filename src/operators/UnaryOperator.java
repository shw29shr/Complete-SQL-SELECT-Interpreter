package operators;

import java.io.PrintStream;
import java.util.List;

/**
 * UnaryOperator abstract class for operators which will have one child
 * Currently it is implemented by the Select, Sort, Project, DuplicateElimination and HashDuplicateElimination Operators
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public abstract class UnaryOperator extends Operator {

    public Operator child;

    /**
     * Constructor for Unary Operator
     *
     * @param child Child operator
     */
    protected UnaryOperator(Operator child) {
        this.child = child;
    }

    /**
     * Method to reset the child read head
     */
    @Override
    public void reset() {
        child.reset();
    }

    /**
     * Get the schema
     *
     * @return Schema object
     */
    public List<String> getSchema() {
        if (this.schema != null)
            return this.schema;
        return child.getSchema();
    }

    /**
     * For printing the operator in plan
     * @param printStream the print stream
     * @param hyphen the value of num of hyphens
     */
    @Override
    public void printQueryPlan(PrintStream printStream, int hyphen) {
        printHyphens(printStream, hyphen);
        printStream.println(print());
        child.printQueryPlan(printStream, hyphen + 1);
    }
}
