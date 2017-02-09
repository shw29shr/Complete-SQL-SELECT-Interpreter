package operators;

import catalog.ConfigHandler;
import models.Tuple;
import net.sf.jsqlparser.expression.Expression;
import utils.PropertyFileReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */
public class BlockNestedJoinOperator extends JoinOperator {

	private PropertyFileReader reader = PropertyFileReader.getInstance();
	private List<Tuple> joinOuterBlock = null;
	private int tuplesPerPage = -1;
	private int currentOuterIdx = 0;

    /**
     * Constructor for BNLJ
     * @param leftChild left child of join operator
     * @param rightChild right child of join operator
     * @param joinCondition join condition
     */
	public BlockNestedJoinOperator(Operator leftChild, Operator rightChild, Expression joinCondition) {
		super(leftChild, rightChild, joinCondition);
		int tupleSize = Integer.parseInt(reader.getProperty("attributeSize")) * leftChild.getSchema().size();
		tuplesPerPage = ConfigHandler.joinBufferPagesNumber * (Integer.parseInt(reader.getProperty("pageSize")) / tupleSize);
		joinOuterBlock = new ArrayList<>(tuplesPerPage);
		gotoNext();
	}

    /**
     * Prepare the first outer block for join
     */
	private void generateJoinOuterBlock() {
		joinOuterBlock.clear();
		int count = tuplesPerPage;
		Tuple tuple;
		while(count-- > 0 && ((tuple = leftchild.getNextTuple()) != null))
			joinOuterBlock.add(tuple);
		resetOuterBlockIndex(0);
	}

    /**
     * Once the current outer block is used, get the next one
     * @param index the page index of the current block
     */
	private void resetOuterBlockIndex(int index) {
		currentOuterIdx = index;
		leftTuple = (index < joinOuterBlock.size()) ? joinOuterBlock.get(index) : null;
	}

    /**
     * Goto the next block
     */
	@Override
	protected void gotoNext() {
		if (rightTuple != null) {
			resetOuterBlockIndex(++currentOuterIdx);
			if (leftTuple != null)
				return;
			rightTuple = rightchild.getNextTuple();
			if (rightTuple != null) {
				resetOuterBlockIndex(0);
				return;
			}
		}

		generateJoinOuterBlock();
		rightchild.reset();
		rightTuple = rightchild.getNextTuple();
	}

    /**
     * For printing the operator in plan
     * @return String form for printing in query plan
     */
    @Override
    public String print() {
		String expression = (joinCondition != null) ? joinCondition.toString() : "";
		return ("BNLJ[" + expression + "]");
	}

}
