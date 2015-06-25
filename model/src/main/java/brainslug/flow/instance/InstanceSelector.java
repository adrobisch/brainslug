package brainslug.flow.instance;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.EqualsExpression;
import brainslug.flow.expression.Property;
import brainslug.flow.expression.Value;
import brainslug.util.Option;

import java.util.Collection;

public interface InstanceSelector {
    Option<Identifier> instanceId();
    Option<Identifier> definitionId();
    Collection<EqualsExpression<Property<?>, Value<String>>> properties();
}
