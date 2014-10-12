package brainslug.jdbc.table;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QFlowInstance is a Querydsl query type for QFlowInstance
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFlowInstance extends com.mysema.query.sql.RelationalPathBase<QFlowInstance> {

    private static final long serialVersionUID = -1519674607;

    public static final QFlowInstance flowInstance = new QFlowInstance("FLOW_INSTANCE");

    public final NumberPath<Long> created = createNumber("created", Long.class);

    public final StringPath definitionId = createString("definitionId");

    public final StringPath id = createString("id");

    public final com.mysema.query.sql.PrimaryKey<QFlowInstance> constraintC = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QFlowToken> _constraint80 = createInvForeignKey(id, "FLOW_INSTANCE_ID");

    public QFlowInstance(String variable) {
        super(QFlowInstance.class, forVariable(variable), "PUBLIC", "FLOW_INSTANCE");
        addMetadata();
    }

    public QFlowInstance(String variable, String schema, String table) {
        super(QFlowInstance.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFlowInstance(Path<? extends QFlowInstance> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FLOW_INSTANCE");
        addMetadata();
    }

    public QFlowInstance(PathMetadata<?> metadata) {
        super(QFlowInstance.class, metadata, "PUBLIC", "FLOW_INSTANCE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(created, ColumnMetadata.named("CREATED").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(definitionId, ColumnMetadata.named("DEFINITION_ID").withIndex(2).ofType(Types.VARCHAR).withSize(40).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.VARCHAR).withSize(40).notNull());
    }

}

