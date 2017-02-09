package helpers;

import catalog.DBCatalog;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The helper functions useful througout the project.
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */
public class Helpers {

    /**
     * Get the column tableName in a "Table.Column" string
     *
     * @param tableName the string
     * @return the column tableName
     */
    public static String getColumnName(String tableName) {
        return tableName.split("\\.")[1];
    }

    /**
     * Get the index of a tp's attribute.
     *
     * @param attribute   the attribute
     * @param schema the getSchema
     * @return the position of the attribute
     */
    public static int getAttributePosition(String attribute, List<String> schema) {
        int idx = schema.indexOf(attribute);
        if(idx != -1) return idx;

        for(int i = 0; i < schema.size(); i++) {
            String colName = getColumnName(schema.get(i));
            if(colName.equals(attribute))
                return i;
        }

        return -1;
    }


    /**
     * Analyze the tables mentioned in a binary expression.
     *
     * @param exp the binary expression
     * @return the list of tables mentioned; is null if
     * a table is referenced anonymously
     */
    public static List<String> getExpressionsList(Expression exp) {
        List<String> ret = new ArrayList<>();
        if(!(exp instanceof BinaryExpression))
            return ret;

        BinaryExpression be = (BinaryExpression) exp;
        Expression left = be.getLeftExpression();
        Expression right = be.getRightExpression();

        Column col;
        if(left instanceof Column) {
            col = (Column) left;
            if(col.getTable() == null) return null;
            ret.add(col.getTable().toString());
        }
        if(right instanceof Column) {
            col = (Column) right;
            if(col.getTable() == null) return null;
            ret.add(col.getTable().toString());
        }

        if(ret.size() == 2 && ret.get(0).equals(ret.get(1)))
            ret.remove(1);

        return ret;
    }

    public static boolean compareExpressions(Expression exp) {
        if(exp == null || !checkIfSelect(exp))
            return false;
        Expression left = ((BinaryExpression) exp).getLeftExpression();
        Expression right = ((BinaryExpression) exp).getRightExpression();
        return (left instanceof Column) && (right instanceof Column);
    }

    /**
     * Concatenate a group of expressions into a
     * long AND expression.
     *
     * @param exps the list of binary expressions
     * @return the final AND expression
     */
    public static Expression generateAnds(List<Expression> exps) {
        if(exps.isEmpty()) return null;
        Expression e = exps.get(0);
        for (int i = 1; i < exps.size(); i++) {
            if (exps.get(i) != null) {
                e = new AndExpression(e, exps.get(i));
            }
        }

        return e;
    }

    private static void updateLowHigh(Integer[] range, int val, boolean isLower, boolean inclusive, boolean opposite) {
        if (opposite) {
            updateLowHigh(range, val, !isLower, inclusive, false);
            return;
        }

        if(!inclusive)
            val = (isLower) ? val + 1 : val - 1;

        if(isLower)
            range[0] = (range[0] == null) ? val :
                    Math.max(range[0], val);
        else
            range[1] = (range[1] == null) ? val :
                    Math.min(range[1], val);
    }

    public static boolean checkIfSelect(Expression exp) {
        List<String> tmp = getExpressionsList(exp);
        return (tmp != null && tmp.size() == 1);
    }

    public static Integer[] getSelectLowHigh(Expression exp, String[] attribute) {
        if(!checkIfSelect(exp))
            throw new IllegalArgumentException();

        Expression left =
                ((BinaryExpression) exp).getLeftExpression();
        Expression right =
                ((BinaryExpression) exp).getRightExpression();

        Integer val;

        if(left instanceof Column) {
            attribute[0] = left.toString();
            val = Integer.parseInt(right.toString());
        } else {
            attribute[0] = right.toString();
            val = Integer.parseInt(left.toString());
        }

        boolean oppo = !(left instanceof Column);
        boolean inclusive = !(exp instanceof MinorThan) &&
                !(exp instanceof GreaterThan);
        boolean isUpper = (exp instanceof MinorThan ||
                exp instanceof MinorThanEquals ||
                exp instanceof EqualsTo);
        boolean isLower = (exp instanceof GreaterThan || exp instanceof GreaterThanEquals ||
                exp instanceof EqualsTo);

        if(!isLower && !isUpper)
            throw new IllegalArgumentException();

        Integer[] ret = new Integer[2];

        if(isLower)
            updateLowHigh(ret, val, true, inclusive, oppo);
        if(isUpper)
            updateLowHigh(ret, val, false, inclusive, oppo);

        return ret;
    }

    public static Expression generateCondition(String t, String column, int val, boolean isEqual, boolean isGreaterThanEqual) {
        Table table = new Table(null, t);
        Column c = new Column(table, column);
        LongValue v = new LongValue(String.valueOf(val));

        if (isEqual)
            return new EqualsTo(c, v);
        if (isGreaterThanEqual)
            return new GreaterThanEquals(c, v);
        return new MinorThanEquals(c, v);
    }


    public static class compareTables implements Comparator<String> {
        @Override
        public int compare(String tab1, String tab2) {
            int sizeTable1 = DBCatalog.getTableStatsStructure(tab1).getRowCount();
            int sizeTable2 = DBCatalog.getTableStatsStructure(tab2).getRowCount();
            return Integer.compare(sizeTable1, sizeTable2);
        }

    }

}

