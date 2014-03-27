package brainslug.flow.execution;

/**
 * Marker interface for the inline creation of task handlers to be used
 * in a {@link brainslug.flow.model.HandlerCallDefinition}.
 *
 * Implementations of this interface typically have one method with a
 * {@link brainslug.flow.execution.Execute} annotation.
 */
public interface TaskHandler {
}
