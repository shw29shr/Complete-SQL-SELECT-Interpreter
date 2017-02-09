package unionfind;

/**
 * This class defines the UnionFindElement which represents the
 * atomic element used inside UnionFind data structure
 * Every UnionFindElement has three attributes representing constraints
 * - lower bound, upper bound and equality
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */

public class UnionFindElement {

    public Integer lowerbound;
    public Integer equality;
    public Integer upperbound;

    /**
     * Constructor - initializes all constraints to null
     */
    public UnionFindElement() {
        this.lowerbound = null;
        this.upperbound = null;
        this.equality = null;
    }

    /**
     * Set the equality constraint
     * @param eqvalue Equality value
     */
    public void setEqualityConstraint(Integer eqvalue) {
        if (eqvalue == null)
            return;
        equality = eqvalue;
    }

    /**
     * Set the lowerbound constraint
     * based on the previously assigned value
     * @param lbvalue lowerbound value
     */
    public void setLowerboundConstraint(Integer lbvalue) {
        if (lbvalue != null) {
            if ((lowerbound != null && lbvalue > lowerbound) || lowerbound == null) {
                lowerbound = lbvalue;
            }
        }
    }

    /**
     * Set the upperbound constraint
     * based on the previously assigned value
     * @param ubvalue upperbound value
     *
     */
    public void setUpperboundConstraint(Integer ubvalue) {
        if (ubvalue != null) {
            if ((upperbound != null && ubvalue < upperbound) || upperbound == null) {
                upperbound = ubvalue;
            }
        }
    }

    /**
     * Set another union find element's constraints to this
     * @param ufe union find element whose constraints should be set to this
     */
    public void setAllConstraints(UnionFindElement ufe) {
        if (ufe != null) {
            this.setLowerboundConstraint(ufe.lowerbound);
            this.setUpperboundConstraint(ufe.upperbound);
            this.setEqualityConstraint(ufe.equality);
        }
    }

}
