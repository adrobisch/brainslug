package brainslug.flow.execution.instance;

import brainslug.flow.execution.property.store.HashMapPropertyStore;
import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.flow.execution.token.HashMapTokenStore;
import brainslug.flow.instance.FlowInstance;
import brainslug.util.Option;
import brainslug.util.UuidGenerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static brainslug.flow.builder.FlowBuilderSupport.id;
import static brainslug.flow.execution.property.ExecutionProperties.newProperties;
import static org.assertj.core.api.Assertions.assertThat;

public class HashMapInstanceStoreTest {

    @Test
    public void shouldFindInstanceById() {
        InstanceStore instanceStore = instanceStore(propertyStore());

        FlowInstance instance = instanceStore.createInstance(id("flow1"));

        Option<? extends FlowInstance> foundInstance = instanceStore
                .findInstance(new InstanceSelector().withInstanceId(instance.getIdentifier()));

        assertThat(foundInstance.isPresent()).isTrue();
    }

    @Test
    public void shouldFindInstancesByDefinitionId() {
        InstanceStore instanceStore = instanceStore(propertyStore());

        FlowInstance instance1 = instanceStore.createInstance(id("flow1"));
        FlowInstance instance2 = instanceStore.createInstance(id("flow2"));
        FlowInstance instance3 = instanceStore.createInstance(id("flow2"));

        Collection<? extends FlowInstance> foundByDefinitionId = instanceStore
                .findInstances(new InstanceSelector()
                        .withDefinitionId(id("flow2")));

        assertThat(foundByDefinitionId).hasSize(2);

        Collection<? extends FlowInstance> foundByIdAndDefinitionId = instanceStore
                .findInstances(new InstanceSelector()
                        .withInstanceId(instance1.getIdentifier())
                        .withDefinitionId(id("flow1")));

        assertThat(foundByIdAndDefinitionId).hasSize(1);
        assertThat(new ArrayList<FlowInstance>(foundByIdAndDefinitionId).get(0).getIdentifier()).isEqualTo(instance1.getIdentifier());
    }

    @Test
    public void shouldFindInstancesByProperty() {
        HashMapPropertyStore propertyStore = propertyStore();
        HashMapInstanceStore instanceStore = instanceStore(propertyStore);

        FlowInstance instanceId = instanceStore.createInstance(id("flow1"));
        propertyStore.setProperties(instanceId.getIdentifier(), newProperties().with("foo", "bar"));

        Option<FlowInstance> instance = instanceStore.findInstance(new InstanceSelector()
                .withDefinitionId(id("flow1"))
                .withProperty(id("foo"), "bar"));

        assertThat(instance.isPresent()).isTrue();
    }

    HashMapInstanceStore instanceStore(PropertyStore propertyStore) {
        UuidGenerator idGenerator = new UuidGenerator();
        return new HashMapInstanceStore(idGenerator, propertyStore, new HashMapTokenStore(idGenerator));
    }

    HashMapPropertyStore propertyStore() {
        return new HashMapPropertyStore();
    }
}