package brainslug.juel;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.expression.DefaultExpressionEvaluator;
import brainslug.flow.expression.Expression;
import brainslug.flow.instance.FlowInstanceProperty;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

public class JuelExpressionEvaluator extends DefaultExpressionEvaluator {
    @Override
    public <T> T evaluate(Expression expression, ExecutionContext context, Class<T> resultType) {
        if (expression instanceof JuelExpression) {
            return evaluateJuel(((JuelExpression) expression).getString(), context, resultType);
        }

        return super.evaluate(expression, context, resultType);
    }

    private <T> T evaluateJuel(String juelExpression, ExecutionContext context, Class<T> resultType) {
        ExpressionFactory factory = new de.odysseus.el.ExpressionFactoryImpl();

        de.odysseus.el.util.SimpleContext juelContext = new de.odysseus.el.util.SimpleContext();

        for (FlowInstanceProperty<?> property : context.getProperties().values()) {
            juelContext.setVariable(property.getKey(), valueExpression(property, factory));
        }

        return (T) factory.createValueExpression(juelContext, juelExpression, resultType)
                .getValue(juelContext);
    }

    private ValueExpression valueExpression(FlowInstanceProperty<?> property, ExpressionFactory factory) {
        if (property.getValue() instanceof String) {
            return factory.createValueExpression(property.getValue(), String.class);
        } else if (property.getValue() instanceof Number) {
            return factory.createValueExpression(property.getValue(), Number.class);
        } else {
            return factory.createValueExpression(property.getValue(), property.getClass());
        }
    }
}
