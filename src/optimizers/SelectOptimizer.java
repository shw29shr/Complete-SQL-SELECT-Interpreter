package optimizers;

import catalog.DBCatalog;
import helpers.Helpers;
import helpers.SelectExecutorHelper;
import indexhelpers.BPlusTreeIndexData;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import utils.PropertyFileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class optimizes the selections
 * It identifies the places where indexes should be used
 * by calculating the cost down each index path
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 *
 */
public class SelectOptimizer {

    public static PropertyFileReader reader = PropertyFileReader.getInstance();
	private static HashMap<String, Integer[]> columnToRangeMapping;
	private static List<String> columnName;
	private static List<Double> selectionPathCost;
	private static double fullScanCost;

    /**
     * Constructor
     */
    public SelectOptimizer(){
		columnToRangeMapping = new HashMap<>();
		columnName = new ArrayList<>();
		selectionPathCost = new ArrayList<>();
		fullScanCost = -1;
	}

    /**
     * This function analyzes all possible indexes of the columns on the given relation
     * and finds the best index path with the minimum cost
     * @param tableName Name of the table
     * @param selectExpression select expression
     * @return
     */
	public static BPlusTreeIndexData bestIndexPath(String tableName, Expression selectExpression) {
		columnToRangeMapping = new HashMap<>();
		columnName = new ArrayList<>();
        selectionPathCost = new ArrayList<>();
        fullScanCost = -1;
		List<Expression> selectAndExpressionList = SelectExecutorHelper.decomposeAnds(selectExpression);
		for(Expression expr : selectAndExpressionList) {
			Expression leftExpression = ((BinaryExpression) expr).getLeftExpression();
			Expression rightExpression = ((BinaryExpression) expr).getRightExpression();
			String tableColumn = "";
			if(leftExpression instanceof Column && rightExpression instanceof LongValue){
				tableColumn = leftExpression.toString().split("\\.")[1];
			}
            else if(rightExpression instanceof Column && leftExpression instanceof LongValue){
				tableColumn = rightExpression.toString().split("\\.")[1];
			}
            else if (rightExpression instanceof Column && leftExpression instanceof Column){
                fullScanCost = (DBCatalog.tableStatsStructure.get(tableName).getRowCount() * DBCatalog.tableStatsStructure.get(tableName).getColumnCount() * 4) / 4096;
				continue;
			}
            else if(expr instanceof EqualsTo){
                fullScanCost = (DBCatalog.tableStatsStructure.get(tableName).getRowCount() * DBCatalog.tableStatsStructure.get(tableName).getColumnCount() * 4) / 4096;
				continue;
			}
			String[] strings = {tableColumn};
			Integer[] selRange = Helpers.getSelectLowHigh(expr, strings);
			setColumnRange(tableColumn, selRange);
		}
		double tupleSize = DBCatalog.tableStatsStructure.get(tableName).getRowCount() * DBCatalog.tableStatsStructure.get(tableName).getColumnCount() * 4;
		double pageNumber = tupleSize / Integer.valueOf(reader.getProperty("pageSize"));
		Set<Entry<String, Integer[]>> columnToIndexSet = columnToRangeMapping.entrySet();
		for(Entry<String, Integer[]> entry : columnToIndexSet){
			BPlusTreeIndexData tableIndexData = DBCatalog.bPlusTreeIndexManager.getIndexInformation(tableName, entry.getKey());
			double currentIndexCost;
			if (tableIndexData == null) {
				currentIndexCost = pageNumber;
			}
            else {
				int[] tableRange = DBCatalog.tableStatsStructure.get(tableName).getRange(entry.getKey());
				double range = tableRange[1] - tableRange[0];
				double lowValue = 0;
				double highValue = 0;
				if (entry.getValue()[0] == null) {
					highValue = tableRange[1];
				}
				if (entry.getValue()[1] == null) {
					lowValue = tableRange[0];
				}

                //Reduction factor calculations follow
				double reductionFactor = (highValue - lowValue) / range;
				if (tableIndexData.isIndexClustered) {
					currentIndexCost = 3 + pageNumber * reductionFactor;
				}
                else {
					double leafNum = tableIndexData.getLeafNodeCount();
					currentIndexCost = 3 + (leafNum + DBCatalog.tableStatsStructure.get(tableName).getRowCount()) * reductionFactor;
				}
			}
			columnName.add(entry.getKey());
            selectionPathCost.add(currentIndexCost);
		}

		if(fullScanCost != -1){
			if(selectionPathCost.size() != 0){
				for(double currentPathCost : selectionPathCost){
					if (fullScanCost < currentPathCost){
						return null;
					}
				}
			}
		}
		if (selectionPathCost.size() == 0) {
			return null;
		}
		if (columnName.size() != selectionPathCost.size()) {
			throw new IllegalArgumentException();
		}
		int lowestIndex = 0;
		double minimumCost = selectionPathCost.get(0);
		for (int i = 1; i < selectionPathCost.size(); i++) {
			if (minimumCost > selectionPathCost.get(i)) {
				minimumCost = selectionPathCost.get(i);
				lowestIndex = i;
			}
		}
		return DBCatalog.bPlusTreeIndexManager.getIndexInformation(tableName, columnName.get(lowestIndex));
	}

    /**
     * This function sets the range of values for each column
     * @param columnName Name of the column
     * @param columnRange Current range of the column
     */
	private static void setColumnRange(String columnName, Integer[] columnRange) {
		if(!columnToRangeMapping.containsKey(columnName)){
			Integer[] range = new Integer[2];
			for(int i = 0; i < columnRange.length; i++){
				range[i] = columnRange[i];
			}
			columnToRangeMapping.put(columnName, range);
		}
        else {
			Integer[] previousColumnRange = columnToRangeMapping.get(columnName);
			if (columnRange[0] != null) {
				if (previousColumnRange[0] == null) {
					previousColumnRange[0] = columnRange[0];
				}
                else {
					previousColumnRange[0] = Math.max(previousColumnRange[0], columnRange[0]);
					columnToRangeMapping.put(columnName, previousColumnRange);
				}
			}
			if (columnRange[1] != null) {
				if (previousColumnRange[1] == null) {
					previousColumnRange[1] = columnRange[1];
				} else {
					previousColumnRange[1] = Math.min(previousColumnRange[1], columnRange[1]);
					columnToRangeMapping.put(columnName, previousColumnRange);
				}
			}
		}
	}
}
