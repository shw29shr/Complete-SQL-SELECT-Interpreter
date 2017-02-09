package costcalculator;

import catalog.DBCatalog;
import catalog.TableStatsStructure;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.util.HashMap;

/**
 * This class is used to calculate the VValues
 * for Join optimization
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public class VValueCalculator {

    private static HashMap<String, TableStatsStructure> tableStatsStructure = null;
    private static HashMap<String, Integer> tableAttributeToVValue = null;

    /**
     * This is the main method which does the VValue calculation for each table in the FROM clause
     * It does the calculations separately for base tables (having full scans on them) and the tables
     * resulting as a result of selection and join
     *
     * @param baseTable            the name of the base table
     * @param selectionResultTable the selection expression list which gives the table result of that selection
     * @param joinResultTable      the join expression list which gives the table result of the join
     * @return The hashmap containing the table to its VValue mapping
     */
    public static HashMap<String, Integer> calculateVValue(String[] baseTable, Expression[] selectionResultTable, Expression[] joinResultTable) {
        tableAttributeToVValue = new HashMap<>();
        tableStatsStructure = DBCatalog.tableStatsStructure;

        /* VValue calculation for Base Table */
        for(String tableNameInFrom : baseTable) {
            String tableName = tableNameInFrom.split("\\,")[0];
            String columnName = tableNameInFrom.split("\\.")[1];
            int[] columnValueRange = tableStatsStructure.get(tableName).getRange(columnName);
            tableAttributeToVValue.put(tableName, columnValueRange[1] - columnValueRange[0] + 1);
        }

        /* VValue calculation for Select Result Table */
        for(Expression selectExpression : selectionResultTable) {
            Column selectLeftColumn;
            int rightValue = 0;

            //Calculation for GreaterThan in select condition
            if(selectExpression instanceof GreaterThan) {
                if(((GreaterThan) selectExpression).getLeftExpression() instanceof Column) {
                    selectLeftColumn = (Column) ((GreaterThan) selectExpression).getLeftExpression();
                    LongValue value = (LongValue) ((GreaterThan) selectExpression).getRightExpression();
                    rightValue = (int) value.getValue();
                } else {
                    selectLeftColumn = (Column) ((GreaterThan) selectExpression).getRightExpression();
                    LongValue value = (LongValue) ((GreaterThan) selectExpression).getLeftExpression();
                    rightValue = (int) value.getValue();
                }

                String tableName = selectLeftColumn.toString().split("\\.")[0];
                String columnName = selectLeftColumn.toString().split("\\.")[1];
                int[] columnValueRange = tableStatsStructure.get(tableName).getRange(columnName);

                double reductionFactor = ((double) (columnValueRange[1] - rightValue)) / ((double) (columnValueRange[1] - columnValueRange[0]));

                if(tableAttributeToVValue.containsKey(tableName)) {
                    tableAttributeToVValue.put(tableName, Math.min(tableAttributeToVValue.get(tableName), (int) (reductionFactor * (columnValueRange[1] - columnValueRange[0] + 1))));
                } else {
                    tableAttributeToVValue.put(tableName, (int) (reductionFactor * (columnValueRange[1] - columnValueRange[0] + 1)));
                }
            }

            //Calculation for GreaterThanEquals in select condition
            else if(selectExpression instanceof GreaterThanEquals) {
                if(((GreaterThanEquals) selectExpression).getLeftExpression() instanceof Column) {
                    selectLeftColumn = (Column) ((GreaterThanEquals) selectExpression).getLeftExpression();
                    LongValue value = (LongValue) ((GreaterThanEquals) selectExpression).getRightExpression();
                    rightValue = (int) value.getValue();
                } else {
                    selectLeftColumn = (Column) ((GreaterThanEquals) selectExpression).getRightExpression();
                    LongValue value = (LongValue) ((GreaterThanEquals) selectExpression).getLeftExpression();
                    rightValue = (int) value.getValue();
                }
                String tableName = selectLeftColumn.toString().split("\\.")[0];
                String columnName = selectLeftColumn.toString().split("\\.")[1];
                int[] columnValueRange = tableStatsStructure.get(tableName).getRange(columnName);

                double reductionFactor = ((double) (columnValueRange[1] - rightValue)) / ((double) (columnValueRange[1] - columnValueRange[0]));

                if (tableAttributeToVValue.containsKey(tableName)) {
                    tableAttributeToVValue.put(tableName, Math.min(tableAttributeToVValue.get(tableName), (int) (reductionFactor * (columnValueRange[1] - columnValueRange[0] + 1))));
                } else {
                    tableAttributeToVValue.put(tableName, (int) (reductionFactor * (columnValueRange[1] - columnValueRange[0] + 1)));
                }
            }

            //Calculation for MinorThan in select condition
            else if(selectExpression instanceof MinorThan) {
                if(((MinorThan) selectExpression).getLeftExpression() instanceof Column) {
                    selectLeftColumn = (Column) ((MinorThan) selectExpression).getLeftExpression();
                    LongValue value = (LongValue) ((MinorThan) selectExpression).getRightExpression();
                    rightValue = (int) value.getValue();
                } else {
                    selectLeftColumn = (Column) ((MinorThan) selectExpression).getRightExpression();
                    LongValue value = (LongValue) ((MinorThan) selectExpression).getLeftExpression();
                    rightValue = (int) value.getValue();
                }
                String tableName = selectLeftColumn.toString().split("\\.")[0];
                String columnName = selectLeftColumn.toString().split("\\.")[1];
                int[] columnValueRange = tableStatsStructure.get(tableName).getRange(columnName);

                double reductionFactor = ((double) (columnValueRange[1] - rightValue)) / ((double) (columnValueRange[1] - columnValueRange[0]));

                if (tableAttributeToVValue.containsKey(tableName)) {
                    tableAttributeToVValue.put(tableName, Math.min(tableAttributeToVValue.get(tableName), (int) (reductionFactor * (columnValueRange[1] - columnValueRange[0] + 1))));
                } else {
                    tableAttributeToVValue.put(tableName, (int) (reductionFactor * (columnValueRange[1] - columnValueRange[0] + 1)));
                }
            }

            //Calculation for MinorThanEquals in select condition
            else if(selectExpression instanceof MinorThanEquals) {
                if(((MinorThanEquals) selectExpression).getLeftExpression() instanceof Column) {
                    selectLeftColumn = (Column) ((MinorThanEquals) selectExpression).getLeftExpression();
                    LongValue value = (LongValue) ((MinorThanEquals) selectExpression).getRightExpression();
                    rightValue = (int) value.getValue();
                } else {
                    selectLeftColumn = (Column) ((MinorThanEquals) selectExpression).getRightExpression();
                    LongValue value = (LongValue) ((MinorThanEquals) selectExpression).getLeftExpression();
                    rightValue = (int) value.getValue();
                }
                String tableName = selectLeftColumn.toString().split("\\.")[0];
                String columnName = selectLeftColumn.toString().split("\\.")[1];
                int[] columnValueRange = tableStatsStructure.get(tableName).getRange(columnName);

                double reductionFactor = ((double) (columnValueRange[1] - rightValue)) / ((double) (columnValueRange[1] - columnValueRange[0]));

                if(tableAttributeToVValue.containsKey(tableName)) {
                    tableAttributeToVValue.put(tableName, Math.min(tableAttributeToVValue.get(tableName), (int) (reductionFactor * (columnValueRange[1] - columnValueRange[0] + 1))));
                } else {
                    tableAttributeToVValue.put(tableName, (int) (reductionFactor * (columnValueRange[1] - columnValueRange[0] + 1)));
                }

            }
        }

        /* VValue calculation for Join Result Table */
        for (Expression joinExpression : joinResultTable) {
            Column joinLeftColumn = (Column) (((EqualsTo) joinExpression).getLeftExpression());
            Column joinRightColumn = (Column) (((EqualsTo) joinExpression).getRightExpression());

            if(joinLeftColumn != null && joinRightColumn != null) {
                String leftTableName = joinLeftColumn.toString().split("\\.")[0];
                String rightTableName = joinRightColumn.toString().split("\\.")[0];
                if(!tableAttributeToVValue.containsKey(leftTableName) && tableAttributeToVValue.containsKey(rightTableName)) {
                    tableAttributeToVValue.put(leftTableName, tableAttributeToVValue.get(rightTableName));
                } else if(!tableAttributeToVValue.containsKey(rightTableName) && tableAttributeToVValue.containsKey(leftTableName)) {
                    tableAttributeToVValue.put(rightTableName, tableAttributeToVValue.get(leftTableName));
                } else if(tableAttributeToVValue.containsKey(rightTableName) && tableAttributeToVValue.containsKey(leftTableName)) {
                    int minVValue = Math.min(tableAttributeToVValue.get(leftTableName), tableAttributeToVValue.get(rightTableName));
                    tableAttributeToVValue.put(leftTableName, minVValue);
                    tableAttributeToVValue.put(rightTableName, minVValue);
                } else {
                    int[] leftColumnRange = tableStatsStructure.get(leftTableName).getRange(joinLeftColumn.toString().split("\\.")[1]);
                    int[] rightColumnRange = tableStatsStructure.get(rightTableName).getRange(joinRightColumn.toString().split("\\.")[1]);
                    int leftValue = leftColumnRange[1] - leftColumnRange[0] + 1;
                    int rightValue = rightColumnRange[1] - rightColumnRange[0] + 1;
                    tableAttributeToVValue.put(leftTableName, Math.min(leftValue, rightValue));
                    tableAttributeToVValue.put(rightTableName, Math.min(leftValue, rightValue));
                }
            }
        }
        System.out.print("VValue calculation done\n");
        return tableAttributeToVValue;
    }
}
