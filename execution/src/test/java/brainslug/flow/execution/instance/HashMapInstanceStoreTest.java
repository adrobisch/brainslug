package brainslug.flow.execution.instance;

import brainslug.flow.instance.FlowInstance;
import brainslug.util.Option;
import brainslug.util.UuidGenerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static brainslug.flow.builder.FlowBuilderSupport.id;
import static org.assertj.core.api.Assertions.assertThat;

public class HashMapInstanceStoreTest {
    @Test
    public void shouldFindInstanceById() {
        InstanceStore instanceStore = instanceStore();

        FlowInstance instance = instanceStore.createInstance(id("flow1"));

        Option<? extends FlowInstance> foundInstance = instanceStore
                .findInstance(new DefaultInstanceSelector().withInstanceId(instance.getIdentifier()));

        assertThat(foundInstance.isPresent()).isTrue();
    }

    @Test
    public void shouldFindInstancesByDefinitionId() {
        InstanceStore instanceStore = instanceStore();

        FlowInstance instance1 = instanceStore.createInstance(id("flow1"));
        FlowInstance instance2 = instanceStore.createInstance(id("flow2"));
        FlowInstance instance3 = instanceStore.createInstance(id("flow2"));

        Collection<? extends FlowInstance> foundByDefinitionId = instanceStore
                .findInstances(new DefaultInstanceSelector()
                        .withDefinitionId(id("flow2")));

        assertThat(foundByDefinitionId).hasSize(2);

        Collection<? extends FlowInstance> foundByIdAndDefinitionId = instanceStore
                .findInstances(new DefaultInstanceSelector()
                                .withInstanceId(instance1.getIdentifier())
                                .withDefinitionId(id("flow1")));

        assertThat(foundByIdAndDefinitionId).hasSize(1);
        assertThat(new ArrayList<FlowInstance>(foundByIdAndDefinitionId).get(0).getIdentifier()).isEqualTo(instance1.getIdentifier());
    }

    private HashMapInstanceStore instanceStore() {
        return new HashMapInstanceStore(new UuidGenerator());
    }
}