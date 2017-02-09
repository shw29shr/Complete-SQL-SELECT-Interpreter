package operators;

import helpers.AttributeMapper;
import models.Tuple;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for Project Operator
 * The ProjectOperator will be created when query does NOT have a SELECT * but instead chooses some specific columns
 * ProjectOperator extends the UnaryOperator and has one child which maybe the ScanOperator or SelectOperator
 * If query has a WHERE clause, the SelectOperator is the child. If it doesn't, ScanOperator is the child
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public class ProjectOperator extends UnaryOperator {

    List<String> childSchema = null;
    private List<SelectItem> selectList = new ArrayList<>();

    /**
     * Constructor for Project Operator. It takes a list of SelectItem and child operator
     * The SelectItem can be -
     * SelectExpressionItem (SELECT A as X, SELECT S.A from S)
     * AllColumns (SELECT *)
     *
     * @paramList<SelectItem> selectItemList List of columns that need to be projected
     * @param child          Child operator
     */
    public ProjectOperator(Operator child, List<SelectItem> selectItemList) {
        super(child);
        childSchema = child.getSchema();
        this.selectList = selectItemList;
        extractAllColumnsofTable(selectItemList);
    }

    /**
     * Generates the list of projected columns from the given selectItemList
     * @param selectItemList List of selectItems from the input query
     */
    private void extractAllColumnsofTable(List<SelectItem> selectItemList) {
        List<String> tempChildSchema = new ArrayList<>();
        for(SelectItem selectItem : selectItemList) {
            if(selectItem instanceof AllColumns) {
                schema = childSchema;
                return;
            }
            else { // Not a SELECT * operation
                Column projectedColumn = (Column)((SelectExpressionItem) selectItem).getExpression();
                if(projectedColumn.getTable() != null) {
                    String table = projectedColumn.getTable().getName();
                    tempChildSchema.add(table + '.' + projectedColumn.getColumnName());
                }
                else {
                    String projectedColumnName = projectedColumn.getColumnName();
                    for(String childSchemaColumn : childSchema) {
                        if(childSchemaColumn.split("\\.")[1].equals(projectedColumnName)) {
                            tempChildSchema.add(childSchemaColumn);
                            break;
                        }
                    }
                }
            }
        }
        schema = tempChildSchema;
    }

    /**
     * Read the next tuple via the getNextTuple method of the child
     * Then generate a tuple which projects the desired columns from the schema
     *
     * @return The projected tuple
     */
    @Override
    public Tuple getNextTuple() {
        Tuple childNextTuple = child.getNextTuple();
        if(childNextTuple == null)
            return childNextTuple;
        int k = 0;
        int[] projectedAttributes = new int[schema.size()];
        for(String schemaCol : schema) {
            long projectedColumnValue = AttributeMapper.getColumnActualValue(childNextTuple, child.getSchema(), schemaCol);
            projectedAttributes[k++] = (int) projectedColumnValue;
        }
        return new Tuple(projectedAttributes);
    }

    /**
     * For printing the operator in plan
     * @return String form for printing in query plan
     */
    @Override
    public String print() {
        return String.format("Project%s", ((selectList == null) ? "[null]" : selectList.toString()));
    }


}




