package planbuilders;

import catalog.ConfigHandler;
import catalog.DBCatalog;
import catalog.Enums.JoinType;
import catalog.Enums.SortType;
import helpers.JoinHelper;
import indexhelpers.BPlusTreeIndexData;
import indexhelpers.BPlusTreeIndexHelper;
import logicaloperators.*;
import net.sf.jsqlparser.expression.Expression;
import operators.*;
import optimizers.SelectOptimizer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for Physical Plan Builder.
 * Implements visit methods of all Logical Operators.
 * Each visit method in turn provides concrete implementations of Operators.
 * Physical Operators are called inside Logical Operators.
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public class PhysicalPlanBuilder {

    private Operator physicalOperator = null;

    /**
     * Visitor function for scan operator
     * @param scanOperator logical scan operator
     */
    public void visit(LogicalScanOperator scanOperator) {
        physicalOperator = new ScanOperator(scanOperator.currentTable);
    }

    /**
     * Visitor function for join operator
     * @param ljo logical join operator
     */
    public void visit(LogicalJoinOperator ljo) {
        Operator leftChild, rightChild;

        //Evaluate left sub tree.
        physicalOperator = null;
        ljo.left.accept(this);
        leftChild = physicalOperator;

        //Now evaluate right sub tree
        physicalOperator = null;
        ljo.right.accept(this);
        rightChild = physicalOperator;

        ConfigHandler.joinType = JoinType.BlockNestedLoopJoin;

        List<Integer> outerBlockIndexes = new ArrayList<>();
        List<Integer> innerBlockIndexes = new ArrayList<>();
        Expression newExpression = JoinHelper.evaluateJoinConditions(ljo.exp, leftChild.getSchema(), rightChild.getSchema(), outerBlockIndexes, innerBlockIndexes);

        if(outerBlockIndexes.size() != innerBlockIndexes.size())
            throw new IllegalArgumentException();

        if(!outerBlockIndexes.isEmpty())
            ConfigHandler.joinType = JoinType.SortMergeJoin;

        switch(ConfigHandler.joinType) {
            case TupleNestedLoopJoin:
                physicalOperator = new TupleNestedJoinOperator(leftChild, rightChild, ljo.exp);
                break;
            case BlockNestedLoopJoin:
                physicalOperator = new BlockNestedJoinOperator(leftChild, rightChild, ljo.exp);
                break;
            case SortMergeJoin:
                if (!outerBlockIndexes.isEmpty()) {
                    ljo.exp = newExpression;
                    if (ConfigHandler.sortType == SortType.InMemorySort) {
                        leftChild = new InMemorySortOperator(leftChild, outerBlockIndexes);
                        rightChild = new InMemorySortOperator(rightChild, innerBlockIndexes);
                    } else {
                        leftChild = new ExternalSortOperator(leftChild, outerBlockIndexes);
                        rightChild = new ExternalSortOperator(rightChild, innerBlockIndexes);
                    }
                    physicalOperator = new SortMergeJoinOperator(leftChild,  ljo.exp, rightChild,outerBlockIndexes, innerBlockIndexes);
                } else {
                    System.out.println("No EqualsTo condition.. Performing Block Nested Join instead");
                    physicalOperator = new BlockNestedJoinOperator(leftChild, rightChild, ljo.exp);
                }
                break;
            default:
                System.out.println("No Valid Join Operator selected");
                throw new UnsupportedOperationException();

        }
    }

    /**
     * Visitor function for project operator
     * @param lpo logical project operator
     */
    public void visit(LogicalProjectOperator lpo) {
        physicalOperator = null;
        lpo.onlyChild.accept(this);
        Operator child = physicalOperator;
        physicalOperator = new ProjectOperator(physicalOperator, lpo.selectItemList);
    }


    /**
     * Visitor function for select operator
     * @param lso logical select operator
     */
    public void visit(LogicalSelectOperator lso) {
        //Every select operator has one child scan operator.
        //Call accept and visit on its child first.
        LogicalScanOperator child = (LogicalScanOperator) lso.onlyChild;
        ScanOperator indexScanOperator = null;
        if (ConfigHandler.useIndexForSelect) {
            String selectCurrentTable = getChildTableName(lso);
            String origTableName = DBCatalog.getActualTableName(selectCurrentTable);
            BPlusTreeIndexData indexData = SelectOptimizer.bestIndexPath(origTableName, lso.expression);
            boolean isIndexColumnPresent = (indexData != null);

            if (isIndexColumnPresent) {
                Integer[] indexRange = BPlusTreeIndexHelper.computeKeys(indexData.relationIndexColumn, lso.expression);
                String indexFilePath = DBCatalog.indexDirectory + indexData.relationName + '.' + indexData.relationIndexColumn;
                File idxFile = new File(indexFilePath);
                int columnIndex = DBCatalog.schemas.get(indexData.relationName).indexOf(indexData.relationIndexColumn);
                indexScanOperator = new IndexScanOperator(child.currentTable, columnIndex, indexRange[0], indexRange[1], indexData.isIndexClustered, idxFile);
            }
        }

        //In case of no index, create a simple Scan operator
        if (indexScanOperator == null)
            indexScanOperator = new ScanOperator(child.currentTable);

        physicalOperator = new SelectOperator(indexScanOperator, lso.expression);
    }

    /**
     * Returns the table name for the child of a select operator
     * @param lup logical unary operator or select
     */
    private String getChildTableName(LogicalUnaryOperator lup) {
        if (lup.onlyChild instanceof LogicalScanOperator) {
            return ((LogicalScanOperator) lup.onlyChild).currentTable.tableName;
        }
        return getChildTableName((LogicalUnaryOperator) lup.onlyChild);
    }

    /**
     * Visitor function for sort operator
     * @param lso logical sort operator
     */
    public void visit(LogicalSortOperator lso) {
        // Act on its child.
        physicalOperator = null;
        lso.onlyChild.accept(this);
        Operator child = physicalOperator;
        if (ConfigHandler.sortType == SortType.InMemorySort)
            physicalOperator = new InMemorySortOperator(physicalOperator, lso.orderElements);
        else
            physicalOperator = new ExternalSortOperator(physicalOperator, lso.orderElements);
    }

    /**
     * Visitor function for duplicate elimination operator
     * @param ldeo logical duplicate elimination operator
     */
    public void visit(LogicalDuplicateEliminationOperator ldeo) {
        physicalOperator = null;
        ldeo.onlyChild.accept(this);
        Operator child = physicalOperator;
        physicalOperator = new DuplicateEliminationOperator(physicalOperator);
    }

    /**
     * return the appropriate physical operator
     * @return physical operator instance
     */
    public Operator getPhysicalOperator() {
        return physicalOperator;
    }

}