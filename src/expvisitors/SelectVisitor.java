package expvisitors;

import helpers.AttributeMapper;
import models.Tuple;
import net.sf.jsqlparser.schema.Column;

import java.util.List;

/**
 * This is an ExpressionVisitor for the select condition evaluations
 * The Select Expression Visitor extends the ExpressionVisitorWrapper
 * Every SelectVisitor carries with itself the tuple
 * and its corresponding schemas
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public class SelectVisitor extends ExpressionVisitorWrapper {

    private Tuple t = null;
    private List<String> schema = null;

    /**
     * Constructor for Select Visitor
     * @param schema schema of the current tuple
     */
    public SelectVisitor(List<String> schema){
        this.schema = schema;
    }

    /**
     * Update the current tuple being operated on
     * @param t current tuple
     */
    public void updateCurrentTuple(Tuple t) {
        this.t = t;
    }

    /**
     * We implement the visit method for Column expression type separately for select and join
     * as we will need to handle them both separately
     * Here we map the column name to the actual value from the tuple using the tuple schema
     * and assign it to the expression value
     *
     * @param arg0 Column object
     */
    @Override
    public void visit(Column arg0) {
        expressionValue = AttributeMapper.getColumnActualValue(t, schema, arg0.toString());
    }

}
