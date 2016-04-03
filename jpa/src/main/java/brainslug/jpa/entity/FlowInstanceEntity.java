package brainslug.jpa.entity;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.property.ExecutionProperties;
import brainslug.flow.execution.token.TokenList;
import brainslug.flow.execution.instance.FlowInstance;
import brainslug.flow.execution.instance.FlowInstanceProperties;
import brainslug.flow.execution.instance.FlowInstanceProperty;
import brainslug.flow.execution.instance.FlowInstanceTokenList;
import brainslug.util.IdUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "BS_FLOW_INSTANCE")
public class FlowInstanceEntity implements FlowInstance {

  @Id
  @Column(name = "_ID")
  protected String id;

  @NotNull
  @Column(name = "_CREATED")
  protected Long created;

  @NotNull
  @Column(name = "_DEFINITION_ID")
  protected String definitionId;

  @Version
  @Column(name = "_VERSION")
  protected Long version;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "_INSTANCE_ID")
  Set<InstancePropertyEntity> properties = new HashSet<InstancePropertyEntity>();

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "_FLOW_INSTANCE_ID")
  List<FlowTokenEntity> tokens = new ArrayList<FlowTokenEntity>();

  public FlowInstanceEntity() {
  }

  public String getId() {
    return id;
  }

  public FlowInstanceEntity withId(String id) {
    this.id = id;
    return this;
  }

  public Long getCreated() {
    return created;
  }

  public FlowInstanceEntity withCreated(Long created) {
    this.created = created;
    return this;
  }

  public Identifier getDefinitionId() {
    return IdUtil.id(definitionId);
  }

  @Override
  public FlowInstanceTokenList getTokens() {
    return new TokenList(tokens);
  }

  public List<FlowTokenEntity> getTokenEntities() {
    return tokens;
  }

  public FlowInstanceEntity withDefinitionId(String definitionId) {
    this.definitionId = definitionId;
    return this;
  }

  @Override
  public FlowInstanceProperties<?, FlowInstanceProperty<?>> getProperties() {
    return new ExecutionProperties().from(properties);
  }

  public Set<InstancePropertyEntity> getPropertiesEntities() {
    return properties;
  }

  public Long getVersion() {
    return version;
  }

  public FlowInstanceEntity withVersion(Long version) {
    this.version = version;
    return this;
  }

  @Override
  public Identifier<?> getIdentifier() {
    return FlowBuilder.id(id);
  }
}
