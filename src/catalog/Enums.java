package catalog;

/**
*
*This is just to store Enum datatypes for the options of Join and Sort that our code supports.
*
*
* @author Saarthak Chandra - sc2776
*         Shweta Shrivastava - ss3646
*         Vikas P Nelamangala - vpn6
*
*
*/
public class Enums {

    public enum JoinType {
        TupleNestedLoopJoin,
        BlockNestedLoopJoin,
        SortMergeJoin
    }

    public enum SortType {
        InMemorySort,
        ExternalSort
    }
}
