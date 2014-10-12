package brainslug.jdbc.table;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QAsyncTaskErrorDetails is a Querydsl query type for QAsyncTaskErrorDetails
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QAsyncTaskErrorDetails extends com.mysema.query.sql.RelationalPathBase<QAsyncTaskErrorDetails> {

    private static final long serialVersionUID = 1556372528;

    public static final QAsyncTaskErrorDetails asyncTaskErrorDetails = new QAsyncTaskErrorDetails("ASYNC_TASK_ERROR_DETAILS");

    public final StringPath asyncTaskId = createString("asyncTaskId");

    public final NumberPath<Long> created = createNumber("created", Long.class);

    public final StringPath exceptionType = createString("exceptionType");

    public final StringPath id = createString("id");

    public final StringPath message = createString("message");

    public final SimplePath<java.sql.Blob> stackTrace = createSimple("stackTrace", java.sql.Blob.class);

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QAsyncTaskErrorDetails> constraint3 = createPrimaryKey(id);

    public QAsyncTaskErrorDetails(String variable) {
        super(QAsyncTaskErrorDetails.class, forVariable(variable), "PUBLIC", "ASYNC_TASK_ERROR_DETAILS");
        addMetadata();
    }

    public QAsyncTaskErrorDetails(String variable, String schema, String table) {
        super(QAsyncTaskErrorDetails.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QAsyncTaskErrorDetails(Path<? extends QAsyncTaskErrorDetails> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "ASYNC_TASK_ERROR_DETAILS");
        addMetadata();
    }

    public QAsyncTaskErrorDetails(PathMetadata<?> metadata) {
        super(QAsyncTaskErrorDetails.class, metadata, "PUBLIC", "ASYNC_TASK_ERROR_DETAILS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(asyncTaskId, ColumnMetadata.named("ASYNC_TASK_ID").withIndex(2).ofType(Types.VARCHAR).withSize(40).notNull());
        addMetadata(created, ColumnMetadata.named("CREATED").withIndex(6).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(exceptionType, ColumnMetadata.named("EXCEPTION_TYPE").withIndex(4).ofType(Types.VARCHAR).withSize(1024).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.VARCHAR).withSize(40).notNull());
        addMetadata(message, ColumnMetadata.named("MESSAGE").withIndex(5).ofType(Types.VARCHAR).withSize(2048));
        addMetadata(stackTrace, ColumnMetadata.named("STACK_TRACE").withIndex(3).ofType(Types.BLOB).withSize(2147483647));
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(7).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

