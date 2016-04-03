package brainslug.flow.execution.instance;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.flow.execution.token.TokenStore;

public class DefaultFlowInstance implements FlowInstance {
    private final Identifier<?> id;
    private final Identifier<?> definitionId;
    private final PropertyStore propertyStore;
    private final TokenStore tokenStore;

    public DefaultFlowInstance(Identifier<?> id, Identifier<?> definitionId, PropertyStore propertyStore, TokenStore tokenStore) {
        this.id = id;
        this.definitionId = definitionId;
        this.propertyStore = propertyStore;
        this.tokenStore = tokenStore;
    }

    @Override
    public Identifier<?> getIdentifier() {
        return id;
    }

    @Override
    public Identifier getDefinitionId() {
        return definitionId;
    }

    @Override
    public FlowInstanceTokenList getTokens() {
        return tokenStore.getInstanceTokens(getIdentifier());
    }

    @Override
    public FlowInstanceProperties<?, FlowInstanceProperty<?>> getProperties() {
        return propertyStore.getProperties(getIdentifier());
    }
}
