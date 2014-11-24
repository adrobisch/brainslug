package brainslug.jpa.table;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QInstanceProperty is a Querydsl query type for QInstanceProperty
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QInstanceProperty extends com.mysema.query.sql.RelationalPathBase<QInstanceProperty> {

    private static final long serialVersionUID = -1079594245;

    public static final QInstanceProperty instanceProperty = new QInstanceProperty("INSTANCE_PROPERTY");

    public final SimplePath<java.sql.Blob> byteArrayValue = createSimple("byteArrayValue", java.sql.Blob.class);

    public final NumberPath<Long> created = createNumber("created", Long.class);

    public final NumberPath<java.math.BigDecimal> doubleValue = createNumber("doubleValue", java.math.BigDecimal.class);

    public final StringPath id = createString("id");

    public final StringPath instanceId = createString("instanceId");

    public final NumberPath<Long> longValue = createNumber("longValue", Long.class);

    public final StringPath propertyKey = createString("propertyKey");

    public final StringPath stringValue = createString("stringValue");

    public final StringPath valueType = createString("valueType");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QInstanceProperty> constraintA = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QFlowInstance> constraintA9 = createForeignKey(instanceId, "ID");

    public QInstanceProperty(String variable) {
        super(QInstanceProperty.class, forVariable(variable), "PUBLIC", "INSTANCE_PROPERTY");
        addMetadata();
    }

    public QInstanceProperty(String variable, String schema, String table) {
        super(QInstanceProperty.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QInstanceProperty(Path<? extends QInstanceProperty> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "INSTANCE_PROPERTY");
        addMetadata();
    }

    public QInstanceProperty(PathMetadata<?> metadata) {
        super(QInstanceProperty.class, metadata, "PUBLIC", "INSTANCE_PROPERTY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(byteArrayValue, ColumnMetadata.named("BYTE_ARRAY_VALUE").withIndex(10).ofType(Types.BLOB).withSize(2147483647));
        addMetadata(created, ColumnMetadata.named("CREATED").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(doubleValue, ColumnMetadata.named("DOUBLE_VALUE").withIndex(9).ofType(Types.DECIMAL).withSize(20).withDigits(10));
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.VARCHAR).withSize(40).notNull());
        addMetadata(instanceId, ColumnMetadata.named("INSTANCE_ID").withIndex(4).ofType(Types.VARCHAR).withSize(40).notNull());
        addMetadata(longValue, ColumnMetadata.named("LONG_VALUE").withIndex(8).ofType(Types.BIGINT).withSize(19));
        addMetadata(propertyKey, ColumnMetadata.named("PROPERTY_KEY").withIndex(6).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(stringValue, ColumnMetadata.named("STRING_VALUE").withIndex(7).ofType(Types.VARCHAR).withSize(4000));
        addMetadata(valueType, ColumnMetadata.named("VALUE_TYPE").withIndex(5).ofType(Types.VARCHAR).withSize(1024).notNull());
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

