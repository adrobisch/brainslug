package brainslug.flow.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class JdkServiceProxyFactory implements ServiceProxyFactory {
    @Override
    public <T> Object createProxyInstance(Class<T> clazz, InvocationHandler handler) {
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz}, handler);
    }
}
