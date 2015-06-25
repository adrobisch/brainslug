package brainslug.flow.execution.instance;

import brainslug.flow.definition.Identifier;
import brainslug.flow.instance.FlowInstance;

public class DefaultFlowInstance implements FlowInstance {
    final Identifier<?> id;

    public DefaultFlowInstance(Identifier<?> id) {
        this.id = id;
    }

    @Override
    public Identifier<?> getIdentifier() {
        return id;
    }
}
