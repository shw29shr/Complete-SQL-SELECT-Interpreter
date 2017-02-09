package expvisitors;

import helpers.AttributeMapper;
import models.Tuple;
import net.sf.jsqlparser.schema.Column;

import java.util.List;

/**
 * This is an ExpressionVisitor for the join condition evaluations
 * The Join Expression Visitor extends the ExpressionVisitorWrapper
 * Every JoinVisitor carries with itself the two tuples which are to be joined
 * and their corresponding schemas
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class JoinVisitor extends ExpressionVisitorWrapper {

    private Tuple tuple1 = null, tuple2 = null;
    private List<String> schema1 = null, schema2 = null;

    /**
     * Constructor for JoinVisitor
     *
     * @param schema1 Schema for Tuple1
     * @param schema2 Schema for Tuple2
     */
    public JoinVisitor(List<String> schema1, List<String> schema2) {
        this.schema1 = schema1;
        this.schema2 = schema2;
    }

    /**
     * Set the tuples pm both sides of the expression
     *
     * @param tuple1 left tuple
     * @param tuple2 right tuple
     */
    public void updateLeftRightTuples(Tuple tuple1, Tuple tuple2) {
        this.tuple1 = tuple1;
        this.tuple2 = tuple2;
    }

    /**
     * Visit function for a column
     * Visit the column and extract the actual value for the given attribute
     *
     * @param c Column object
     */
    @Override
    public void visit(Column c) {
        Long tempValue;
        expressionValue = ((tempValue = AttributeMapper.getColumnActualValue(tuple1, schema1, c.toString())) == null) ? (AttributeMapper.getColumnActualValue(tuple2, schema2, c.toString())) : tempValue;
    }
}
