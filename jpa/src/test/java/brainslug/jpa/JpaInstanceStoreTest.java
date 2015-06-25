package brainslug.jpa;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.instance.DefaultInstanceSelector;
import brainslug.flow.instance.FlowInstance;
import brainslug.util.Option;
import org.junit.Test;

import java.util.List;

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
        List<? extends FlowInstance> foundInstances = instanceStore.findInstances(new DefaultInstanceSelector().withDefinitionId(flowId));

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
        Option<? extends FlowInstance> foundInstance = instanceStore.findInstance(new DefaultInstanceSelector().withInstanceId(instanceOne));
        List<? extends FlowInstance> foundInstances = instanceStore.findInstances(new DefaultInstanceSelector().withInstanceId(instanceOne));

        // then:
        assertThat(foundInstance.isPresent()).isTrue();
        assertThat(foundInstance.get().getIdentifier()).isEqualTo(instanceOne);

        assertThat(foundInstances).hasSize(1);
        assertThat(foundInstances.get(0).getIdentifier()).isEqualTo(instanceOne);
    }

    private FlowInstance createInstance(JpaInstanceStore instanceStore, Identifier instanceOne) {
        when(idGeneratorMock.generateId()).thenReturn(instanceOne);
        return instanceStore.createInstance(flowId);
    }

    JpaInstanceStore jpaInstanceStore() {
        return new JpaInstanceStore(database, idGeneratorMock);
    }

}