package brainslug.jpa.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "BS_FLOW_INSTANCE")
public class FlowInstanceEntity {

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

  public Long getVersion() {
    return version;
  }

  public FlowInstanceEntity withVersion(Long version) {
    this.version = version;
    return this;
  }
}
