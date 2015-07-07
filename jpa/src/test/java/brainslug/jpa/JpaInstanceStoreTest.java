package brainslug.jpa;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.instance.FlowInstanceSelector;
import brainslug.flow.expression.Property;
import brainslug.flow.expression.Value;
import brainslug.flow.instance.FlowInstance;
import brainslug.util.Option;
import org.junit.Test;

import java.util.List;

import static brainslug.flow.execution.property.ExecutionProperties.newProperties;
import static brainslug.util.IdUtil.id;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class JpaInstanceStoreTest extends AbstractDatabaseTest {

    Identifier flowId = id("flow");

    @Test
    public void shouldFindInstancesForDefinitionId() {
        // given:
        JpaInstanceStore instanceStore = jpaInstanceStore();

        Identifier instanceOne = id("1");
        createInstance(instanceStore, instanceOne);

        Identifier instanceTwo = id("2");
        createInstance(instanceStore, instanceTwo);

        // when:
        List<? extends FlowInstance> foundInstances = instanceStore.findInstances(new FlowInstanceSelector().withDefinitionId(flowId));

        // then:
        assertThat(foundInstances).hasSize(2);
    }

    @Test
    public void shouldFindInstanceByInstanceId() {
        // given:
        JpaInstanceStore instanceStore = jpaInstanceStore();

        Identifier instanceOne = id("1");
        createInstance(instanceStore, instanceOne);

        Identifier instanceTwo = id("2");
        createInstance(instanceStore, instanceTwo);

        // when:
        Option<? extends FlowInstance> foundInstance = instanceStore.findInstance(new FlowInstanceSelector().withInstanceId(instanceOne));
        List<? extends FlowInstance> foundInstances = instanceStore.findInstances(new FlowInstanceSelector().withInstanceId(instanceOne));

        // then:
        assertThat(foundInstance.isPresent()).isTrue();
        assertThat(foundInstance.get().getIdentifier()).isEqualTo(instanceOne);

        assertThat(foundInstances).hasSize(1);
        assertThat(foundInstances.get(0).getIdentifier()).isEqualTo(instanceOne);
    }

    @Test
    public void shouldFilterInstancesByProperties() {
        // given:
        JpaInstanceStore instanceStore = jpaInstanceStore();
        JpaPropertyStore jpaPropertyStore = jpaPropertyStore();

        Identifier instanceOne = id("1");
        createInstance(instanceStore, instanceOne);

        Identifier instanceTwo = id("2");
        createInstance(instanceStore, instanceTwo);

        jpaPropertyStore.setProperties(instanceOne, newProperties().with("foo", "bar"));

        // when:
        FlowInstanceSelector matchingSelector = new FlowInstanceSelector()
                .withInstanceId(instanceOne)
                .withProperty(new Property<String>(id("foo")), new Value<String>("bar"));

        FlowInstanceSelector nonMatchingSelector = new FlowInstanceSelector()
                .withInstanceId(instanceOne)
                .withProperty(new Property<String>(id("foo")), new Value<String>("baz"));

        List<? extends FlowInstance> matchingInstances = instanceStore.findInstances(matchingSelector);
        List<? extends FlowInstance> nonMatchtingInstances = instanceStore.findInstances(nonMatchingSelector);

        assertThat(matchingInstances).hasSize(1);
        assertThat(matchingInstances.get(0).getIdentifier()).isEqualTo(instanceOne);

        assertThat(nonMatchtingInstances).isEmpty();
    }

    private FlowInstance createInstance(JpaInstanceStore instanceStore, Identifier instanceOne) {
        when(idGeneratorMock.generateId()).thenReturn(instanceOne);
        return instanceStore.createInstance(flowId);
    }

    JpaInstanceStore jpaInstanceStore() {
        return new JpaInstanceStore(database, idGeneratorMock);
    }

    JpaPropertyStore jpaPropertyStore() {
        return new JpaPropertyStore(database, idGeneratorMock);
    }

}