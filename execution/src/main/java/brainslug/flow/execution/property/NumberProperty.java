package brainslug.flow.execution.property;

public abstract class NumberProperty<T extends Number> extends AbstractProperty<T> {
    public NumberProperty(String key, T value) {
        super(key, value);
    }
}
