package brainslug.jpa.entity;

import javax.persistence.*;

@Entity
@Table(name = "BS_FLOW_TOKEN")
public class FlowTokenEntity {

  @Id
  @Column(name = "_ID")
  protected String id;

  @Version
  @Column(name = "_VERSION")
  protected Long version;

  @Column(name = "_CREATED")
  protected Long created;

  @Column(name = "_CURRENT_NODE")
  protected String currentNode;

  @Column(name = "_FLOW_INSTANCE_ID")
  protected String flowInstanceId;

  @Column(name = "_IS_DEAD")
  protected Integer isDead;

  @Column(name = "_SOURCE_NODE")
  protected String sourceNode;

  public String getId() {
    return id;
  }

  public FlowTokenEntity withId(String id) {
    this.id = id;
    return this;
  }

  public Long getVersion() {
    return version;
  }

  public FlowTokenEntity withVersion(Long version) {
    this.version = version;
    return this;
  }

  public Long getCreated() {
    return created;
  }

  public FlowTokenEntity withCreated(Long created) {
    this.created = created;
    return this;
  }

  public String getCurrentNode() {
    return currentNode;
  }

  public FlowTokenEntity withCurrentNode(String currentNode) {
    this.currentNode = currentNode;
    return this;
  }

  public String getFlowInstanceId() {
    return flowInstanceId;
  }

  public FlowTokenEntity withFlowInstanceId(String flowInstanceId) {
    this.flowInstanceId = flowInstanceId;
    return this;
  }

  public Integer getIsDead() {
    return isDead;
  }

  public FlowTokenEntity withIsDead(Integer isDead) {
    this.isDead = isDead;
    return this;
  }

  public String getSourceNode() {
    return sourceNode;
  }

  public FlowTokenEntity withSourceNode(String sourceNode) {
    this.sourceNode = sourceNode;
    return this;
  }
}
