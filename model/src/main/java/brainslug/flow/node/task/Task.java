package brainslug.flow.node.task;

public interface Task<T> {
    void execute(T context);
}
