package brainslug.jpa.entity.query;

import brainslug.jpa.entity.AsyncTaskErrorDetailsEntity;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.SimplePath;
import com.mysema.query.types.path.StringPath;

import static com.mysema.query.types.PathMetadataFactory.forVariable;


public class QAsyncTaskErrorDetails extends EntityPathBase<AsyncTaskErrorDetailsEntity> {

  private static final long serialVersionUID = 1554372528;

  public static final QAsyncTaskErrorDetails asyncTaskErrorDetails = new QAsyncTaskErrorDetails("ASYNC_TASK_ERROR_DETAILS");

  public final NumberPath<Long> created = createNumber("created", Long.class);

  public final StringPath exceptionType = createString("exceptionType");

  public final StringPath id = createString("id");

  public final StringPath message = createString("message");

  public final SimplePath<byte[]> stackTrace = createSimple("stackTrace", byte[].class);

  public final NumberPath<Long> version = createNumber("version", Long.class);

  public QAsyncTaskErrorDetails(String variable) {
    super(AsyncTaskErrorDetailsEntity.class, forVariable(variable));
  }

  public QAsyncTaskErrorDetails(Path<? extends AsyncTaskErrorDetailsEntity> path) {
    super(path.getType(), path.getMetadata());
  }

  public QAsyncTaskErrorDetails(PathMetadata<?> metadata) {
    super(AsyncTaskErrorDetailsEntity.class, metadata);
  }

}

