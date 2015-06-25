package brainslug.flow.execution.instance;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.EqualsExpression;
import brainslug.flow.expression.Property;
import brainslug.flow.expression.Value;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.instance.InstanceSelector;
import brainslug.util.IdGenerator;
import brainslug.util.Option;

import java.util.*;

public class HashMapInstanceStore implements InstanceStore {
    final IdGenerator idGenerator;
    final Map<Identifier, Map<Identifier, FlowInstance>> instancesByDefinitionId;

    public HashMapInstanceStore(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        this.instancesByDefinitionId = new HashMap<Identifier, Map<Identifier, FlowInstance>>();
    }

    @Override
    public List<FlowInstance> findInstances(InstanceSelector instanceSelector) {
      return filterProperties(instanceSelector.properties(), instancesByFlowIdAndInstanceId(instanceSelector));
    }

    private List<FlowInstance> filterProperties(Collection<EqualsExpression<Property<?>, Value<String>>> properties, List<FlowInstance> flowInstances) {
      if (!properties.isEmpty()) {
        throw new UnsupportedOperationException("hash map instance store can't select by properties");
      }
      return flowInstances;
    }

    private List<FlowInstance> instancesByFlowIdAndInstanceId(InstanceSelector instanceSelector) {
      if (!instanceSelector.definitionId().isPresent() && !instanceSelector.instanceId().isPresent()) {
          throw new IllegalArgumentException("you need to specify either instance or definition identifier");
      } else if (instanceSelector.definitionId().isPresent() && !instanceSelector.instanceId().isPresent()) {
          return instancesByDefinitionId(instanceSelector.definitionId().get());
      } else if (instanceSelector.instanceId().isPresent() && instanceSelector.definitionId().isPresent()) {
          return emptyOrSingletonListFromNullable(instanceByDefinitionIdAndInstanceId(instanceSelector));
      } else {
          return emptyOrSingletonListFromNullable(findInstance(instanceSelector).orElse(null));
      }
    }

    List<FlowInstance> emptyOrSingletonListFromNullable(FlowInstance instance) {
        if (instance == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(instance);
    }

    protected FlowInstance instanceByDefinitionIdAndInstanceId(InstanceSelector instanceSelector) {
        return getOrCreateInstanceMap(instanceSelector.definitionId().get()).get(instanceSelector.instanceId().get());
    }

    @Override
    public Option<FlowInstance> findInstance(InstanceSelector instanceSelector) {
       for (Map<Identifier, FlowInstance> instances : instancesByDefinitionId.values()) {
            if (instances.get(instanceSelector.instanceId().get()) != null) {
                return Option.of(instances.get(instanceSelector.instanceId().get()));
            }
       }
       return Option.empty();
    }

    protected List<FlowInstance> instancesByDefinitionId(Identifier definitionId) {
        return new ArrayList<FlowInstance>(getOrCreateInstanceMap(definitionId).values());
    }

    @Override
    public FlowInstance createInstance(Identifier definitionId) {
        Identifier instanceId = idGenerator.generateId();
        DefaultFlowInstance newInstance = new DefaultFlowInstance(instanceId);

        addInstanceToDefinitionInstances(definitionId, newInstance);

        return newInstance;
    }

    private List<FlowInstance> addInstanceToDefinitionInstances(Identifier definitionId, DefaultFlowInstance newInstance) {
        Map<Identifier, FlowInstance> definitionInstanceMap = getOrCreateInstanceMap(definitionId);
        definitionInstanceMap.put(newInstance.getIdentifier(), newInstance);

        return new ArrayList<FlowInstance>(definitionInstanceMap.values());
    }

    private Map<Identifier, FlowInstance> getOrCreateInstanceMap(Identifier definitionId) {
        if (instancesByDefinitionId.get(definitionId) == null) {
            instancesByDefinitionId.put(definitionId, new HashMap<Identifier, FlowInstance>());
        }
        return instancesByDefinitionId.get(definitionId);
    }
}
