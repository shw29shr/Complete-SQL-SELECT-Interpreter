package logicaloperators;

import net.sf.jsqlparser.statement.select.SelectItem;
import planbuilders.PhysicalPlanBuilder;

import java.util.List;

/**
 * Class for Logical Project Operator
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class LogicalProjectOperator extends LogicalUnaryOperator {

    public List<SelectItem> selectItemList = null;

    /**
     * Constructor for Logical Project Operator
     * @param child The operator child
     * @param selectItemList the list containing select items
     * */
    public LogicalProjectOperator(LogicalOperator child, List<SelectItem> selectItemList) {
        super(child);
        this.selectItemList = selectItemList;
    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {
        pb.visit(this);
    }

    @Override
    public String print() {
        return String.format("Project%s", ((selectItemList == null) ? "[null]" : selectItemList.toString()));
    }

}
