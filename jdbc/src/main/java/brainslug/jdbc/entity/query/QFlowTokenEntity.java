package brainslug.jdbc.entity.query;

import brainslug.jdbc.entity.FlowTokenEntity;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

public class QFlowTokenEntity extends EntityPathBase<FlowTokenEntity> {

    private static final long serialVersionUID = 290861597L;

    public static final QFlowTokenEntity flowTokenEntity = new QFlowTokenEntity("flowTokenEntity");

    public final StringPath id = createString("id");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public final NumberPath<Long> created = createNumber("created", Long.class);

    public final StringPath currentNode = createString("currentNode");

    public final StringPath flowInstanceId = createString("flowInstanceId");

    public final NumberPath<Integer> isDead = createNumber("isDead", Integer.class);

    public final StringPath sourceNode = createString("sourceNode");

    public QFlowTokenEntity(String variable) {
        super(FlowTokenEntity.class, forVariable(variable));
    }

    public QFlowTokenEntity(Path<? extends FlowTokenEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFlowTokenEntity(PathMetadata<?> metadata) {
        super(FlowTokenEntity.class, metadata);
    }

}

