package expvisitors;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * Wrapper on top of JSQL Parser ExpressionVisitor
 * We only implement those expression types which are needed for this project
 * For other methods we simply throw an UnsupportedOperation exception
 * (just in case they are invoked)
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public class ExpressionVisitorWrapper implements ExpressionVisitor {

    long expressionValue = 0;
    boolean expressionResult = false;

    /**
     * Retrieve the evaluation result of the expression - True or False
     *
     * @return The evaluation result - True/False
     */
    public boolean getExpressionEvaluationResult() {
        return expressionResult;
    }

    @Override
    public void visit(NullValue arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(Function arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(InverseExpression arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(JdbcParameter arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(DoubleValue arg0) {

    }

    @Override
    public void visit(DateValue arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(TimeValue arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(TimestampValue arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(Parenthesis arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(StringValue arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(Addition arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(Division arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(Multiplication arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(Subtraction arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    /**
     * Visit function for AndExpression type
     * AndExpression is a conjunction of two separate expressions
     * We evaluate the left child and right child first and then
     * take the conjunction of the two results
     *
     * @param arg0 AndExpression expression object
     */
    @Override
    public void visit(AndExpression arg0) {
        arg0.getLeftExpression().accept(this);
        boolean leftChild = expressionResult;
        arg0.getRightExpression().accept(this);
        boolean rightChild = expressionResult;
        expressionResult = leftChild && rightChild;

    }

    @Override
    public void visit(OrExpression arg0) {

    }

    @Override
    public void visit(Between arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    /**
     * Visit function for EqualsTo type
     * EqualsTo needs two separate expressions to evaluate the final result
     * We evaluate the left child and right child first and then
     * produce the result based on if they are equal
     *
     * @param arg0 EqualsTo expression object
     */
    @Override
    public void visit(EqualsTo arg0) {
        arg0.getLeftExpression().accept(this);
        double leftChild = expressionValue;
        arg0.getRightExpression().accept(this);
        double rightChild = expressionValue;
        if(leftChild == rightChild)
            expressionResult = true;
        else
            expressionResult = false;

    }

    /**
     * Visit function for LongValue expression type
     * Since it is a leaf expression type, it does not return a boolean but the actual numeric value
     * Set the current expression value to the numeric value
     *
     * @param arg0 LongValue expression object
     */
    @Override
    public void visit(LongValue arg0) {
        expressionValue = arg0.getValue();

    }

    /**
     * Visit function for GreaterThan type
     * GreaterThan needs two separate expressions to evaluate the final result
     * We evaluate the left child and right child first and then
     * produce the result based on if one is greater than the other
     *
     * @param arg0 GreaterThan expression object
     */
    @Override
    public void visit(GreaterThan arg0) {
        arg0.getLeftExpression().accept(this);
        double leftChild = expressionValue;
        arg0.getRightExpression().accept(this);
        double rightChild = expressionValue;
        if(leftChild > rightChild)
            expressionResult = true;
        else
            expressionResult = false;
    }

    /**
     * Visit function for GreaterThanEquals type
     * GreaterThanEquals needs two separate expressions to evaluate the final result
     * We evaluate the left child and right child first and then
     * produce the result based on if one is greater than or equal to the other
     *
     * @param arg0 GreaterThanEquals expression object
     */
    @Override
    public void visit(GreaterThanEquals arg0) {
        arg0.getLeftExpression().accept(this);
        double leftChild = expressionValue;
        arg0.getRightExpression().accept(this);
        double rightChild = expressionValue;
        if(leftChild >= rightChild)
            expressionResult = true;
        else
            expressionResult = false;
    }

    @Override
    public void visit(InExpression arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(IsNullExpression arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(LikeExpression arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    /**
     * Visit function for MinorThan type
     * MinorThan needs two separate expressions to evaluate the final result
     * We evaluate the left child and right child first and then
     * produce the result based on if one is less than the other
     *
     * @param arg0 MinorThan expression object
     */
    @Override
    public void visit(MinorThan arg0) {
        arg0.getLeftExpression().accept(this);
        double leftChild = expressionValue;
        arg0.getRightExpression().accept(this);
        double rightChild = expressionValue;
        if(leftChild < rightChild)
            expressionResult = true;
        else
            expressionResult = false;

    }

    /**
     * Visit function for MinorThanEquals type
     * MinorThanEquals needs two separate expressions to evaluate the final result
     * We evaluate the left child and right child first and then
     * produce the result based on if one is less than or equal to the other
     *
     * @param arg0 MinorThanEquals expression object
     */
    @Override
    public void visit(MinorThanEquals arg0) {
        arg0.getLeftExpression().accept(this);
        double leftChild = expressionValue;
        arg0.getRightExpression().accept(this);
        double rightChild = expressionValue;
        if(leftChild <= rightChild)
            expressionResult = true;
        else
            expressionResult = false;

    }

    /**
     * Visit function for NotEqualsTo type
     * NotEqualsTo needs two separate expressions to evaluate the final result
     * We evaluate the left child and right child first and then
     * produce the result based on if one is equal to the other or not
     *
     * @param arg0 NotEqualsTo expression object
     */
    @Override
    public void visit(NotEqualsTo arg0) {
        arg0.getLeftExpression().accept(this);
        double leftChild = expressionValue;
        arg0.getRightExpression().accept(this);
        double rightChild = expressionValue;
        if(leftChild != rightChild)
            expressionResult = true;
        else
            expressionResult = false;

    }

    @Override
    public void visit(Column arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(SubSelect arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(CaseExpression arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(WhenClause arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(ExistsExpression arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(AllComparisonExpression arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(AnyComparisonExpression arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(Concat arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(Matches arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(BitwiseAnd arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(BitwiseOr arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void visit(BitwiseXor arg0) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }
}