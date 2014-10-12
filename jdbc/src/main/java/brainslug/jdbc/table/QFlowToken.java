package brainslug.jdbc.table;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QFlowToken is a Querydsl query type for QFlowToken
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFlowToken extends com.mysema.query.sql.RelationalPathBase<QFlowToken> {

    private static final long serialVersionUID = 1561830653;

    public static final QFlowToken flowToken = new QFlowToken("FLOW_TOKEN");

    public final NumberPath<Long> created = createNumber("created", Long.class);

    public final StringPath currentNode = createString("currentNode");

    public final StringPath flowInstanceId = createString("flowInstanceId");

    public final StringPath id = createString("id");

    public final NumberPath<Integer> isDead = createNumber("isDead", Integer.class);

    public final StringPath sourceNode = createString("sourceNode");

    public final com.mysema.query.sql.PrimaryKey<QFlowToken> constraint8 = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QFlowInstance> constraint80 = createForeignKey(flowInstanceId, "ID");

    public QFlowToken(String variable) {
        super(QFlowToken.class, forVariable(variable), "PUBLIC", "FLOW_TOKEN");
        addMetadata();
    }

    public QFlowToken(String variable, String schema, String table) {
        super(QFlowToken.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFlowToken(Path<? extends QFlowToken> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FLOW_TOKEN");
        addMetadata();
    }

    public QFlowToken(PathMetadata<?> metadata) {
        super(QFlowToken.class, metadata, "PUBLIC", "FLOW_TOKEN");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(created, ColumnMetadata.named("CREATED").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(currentNode, ColumnMetadata.named("CURRENT_NODE").withIndex(4).ofType(Types.VARCHAR).withSize(100).notNull());
        addMetadata(flowInstanceId, ColumnMetadata.named("FLOW_INSTANCE_ID").withIndex(3).ofType(Types.VARCHAR).withSize(40).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.VARCHAR).withSize(40).notNull());
        addMetadata(isDead, ColumnMetadata.named("IS_DEAD").withIndex(6).ofType(Types.INTEGER).withSize(10));
        addMetadata(sourceNode, ColumnMetadata.named("SOURCE_NODE").withIndex(5).ofType(Types.VARCHAR).withSize(100));
    }

}

