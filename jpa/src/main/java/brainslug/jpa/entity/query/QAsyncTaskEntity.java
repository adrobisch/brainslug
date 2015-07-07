package brainslug.jpa.entity.query;

import brainslug.jpa.entity.AsyncTaskEntity;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.PathInits;
import com.mysema.query.types.path.StringPath;

import javax.annotation.Generated;

import static com.mysema.query.types.PathMetadataFactory.forVariable;


/**
 * QAsyncTaskEntity is a Querydsl query type for AsyncTaskEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAsyncTaskEntity extends EntityPathBase<AsyncTaskEntity> {

    private static final long serialVersionUID = -940132327L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAsyncTaskEntity asyncTaskEntity = new QAsyncTaskEntity("asyncTaskEntity");

    public final NumberPath<Long> created = createNumber("created", Long.class);

    public final StringPath definitionId = createString("definitionId");

    public final NumberPath<Long> dueDate = createNumber("dueDate", Long.class);

    public final QAsyncTaskErrorDetailsEntity errorDetails;

    public final StringPath id = createString("id");

    public final StringPath instanceId = createString("instanceId");

    public final NumberPath<Long> maxRetries = createNumber("maxRetries", Long.class);

    public final NumberPath<Long> retries = createNumber("retries", Long.class);

    public final StringPath taskNodeId = createString("taskNodeId");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QAsyncTaskEntity(String variable) {
        this(AsyncTaskEntity.class, forVariable(variable), INITS);
    }

    public QAsyncTaskEntity(Path<? extends AsyncTaskEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAsyncTaskEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAsyncTaskEntity(PathMetadata<?> metadata, PathInits inits) {
        this(AsyncTaskEntity.class, metadata, inits);
    }

    public QAsyncTaskEntity(Class<? extends AsyncTaskEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.errorDetails = inits.isInitialized("errorDetails") ? new QAsyncTaskErrorDetailsEntity(forProperty("errorDetails")) : null;
    }

}

