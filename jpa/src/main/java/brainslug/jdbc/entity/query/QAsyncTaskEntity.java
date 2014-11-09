package brainslug.jdbc.entity.query;

import brainslug.jdbc.entity.AsyncTaskEntity;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

public class QAsyncTaskEntity extends EntityPathBase<AsyncTaskEntity> {

    private static final long serialVersionUID = 290861591L;

    public static final QAsyncTaskEntity asyncTaskEntity = new QAsyncTaskEntity("asyncTaskEntity");

    public final NumberPath<Long> created = createNumber("created", Long.class);

    public final StringPath definitionId = createString("definitionId");

    public final NumberPath<Long> dueDate = createNumber("dueDate", Long.class);

    public final StringPath id = createString("id");

    public final StringPath instanceId = createString("instanceId");

    public final NumberPath<Long> maxRetries = createNumber("maxRetries", Long.class);

    public final NumberPath<Long> retries = createNumber("retries", Long.class);

    public final StringPath taskNodeId = createString("taskNodeId");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QAsyncTaskEntity(String variable) {
        super(AsyncTaskEntity.class, forVariable(variable));
    }

    public QAsyncTaskEntity(Path<? extends AsyncTaskEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAsyncTaskEntity(PathMetadata<?> metadata) {
        super(AsyncTaskEntity.class, metadata);
    }

}

