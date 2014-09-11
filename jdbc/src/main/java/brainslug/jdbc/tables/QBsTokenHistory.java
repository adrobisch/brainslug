package brainslug.jdbc.tables;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBsTokenHistory is a Querydsl query type for QBsTokenHistory
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBsTokenHistory extends com.mysema.query.sql.RelationalPathBase<QBsTokenHistory> {

    private static final long serialVersionUID = -1434234118;

    public static final QBsTokenHistory bsTokenHistory = new QBsTokenHistory("BS_TOKEN_HISTORY");

    public final NumberPath<Integer> flowInstanceId = createNumber("flowInstanceId", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath lastNode = createString("lastNode");

    public final StringPath sourceNode = createString("sourceNode");

    public QBsTokenHistory(String variable) {
        super(QBsTokenHistory.class, forVariable(variable), "PUBLIC", "BS_TOKEN_HISTORY");
        addMetadata();
    }

    public QBsTokenHistory(String variable, String schema, String table) {
        super(QBsTokenHistory.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBsTokenHistory(Path<? extends QBsTokenHistory> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "BS_TOKEN_HISTORY");
        addMetadata();
    }

    public QBsTokenHistory(PathMetadata<?> metadata) {
        super(QBsTokenHistory.class, metadata, "PUBLIC", "BS_TOKEN_HISTORY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(flowInstanceId, ColumnMetadata.named("FLOW_INSTANCE_ID").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(lastNode, ColumnMetadata.named("LAST_NODE").withIndex(3).ofType(Types.VARCHAR).withSize(100).notNull());
        addMetadata(sourceNode, ColumnMetadata.named("SOURCE_NODE").withIndex(4).ofType(Types.VARCHAR).withSize(100).notNull());
    }

}

