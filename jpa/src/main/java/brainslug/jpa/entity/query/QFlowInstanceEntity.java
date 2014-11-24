package brainslug.jpa.entity.query;

import brainslug.jpa.entity.FlowInstanceEntity;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

public class QFlowInstanceEntity extends EntityPathBase<FlowInstanceEntity> {

    private static final long serialVersionUID = 290861594L;

    public static final QFlowInstanceEntity flowInstanceEntity = new QFlowInstanceEntity("flowInstanceEntity");

    public final StringPath id = createString("id");

    public final NumberPath<Long> created = createNumber("created", Long.class);

    public final StringPath definitionId = createString("definitionId");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QFlowInstanceEntity(String variable) {
        super(FlowInstanceEntity.class, forVariable(variable));
    }

    public QFlowInstanceEntity(Path<? extends FlowInstanceEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFlowInstanceEntity(PathMetadata<?> metadata) {
        super(FlowInstanceEntity.class, metadata);
    }

}

