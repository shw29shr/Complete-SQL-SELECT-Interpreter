package optimizers;

import operators.Operator;

import java.util.List;

/**
 * This class is used to hold the join optimal plan slist
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */
public class OptimalJoinPlan {
    public double optimalCost;
    public List<Operator> optimalPlanList;

    /**
     * Constructor - intialized cost to MAX and plan to NULL
     */
    public OptimalJoinPlan() {
        this.optimalCost = Double.MAX_VALUE;
        this.optimalPlanList = null;
    }
}
