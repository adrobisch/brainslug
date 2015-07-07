package brainslug.jpa.entity;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.definition.Identifier;
import brainslug.flow.instance.FlowInstance;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
  Set<InstancePropertyEntity> properties;

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

  public String getDefinitionId() {
    return definitionId;
  }

  public FlowInstanceEntity withDefinitionId(String definitionId) {
    this.definitionId = definitionId;
    return this;
  }

  public Set<InstancePropertyEntity> getProperties() {
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
