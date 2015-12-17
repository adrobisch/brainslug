package brainslug.jpa.entity;

import brainslug.flow.execution.property.BooleanProperty;
import brainslug.flow.execution.property.DateProperty;
import brainslug.flow.execution.property.DoubleProperty;
import brainslug.flow.execution.property.FloatProperty;
import brainslug.flow.execution.property.IntProperty;
import brainslug.flow.execution.property.LongProperty;
import brainslug.flow.execution.property.ObjectProperty;
import brainslug.flow.execution.property.StringProperty;
import brainslug.flow.instance.FlowInstanceProperty;
import brainslug.jpa.util.ObjectSerializer;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;

import static brainslug.jpa.entity.InstancePropertyEntity.ValueType.BOOLEAN;
import static brainslug.jpa.entity.InstancePropertyEntity.ValueType.DATE;
import static brainslug.jpa.entity.InstancePropertyEntity.ValueType.DOUBLE;
import static brainslug.jpa.entity.InstancePropertyEntity.ValueType.FLOAT;
import static brainslug.jpa.entity.InstancePropertyEntity.ValueType.INT;
import static brainslug.jpa.entity.InstancePropertyEntity.ValueType.LONG;
import static brainslug.jpa.entity.InstancePropertyEntity.ValueType.SERIALIZABLE;
import static brainslug.jpa.entity.InstancePropertyEntity.ValueType.STRING;
import static brainslug.util.Preconditions.notNull;

@Entity
@Table(name = "BS_INSTANCE_PROPERTY")
public class InstancePropertyEntity implements FlowInstanceProperty {
  @Id
  @Column(name = "_ID")
  protected String id;

  @NotNull
  @Column(name = "_CREATED")
  protected Long created;

  @Version
  @Column(name = "_VERSION")
  protected Long version;

  @NotNull
  @Column(name = "_INSTANCE_ID")
  protected String instanceId;

  @Column(name = "_VALUE_TYPE")
  protected String valueType;

  @Column(name = "_PROPERTY_KEY")
  protected String propertyKey;

  @Lob
  @Type(type = "org.hibernate.type.StringType")
  @Column(name = "_STRING_VALUE", length = 4000)
  protected String stringValue;

  @Column(name = "_LONG_VALUE")
  protected Long longValue;

  @Column(name = "_DOUBLE_VALUE")
  protected Double doubleValue;

  @Type(type = "org.hibernate.type.BlobType")
  @Column(name = "_BLOB_VALUE")
  protected Blob blobValue;

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

  public InstancePropertyEntity withBlobValue(Blob blobValue) {
    this.blobValue = blobValue;
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

  @Override
  public String getKey() {
    return propertyKey;
  }

  @Override
  public Object getValue() {
    return propertyForType(getKey(),
        getValueType(),
        getStringValue(),
        getLongValue(),
        getDoubleValue()).getValue();
  }

  @Override
  public boolean isTransient() {
    return false;
  }

  public static class ValueType {
    public static final ValueType STRING = new ValueType("string");
    public static final ValueType DATE = new ValueType("date");
    public static final ValueType INT = new ValueType("int");
    public static final ValueType DOUBLE = new ValueType("double");
    public static final ValueType FLOAT = new ValueType("float");
    public static final ValueType BOOLEAN = new ValueType("boolean");
    public static final ValueType SERIALIZABLE = new ValueType("serializable");
    public static final ValueType LONG = new ValueType("long");

    String typeName;

    public ValueType(String typeName) {
      this.typeName = typeName;
    }

    public String typeName() {
      return typeName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ValueType valueType = (ValueType) o;

      if (typeName != null ? !typeName.equals(valueType.typeName) : valueType.typeName != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return typeName != null ? typeName.hashCode() : 0;
    }
  }

  FlowInstanceProperty propertyForType(String key, String type, String stringValue, Long longValue, Double doubleValue) {
    InstancePropertyEntity.ValueType valueType = new InstancePropertyEntity.ValueType(type);

    if (valueType.equals(STRING)) {
      return new StringProperty(key, stringValue);
    } else if(valueType.equals(LONG)) {
      return new LongProperty(key, longValue);
    } else if(valueType.equals(DATE)) {
      return new DateProperty(key, new Date(longValue));
    } else if(valueType.equals(INT)) {
      return new IntProperty(key, longValue.intValue());
    } else if(valueType.equals(DOUBLE)) {
      return new DoubleProperty(key, doubleValue);
    } else if(valueType.equals(FLOAT)) {
      return new FloatProperty(key, doubleValue.floatValue());
    } else if(valueType.equals(BOOLEAN)) {
      return new BooleanProperty(key, longValue == 1);
    } else if(valueType.equals(SERIALIZABLE)) {
      byte[] inputStream = bytesFromBlob(notNull(blobValue));
      return new ObjectProperty(key, new ObjectSerializer().deserialize(inputStream));
    } else {
      throw new IllegalArgumentException("unhandled value type:" + valueType);
    }
  }

  private byte[] bytesFromBlob(Blob blob)  {
    try {
      return toBytes(notNull(blob.getBinaryStream()));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  byte[] toBytes(InputStream inputStream) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int nRead;
    byte[] data = new byte[16384];
    try {
      while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }

      buffer.flush();

      return buffer.toByteArray();
    } catch (IOException ioError) {
      throw new RuntimeException(ioError);
    }

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    InstancePropertyEntity that = (InstancePropertyEntity) o;

    if (instanceId != null ? !instanceId.equals(that.instanceId) : that.instanceId != null) return false;
    if (propertyKey != null ? !propertyKey.equals(that.propertyKey) : that.propertyKey != null) return false;
    if (valueType != null ? !valueType.equals(that.valueType) : that.valueType != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = instanceId != null ? instanceId.hashCode() : 0;
    result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
    result = 31 * result + (propertyKey != null ? propertyKey.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "InstancePropertyEntity{" +
      "id='" + id + '\'' +
      ", created=" + created +
      ", version=" + version +
      ", instanceId='" + instanceId + '\'' +
      ", valueType='" + valueType + '\'' +
      ", propertyKey='" + propertyKey + '\'' +
      ", stringValue='" + stringValue + '\'' +
      ", longValue=" + longValue +
      ", doubleValue=" + doubleValue +
      '}';
  }
}
