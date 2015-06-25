package brainslug.flow.expression;

public class EqualsExpression<LeftType extends Expression, RightType extends Expression> implements Expression {
    final LeftType left;
    final RightType right;

    public EqualsExpression(LeftType left, RightType right) {
        this.left = left;
        this.right = right;
    }

    public LeftType getLeft() {
        return left;
    }

    public RightType getRight() {
        return right;
    }

    @Override
    public String toString() {
        return left  + "==" + right;
    }
}
