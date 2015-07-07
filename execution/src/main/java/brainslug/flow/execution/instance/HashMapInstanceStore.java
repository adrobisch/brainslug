package brainslug.flow.execution.instance;

import brainslug.flow.context.ExecutionProperty;
import brainslug.flow.context.FlowProperties;
import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.property.store.PropertyStore;
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
    final PropertyStore propertyStore;
    final Map<Identifier, Map<Identifier, FlowInstance>> instancesByDefinitionId;
    final Map<Identifier, FlowInstance> instancesById;

    public HashMapInstanceStore(IdGenerator idGenerator, PropertyStore propertyStore) {
        this.idGenerator = idGenerator;
        this.propertyStore = propertyStore;
        this.instancesByDefinitionId = new HashMap<Identifier, Map<Identifier, FlowInstance>>();
        this.instancesById = new HashMap<Identifier, FlowInstance>();
    }

    @Override
    public List<FlowInstance> findInstances(InstanceSelector instanceSelector) {
      return filterProperties(instanceSelector.properties(), instancesByFlowIdAndInstanceId(instanceSelector));
    }

    private List<FlowInstance> filterProperties(Collection<EqualsExpression<Property<?>, Value<String>>> propertyExpressions, List<FlowInstance> flowInstances) {
        if (propertyExpressions.isEmpty()) {
            return flowInstances;
        }

        List<FlowInstance> filteredInstances = new ArrayList<FlowInstance>();

        for (FlowInstance flowInstance : flowInstances) {
          FlowProperties<?, ExecutionProperty<?>> instanceProperties = propertyStore.getProperties(flowInstance.getIdentifier());

          for (EqualsExpression<Property<?>, Value<String>> propertyExpression : propertyExpressions) {
              ExecutionProperty<?> propertyValue = instanceProperties.get(propertyExpression.getLeft().getValue().stringValue());

              if (propertyValue != null && propertyValue.getValue().equals(propertyExpression.getRight().getValue())) {
                filteredInstances.add(flowInstance);
              }
          }
        }

        return filteredInstances;
    }

    private List<FlowInstance> instancesByFlowIdAndInstanceId(InstanceSelector instanceSelector) {
      if (!instanceSelector.definitionId().isPresent() && !instanceSelector.instanceId().isPresent()) {
          throw new IllegalArgumentException("you need to specify either instance or definition identifier");
      } else if (instanceSelector.definitionId().isPresent() && !instanceSelector.instanceId().isPresent()) {
          return instancesByDefinitionId(instanceSelector.definitionId().get());
      } else if (instanceSelector.instanceId().isPresent() && instanceSelector.definitionId().isPresent()) {
          return emptyOrSingletonListFromNullable(instanceByDefinitionIdAndInstanceId(instanceSelector));
      } else {
          return emptyOrSingletonListFromNullable(instanceByInstanceId(instanceSelector.instanceId().get()).orElse(null));
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
        List<FlowInstance> instances = filterProperties(instanceSelector.properties(), instancesByFlowIdAndInstanceId(instanceSelector));
        if (instances.isEmpty()) {
            return Option.empty();
        }
        return Option.of(instances.get(0));
    }

    protected List<FlowInstance> instancesByDefinitionId(Identifier definitionId) {
        return new ArrayList<FlowInstance>(getOrCreateInstanceMap(definitionId).values());
    }

    protected Option<FlowInstance> instanceByInstanceId(Identifier instanceId) {
        return Option.of(instancesById.get(instanceId));
    }

    @Override
    public FlowInstance createInstance(Identifier definitionId) {
        Identifier instanceId = idGenerator.generateId();
        DefaultFlowInstance newInstance = new DefaultFlowInstance(instanceId);

        addInstanceToDefinitionInstances(definitionId, newInstance);
        instancesById.put(instanceId, newInstance);

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
