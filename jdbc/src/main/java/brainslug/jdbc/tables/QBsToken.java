package brainslug.jdbc.tables;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBsToken is a Querydsl query type for QBsToken
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBsToken extends com.mysema.query.sql.RelationalPathBase<QBsToken> {

    private static final long serialVersionUID = -1344089030;

    public static final QBsToken bsToken = new QBsToken("BS_TOKEN");

    public final StringPath currentNode = createString("currentNode");

    public final NumberPath<Integer> flowInstanceId = createNumber("flowInstanceId", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath sourceNode = createString("sourceNode");

    public QBsToken(String variable) {
        super(QBsToken.class, forVariable(variable), "PUBLIC", "BS_TOKEN");
        addMetadata();
    }

    public QBsToken(String variable, String schema, String table) {
        super(QBsToken.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBsToken(Path<? extends QBsToken> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "BS_TOKEN");
        addMetadata();
    }

    public QBsToken(PathMetadata<?> metadata) {
        super(QBsToken.class, metadata, "PUBLIC", "BS_TOKEN");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(currentNode, ColumnMetadata.named("CURRENT_NODE").withIndex(3).ofType(Types.VARCHAR).withSize(100).notNull());
        addMetadata(flowInstanceId, ColumnMetadata.named("FLOW_INSTANCE_ID").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(sourceNode, ColumnMetadata.named("SOURCE_NODE").withIndex(4).ofType(Types.VARCHAR).withSize(100).notNull());
    }

}

