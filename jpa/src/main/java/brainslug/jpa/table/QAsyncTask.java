package brainslug.jpa.table;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QAsyncTask is a Querydsl query type for QAsyncTask
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QAsyncTask extends com.mysema.query.sql.RelationalPathBase<QAsyncTask> {

    private static final long serialVersionUID = -1884612144;

    public static final QAsyncTask asyncTask = new QAsyncTask("ASYNC_TASK");

    public final NumberPath<Long> created = createNumber("created", Long.class);

    public final StringPath definitionId = createString("definitionId");

    public final NumberPath<Long> dueDate = createNumber("dueDate", Long.class);

    public final StringPath errorDetailsId = createString("errorDetailsId");

    public final StringPath id = createString("id");

    public final StringPath instanceId = createString("instanceId");

    public final NumberPath<Long> maxRetries = createNumber("maxRetries", Long.class);

    public final NumberPath<Long> retries = createNumber("retries", Long.class);

    public final StringPath taskNodeId = createString("taskNodeId");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QAsyncTask> constraintCb = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QAsyncTaskErrorDetails> constraintCbf = createForeignKey(errorDetailsId, "ID");

    public QAsyncTask(String variable) {
        super(QAsyncTask.class, forVariable(variable), "PUBLIC", "ASYNC_TASK");
        addMetadata();
    }

    public QAsyncTask(String variable, String schema, String table) {
        super(QAsyncTask.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QAsyncTask(Path<? extends QAsyncTask> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "ASYNC_TASK");
        addMetadata();
    }

    public QAsyncTask(PathMetadata<?> metadata) {
        super(QAsyncTask.class, metadata, "PUBLIC", "ASYNC_TASK");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(created, ColumnMetadata.named("CREATED").withIndex(6).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(definitionId, ColumnMetadata.named("DEFINITION_ID").withIndex(4).ofType(Types.VARCHAR).withSize(40).notNull());
        addMetadata(dueDate, ColumnMetadata.named("DUE_DATE").withIndex(7).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(errorDetailsId, ColumnMetadata.named("ERROR_DETAILS_ID").withIndex(5).ofType(Types.VARCHAR).withSize(40));
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.VARCHAR).withSize(40).notNull());
        addMetadata(instanceId, ColumnMetadata.named("INSTANCE_ID").withIndex(3).ofType(Types.VARCHAR).withSize(40).notNull());
        addMetadata(maxRetries, ColumnMetadata.named("MAX_RETRIES").withIndex(9).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(retries, ColumnMetadata.named("RETRIES").withIndex(8).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(taskNodeId, ColumnMetadata.named("TASK_NODE_ID").withIndex(2).ofType(Types.VARCHAR).withSize(40).notNull());
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(10).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

