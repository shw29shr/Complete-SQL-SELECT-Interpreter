package utils;

import catalog.DBCatalog;
import helpers.Helpers;
import helpers.SelectExecutorHelper;
import logicaloperators.*;
import models.Table;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import operators.Operator;
import planbuilders.PhysicalPlanBuilder;
import unionfind.UnionFind;
import unionfind.UnionFindElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class builds the tree, handles all the aliases as well. This class has a
 * constructor called for every single statement that is provided as input
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public class SelectExecutor {

    public FromItem from;
    public Expression where;
    public LogicalOperator logicRoot = null;
    public Operator root = null;
    private PlainSelect plainSelect;
    private Select selectStat;
    private Distinct distinctElements;
    private List<SelectItem> selectElements;
    private List<Join> joins;
    private List<OrderByElement> orderElements;
    private List<String> tableList = new ArrayList<>();
    private List<Expression> ands = null;
    private HashMap<String, List<Expression>> selectCondition = null, joinCondition = null;
    private HashMap<String, Expression> selectConditionList = null, joinConditionList = null;
    private HashMap<String, List<Expression>> oldJoinConditionsList;
    private HashMap<String, Expression> previousJoinCondition;
    private UnionFind uf = new UnionFind();

    /**
     * Select Executor constructor initialization. Parses the input query
     * statements one by one, builds the operator tree and evaluates
     *
     * @param statement Input query statement
     */
    public SelectExecutor(Statement statement) {
        selectStat = (Select) statement;
        plainSelect = (PlainSelect) selectStat.getSelectBody();
        initializeSelectBodyParsing();
        // Get all the tables in the query, that will be after "FROM"
        addTablesToTableList(from);
        // If we have joins, we get the tables out of them too...
        if(joins != null)
           addTablesInJoinCondition();
        Collections.sort(tableList, new Helpers.compareTables());
        addSelectJoinConditionsAfterTabSort();
        ands = SelectExecutorHelper.decomposeAnds(where);
        for(Expression exp : ands){
            List<String> tabs = Helpers.getExpressionsList(exp);
            int idx = maxIndex(tabs);
            if(tabs == null){
                joinCondition.get(tableList.get(tableList.size() - 1)).add(exp);
                return;
            }
            switch(tabs.size()){
                case 0:
                    selectCondition.get(tableList.get(idx)).add(exp);
                    break;
                case 1:
                    if(!SelectExecutorHelper.checkValidComparison(exp) || Helpers.compareExpressions(exp)){
                        selectCondition.get(tableList.get(idx)).add(exp);
                        break;
                    }
                    String[] attr = new String[1];
                    Integer[] range = Helpers.getSelectLowHigh(exp, attr);
                    if(attr[0] == null || attr[0].isEmpty())
                        throw new IllegalStateException();
                    UnionFindElement ufe = uf.find(attr[0]);
                    if(range[0] != null && range[0].equals(range[1]))
                        ufe.setEqualityConstraint(range[0]);
                    else {
                        if (range[0] != null)
                            ufe.setLowerboundConstraint(range[0]);
                        if (range[1] != null)
                            ufe.setUpperboundConstraint(range[1]);
                    }
                    break;
                case 2:
                    oldJoinConditionsList.get(tableList.get(idx)).add(exp);
                    if(exp instanceof EqualsTo){
                        BinaryExpression be = (BinaryExpression) exp;
                        uf.unionColumnElements(be.getLeftExpression().toString(),
                                be.getRightExpression().toString());
                    } else
                        joinCondition.get(tableList.get(idx)).add(exp);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        for(String attr : uf.unionFindElements.keySet()){
            UnionFindElement ufe = uf.find(attr);
            String tab = attr.split("\\.")[0];
            String col = attr.split("\\.")[1];
            List<Expression> lst = selectCondition.get(tab);
            Integer eq = ufe.equality;
            Integer lower = ufe.lowerbound;
            Integer upper = ufe.upperbound;
            if(eq != null)
                lst.add(Helpers.generateCondition(tab, col, eq, true, false));
            else {
                if(lower != null){
                    lst.add(Helpers.generateCondition(tab, col, lower, false, true));
                }
                if(upper != null){
                    lst.add(Helpers.generateCondition(tab, col, upper, false, false));
                }
            }
        }
        parseTableList();
        generateTreePreProcessor();
        generateTree();
        clearArraysForNextIteration();
    }

    /**
     * Parse the list of tables, from the select / join conditions
     */
    private void parseTableList() {
        selectConditionList = new HashMap<>();
        joinConditionList = new HashMap<>();
        previousJoinCondition = new HashMap<>();
        for(String table : tableList){
            selectConditionList.put(table, Helpers.generateAnds(selectCondition.get(table)));
            joinConditionList.put(table, Helpers.generateAnds(joinCondition.get(table)));
            previousJoinCondition.put(table, Helpers.generateAnds(oldJoinConditionsList.get(table)));
        }
    }

    /**
     * Here, we add all the tables into a list of tables called tableList, -
     * using the "from" elements of the query that jsqlParser returns We also
     * take care of adding aliases here
     *
     * @param fromItem List of fromItems from the query
     */
    private void addTablesToTableList(FromItem fromItem) {
        DBCatalog.aliases.clear();
        if(fromItem.getAlias() != null){
            DBCatalog.aliases.put(fromItem.getAlias(), SelectExecutorHelper.getSingleTableName(fromItem));
            tableList.add(fromItem.getAlias());
        } else
            tableList.add(fromItem.toString());
    }

    private void addTablesInJoinCondition() {
        for(Join join : joins){
            FromItem ri = join.getRightItem();
            if(ri.getAlias() != null){
                DBCatalog.aliases.put(ri.getAlias(), SelectExecutorHelper.getSingleTableName(ri));
                tableList.add(ri.getAlias());
            } else
                tableList.add(ri.toString());
        }
    }

    /**
     * Add the tables from the list after we sort the table list
     */
    private void addSelectJoinConditionsAfterTabSort() {
        selectCondition = new HashMap<>();
        joinCondition = new HashMap<>();
        oldJoinConditionsList = new HashMap<>();
        for(String tab : tableList){
            selectCondition.put(tab, new ArrayList<>());
            joinCondition.put(tab, new ArrayList<>());
            oldJoinConditionsList.put(tab, new ArrayList<>());
        }
    }

    /**
     * Return the table object according to its position in the query FROM
     * clause
     *
     * @param tableIndex the index
     * @return the table
     */
    private Table getTableObject(int tableIndex) {
        return DBCatalog.getTableObject(tableList.get(tableIndex));
    }

    /**
     * Get the highest index of all the tables in the FROM clause
     *
     * @param tables The tables present in the FROM clause
     * @return The maximum index
     */
    private int maxIndex(List<String> tables) {
        if(tables == null)
            return tableList.size() - 1;
        int pos = 0;
        for(String tab : tables){
            // pos = Math.max(pos, tableList.indexOf(tab));
            pos = Math.max(pos, tableList.indexOf(tab));
        }
        return pos;
    }

    /**
     * Given the position of a table, return the select condition associated
     * with it
     *
     * @param pos Position/index
     * @return The associated select condition
     */
    private Expression getSelectCondition(int pos) {
        return selectConditionList.get(tableList.get(pos));
    }

    /**
     * Given the position of a table, return the join condition associated with
     * it
     *
     * @param pos Position/index
     * @return The associated join condition
     */
    private Expression getPreviousJoinCondition(int pos) {
        return previousJoinCondition.get(tableList.get(pos));
    }

    private LogicalScanOperator getScanOperator(int pos) {
        return new LogicalScanOperator(getTableObject(pos));
    }

    /**
     * Method which implements the core logic of the SQL interpreter Builds the
     * operator tree in a bottom up fashion Inline comments explain the logic
     */
    private void generateTree() {
        // System.out.println("Inside Genreate Tree....");

        // base of our tree is the scan operator on a table

        LogicalOperator currentRoot = getSelectCondition(0) != null ? new LogicalSelectOperator(new LogicalScanOperator(getTableObject(0)), getSelectCondition(0)) : new LogicalScanOperator(getTableObject(0));

        // Now the bottom part of the tree is done, we have all scan operators
        // on tables,
        // and we have each scan operator's parent as a select(with a
        // condition), or a join(with a condition)
        currentRoot = processAllSelectAndJoins(currentRoot);

        // After all the selects, and join are over, we look for order
        // by,distinct


        boolean orderAllSelectedColumns = false;

        if(selectElements != null)
            currentRoot = new LogicalProjectOperator(currentRoot, selectElements);

        if(orderElements != null && !orderAllSelectedColumns)
            currentRoot = new LogicalSortOperator(currentRoot, orderElements);

        if(distinctElements != null) {
            if(orderElements == null)
                currentRoot = new LogicalSortOperator(currentRoot, new ArrayList<OrderByElement>());

            currentRoot = new LogicalDuplicateEliminationOperator(currentRoot);
        }

        PhysicalPlanBuilder ppb = new PhysicalPlanBuilder();
        currentRoot.accept(ppb);
        root = ppb.getPhysicalOperator();
    }


    private void generateTreePreProcessor() {
        List<LogicalOperator> tables = new ArrayList<>();
        for(int i = 0; i < tableList.size(); i++){
            LogicalOperator tmp = getScanOperator(i);
            if(getSelectCondition(i) != null)
                tmp = new LogicalSelectOperator(tmp, getSelectCondition(i));
            tables.add(tmp);
        }

        LogicalOperator currentRoot;
        if(tableList.size() > 1){
            currentRoot = new LogicalMultiChildJoinOperator(tableList, tables, joinConditionList, uf);
        }
        else {
            currentRoot = tables.get(0);
        }
        boolean orderAllSelectedColumns = false;
        if(selectElements != null)
            currentRoot = new LogicalProjectOperator(currentRoot, selectElements);

        if(orderElements != null && !orderAllSelectedColumns)
            currentRoot = new LogicalSortOperator(currentRoot, orderElements);

        // Tree root can be distinct , so we finally check for that

      /*
         * For distinct elements, we need to ensure they are sorted. If we have
       * already sorted(since we found a orderBy on the way up the tree), we
       * do not add SortOperator Else,we add a SortOperator first and then do
       * a duplicate
       */

        if(distinctElements != null){
            if(orderElements == null)
                currentRoot = new LogicalSortOperator(currentRoot, new ArrayList<OrderByElement>());
            currentRoot = new LogicalDuplicateEliminationOperator(currentRoot);
        }
        logicRoot = currentRoot;
    }

    /**
     * Add all the select/join operator nodes in the parse tree
     *
     * @param currentRoot Current temp root in the parse tree so far
     */
    private LogicalOperator processAllSelectAndJoins(LogicalOperator currentRoot) {
        for(int i = 1; i < tableList.size(); i++){
            LogicalOperator newOp = getScanOperator(i);
            if(getSelectCondition(i) != null)
                newOp = new LogicalSelectOperator(newOp, getSelectCondition(i));
            currentRoot = new LogicalJoinOperator(currentRoot, newOp, getPreviousJoinCondition(i));
        }
        return currentRoot;
    }

    /**
     * Use jsqlParser , to get out all the parts of the current statement being
     * executed Also initialize our select and join condition HashMaps
     */
    private void initializeSelectBodyParsing() {
        distinctElements = plainSelect.getDistinct();
        selectElements = plainSelect.getSelectItems();
        from = plainSelect.getFromItem();
        joins = plainSelect.getJoins();
        where = plainSelect.getWhere();
        orderElements = (ArrayList<OrderByElement>) plainSelect.getOrderByElements();

    }

    /**
     * Clear up the list of arrays, so we start afresh for the next statement
     * that is passed in
     */
    private void clearArraysForNextIteration() {
        selectCondition.clear();
        joinCondition.clear();
        oldJoinConditionsList.clear();
        selectConditionList.clear();
        joinConditionList.clear();
        previousJoinCondition.clear();
    }

}
