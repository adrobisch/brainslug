package brainslug.jpa.entity.query;

import brainslug.jpa.entity.AsyncTaskErrorDetailsEntity;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import javax.annotation.Generated;

import static com.mysema.query.types.PathMetadataFactory.forVariable;


/**
 * QAsyncTaskErrorDetailsEntity is a Querydsl query type for AsyncTaskErrorDetailsEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAsyncTaskErrorDetailsEntity extends EntityPathBase<AsyncTaskErrorDetailsEntity> {

    private static final long serialVersionUID = 1732617939L;

    public static final QAsyncTaskErrorDetailsEntity asyncTaskErrorDetailsEntity = new QAsyncTaskErrorDetailsEntity("asyncTaskErrorDetailsEntity");

    public final NumberPath<Long> created = createNumber("created", Long.class);

    public final StringPath exceptionType = createString("exceptionType");

    public final StringPath id = createString("id");

    public final StringPath message = createString("message");

    public final StringPath stackTrace = createString("stackTrace");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QAsyncTaskErrorDetailsEntity(String variable) {
        super(AsyncTaskErrorDetailsEntity.class, forVariable(variable));
    }

    public QAsyncTaskErrorDetailsEntity(Path<? extends AsyncTaskErrorDetailsEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAsyncTaskErrorDetailsEntity(PathMetadata<?> metadata) {
        super(AsyncTaskErrorDetailsEntity.class, metadata);
    }

}

