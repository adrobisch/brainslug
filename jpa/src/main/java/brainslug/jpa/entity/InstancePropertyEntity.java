package brainslug.jpa.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "BS_INSTANCE_PROPERTY")
public class InstancePropertyEntity {
  @Id
  @Column(name = "_ID")
  protected String id;

  @NotNull
  @Column(name = "_CREATED")
  protected Long created;

  @Version
  @Column(name = "_VERSION")
  protected Long version;

  @Column(name = "_INSTANCE_ID")
  protected String instanceId;

  @Column(name = "_VALUE_TYPE")
  protected String valueType;

  @Column(name = "_PROPERTY_KEY")
  protected String propertyKey;

  @Column(name = "_STRING_VALUE")
  protected String stringValue;

  @Column(name = "_LONG_VALUE")
  protected Long longValue;

  @Column(name = "_DOUBLE_VALUE")
  protected Double doubleValue;

  @Column(name = "_BYTE_ARRAY_VALUE")
  protected byte[] byteArrayValue;

  public String getId() {
    return id;
  }

  public InstancePropertyEntity withId(String id) {
    this.id = id;
    return this;
  }

  public Long getCreated() {
    return created;
  }

  public InstancePropertyEntity withCreated(Long created) {
    this.created = created;
    return this;
  }

  public Long getVersion() {
    return version;
  }

  public InstancePropertyEntity withVersion(Long version) {
    this.version = version;
    return this;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public InstancePropertyEntity withInstanceId(String instanceId) {
    this.instanceId = instanceId;
    return this;
  }

  public String getValueType() {
    return valueType;
  }

  public InstancePropertyEntity withValueType(String valueType) {
    this.valueType = valueType;
    return this;
  }

  public String getPropertyKey() {
    return propertyKey;
  }

  public InstancePropertyEntity withPropertyKey(String propertyKey) {
    this.propertyKey = propertyKey;
    return this;
  }

  public String getStringValue() {
    return stringValue;
  }

  public InstancePropertyEntity withStringValue(String stringValue) {
    this.stringValue = stringValue;
    return this;
  }

  public Long getLongValue() {
    return longValue;
  }

  public InstancePropertyEntity withLongValue(Long longValue) {
    this.longValue = longValue;
    return this;
  }

  public Double getDoubleValue() {
    return doubleValue;
  }

  public InstancePropertyEntity withDoubleValue(Double doubleValue) {
    this.doubleValue = doubleValue;
    return this;
  }

  public byte[] getByteArrayValue() {
    return byteArrayValue;
  }

  public InstancePropertyEntity withByteArrayValue(byte[] byteArrayValue) {
    this.byteArrayValue = byteArrayValue;
    return this;
  }
}
