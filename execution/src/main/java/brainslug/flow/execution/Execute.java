package brainslug.flow.execution;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a method which is meant for execution in a {@link brainslug.flow.model.HandlerCallDefinition} callee.
 * Arguments for this method will provided from the {@link brainslug.flow.context.Registry}.
 * Its also possible to obtain the {@link brainslug.flow.execution.ExecutionContext} this way.
 *
 * @see brainslug.flow.execution.impl.TaskNodeExecutor and the corresponding test
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Execute {
}
