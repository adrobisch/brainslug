package brainslug.flow.execution.instance;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.flow.execution.token.TokenStore;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.instance.FlowInstanceProperties;
import brainslug.flow.instance.FlowInstanceTokenList;

public class DefaultFlowInstance implements FlowInstance {
    final Identifier<?> id;
    private PropertyStore propertyStore;
    private TokenStore tokenStore;

    public DefaultFlowInstance(Identifier<?> id, PropertyStore propertyStore, TokenStore tokenStore) {
        this.id = id;
        this.propertyStore = propertyStore;
        this.tokenStore = tokenStore;
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
        return tokenStore.getInstanceTokens(getIdentifier());
    }

    @Override
    public FlowInstanceProperties getProperties() {
        return propertyStore.getProperties(getIdentifier());
    }
}
