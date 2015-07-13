package brainslug.flow.execution.instance;

import brainslug.flow.definition.Identifier;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.instance.FlowInstanceProperties;
import brainslug.flow.instance.FlowInstanceTokenList;

public class DefaultFlowInstance implements FlowInstance {
    final Identifier<?> id;

    public DefaultFlowInstance(Identifier<?> id) {
        this.id = id;
    }

    @Override
    public Identifier<?> getIdentifier() {
        return id;
    }

    @Override
    public Identifier getDefinitionId() {
        return null;
    }

    @Override
    public FlowInstanceTokenList getTokens() {
        throw new UnsupportedOperationException("implement me");
    }

    @Override
    public FlowInstanceProperties getProperties() {
        return null;
    }
}
