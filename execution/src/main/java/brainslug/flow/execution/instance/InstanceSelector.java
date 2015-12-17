package brainslug.flow.execution.instance;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.EqualsExpression;
import brainslug.flow.expression.Property;
import brainslug.flow.expression.Value;
import brainslug.flow.instance.FlowInstanceSelector;
import brainslug.util.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InstanceSelector implements FlowInstanceSelector {
    Identifier instanceId;
    Identifier definitionId;
    List<EqualsExpression<Property<?>, Value<String>>> properties = new ArrayList<EqualsExpression<Property<?>, Value<String>>>();

    @Override
    public Option<Identifier> instanceId() {
        return Option.of(instanceId);
    }

    @Override
    public Option<Identifier> definitionId() {
        return Option.of(definitionId);
    }

    @Override
    public Collection<EqualsExpression<Property<?>, Value<String>>> properties() {
      return properties;
    }

    public InstanceSelector withInstanceId(Identifier instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    public InstanceSelector withDefinitionId(Identifier definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    public InstanceSelector withProperty(Property<?> property, Value<String> value) {
        properties.add(new EqualsExpression<Property<?>, Value<String>>(property, value));
        return this;
    }

    public InstanceSelector withProperty(Identifier<?> id, String value) {
        properties.add(new EqualsExpression<Property<?>, Value<String>>(new Property<String>(id, String.class), new Value<String>(value)));
        return this;
    }
}
