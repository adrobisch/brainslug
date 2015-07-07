package brainslug.jpa;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.instance.DefaultFlowInstance;
import brainslug.flow.execution.instance.InstanceStore;
import brainslug.flow.expression.EqualsExpression;
import brainslug.flow.expression.Property;
import brainslug.flow.expression.Value;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.instance.InstanceSelector;
import brainslug.jpa.entity.FlowInstanceEntity;
import brainslug.jpa.entity.QFlowInstanceEntity;
import brainslug.jpa.entity.QInstancePropertyEntity;
import brainslug.util.IdGenerator;
import brainslug.util.Option;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;

import java.util.Date;
import java.util.List;

public class JpaInstanceStore implements InstanceStore {

    Database database;
    IdGenerator idGenerator;

    public JpaInstanceStore(Database database, IdGenerator idGenerator) {
        this.database = database;
        this.idGenerator = idGenerator;
    }

    @Override
    public List<? extends FlowInstance> findInstances(InstanceSelector instanceSelector) {
        JPAQuery instanceQuery = database
                .query()
                .from(QFlowInstanceEntity.flowInstanceEntity);

        return filterByInstance(instanceSelector,
                filterByDefinitionId(instanceSelector,
                 filterByProperties(instanceSelector, instanceQuery))).list(QFlowInstanceEntity.flowInstanceEntity);
    }

    private JPAQuery filterByProperties(InstanceSelector instanceSelector, JPAQuery instanceQuery) {
      for (EqualsExpression<Property<?>, Value<String>> propertyExpression : instanceSelector.properties()) {
          QInstancePropertyEntity instanceProperty = QFlowInstanceEntity
                  .flowInstanceEntity
                  .properties.any();

          instanceQuery
                  .where(instanceProperty.stringValue.eq(propertyExpression.getRight().getValue())
                  .and(instanceProperty.propertyKey.eq(propertyExpression.getLeft().getValue().stringValue())));
      }

      return instanceQuery;
    }

    private JPAQuery filterByDefinitionId(InstanceSelector instanceSelector, JPAQuery instanceQuery) {
        if (instanceSelector.definitionId().isPresent()) {
            BooleanExpression matchesDefinitionId = QFlowInstanceEntity
                    .flowInstanceEntity
                    .definitionId
                    .eq(instanceSelector.definitionId().get().stringValue());

            return instanceQuery.where(matchesDefinitionId);
        }
        return instanceQuery;
    }

    private JPAQuery filterByInstance(InstanceSelector instanceSelector, JPAQuery instanceQuery) {
        if (instanceSelector.instanceId().isPresent()) {
            BooleanExpression matchesInstanceId = QFlowInstanceEntity
                    .flowInstanceEntity
                    .id
                    .eq(instanceSelector.instanceId().get().stringValue());

            return instanceQuery.where(matchesInstanceId);
        }
        return instanceQuery;
    }

    @Override
    public Option<? extends FlowInstance> findInstance(InstanceSelector instanceSelector) {
        List<? extends FlowInstance> instances = findInstances(instanceSelector);
        if (instances.isEmpty()) {
            return Option.empty();
        }
        return Option.of(instances.get(0));
    }

    @Override
    public FlowInstance createInstance(Identifier definitionId) {
        Identifier identifier = idGenerator.generateId();

        database.insertOrUpdate(new FlowInstanceEntity()
                        .withId(identifier.stringValue())
                        .withCreated(new Date().getTime())
                        .withDefinitionId(definitionId.stringValue())
        );

        return new DefaultFlowInstance(identifier);
    }
}
