package logicaloperators;

import helpers.Helpers;
import helpers.SelectExecutorHelper;
import net.sf.jsqlparser.expression.Expression;
import planbuilders.PhysicalPlanBuilder;
import unionfind.UnionFind;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implements Multi Child Join Operator and Used for cost calculations.
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */
public class LogicalMultiChildJoinOperator extends LogicalOperator {

    List<String> froms;
    HashMap<String, Expression> resultConditions;
    Expression exp;
    UnionFind uf;
    List<LogicalOperator> children;

    /**
     * Constructor for Multi Join Operator
     *
     *
     *
     * */
    public LogicalMultiChildJoinOperator(List<String> fromsList, List<LogicalOperator> tablesList,HashMap<String, Expression> result,UnionFind uf){
        this.froms = fromsList;
        children = tablesList;
        resultConditions = result;
        this.uf = uf;

        List<Expression> myList = new ArrayList<>();
        for(String s : result.keySet())
            myList.addAll(SelectExecutorHelper.decomposeAnds(result.get(s)));
        if(!myList.isEmpty())
            exp = Helpers.generateAnds(myList);
    }

    public List<LogicalOperator> getChildrenList() {
        return children;
    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("Join");
        if (exp != null)
            sb.append(String.format("[%s]", exp.toString()));
        else
            sb.append("[]");
        for (String s : uf.elementListAsString())
            sb.append('\n' + s);

        return sb.toString();
    }

    @Override
    public void printQueryPlan(PrintStream ps, int val) {
        prettyPrint(ps, val);
        ps.println(print());
        for (LogicalOperator lop : children)
            lop.printQueryPlan(ps, val + 1);
    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {

    }

}