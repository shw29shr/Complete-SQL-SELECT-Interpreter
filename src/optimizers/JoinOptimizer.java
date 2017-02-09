package optimizers;

import operators.Operator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import costcalculator.VValueCalculator;

/**
 * This class performs the join optimization to build the left deep tree
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */

public class JoinOptimizer {

    /**
     * Given a list of join operators,
     * identify all subsets present in the list
     * to perform cost calculations
     * @return Set containing all subsets of operators
     */
    private static Set<Set<Operator>> identifySubsets(List<Operator> joinsList, int joinSize) {
        Set<Set<Operator>> originalSet = new HashSet<Set<Operator>>();
        originalSet.add(new HashSet<Operator>());
        for (int i = 0; i < joinSize; i++) {
            Set<Set<Operator>> newSet = new HashSet<Set<Operator>>();
            for (Set<Operator> ss : originalSet) {
                for (Operator jop : joinsList) {
                    Set<Operator> newSubset = (Set<Operator>) (((HashSet<Operator>) ss).clone());
                    if (newSubset.add(jop)) newSet.add(newSubset);
                }
            }
            originalSet = newSet;
        }
        return originalSet;
    }

    /**
     * Return optimized Join order
     * @param joinsList List of join operators to be ordered.
     */
    public static void optimizeJoinOrder(List<Operator> joinsList) {
        for (int i = 0; i < joinsList.size(); i++){
            for(Set<Operator> joinOperatorSet : identifySubsets(joinsList,joinsList.size())){
                OptimalJoinPlan joinOptimizedPlan = new OptimalJoinPlan();
            }
        }
    }

}
