package brainslug.flow.builder;

import java.lang.reflect.InvocationHandler;

public interface ServiceProxyFactory {
    <T> Object createProxyInstance(Class<T> clazz, InvocationHandler handler);
}
