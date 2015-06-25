package brainslug.spring;

import brainslug.flow.builder.ServiceProxyFactory;
import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SpringServiceProxyFactory implements ServiceProxyFactory {

    static class CglibHandlerWrapper implements org.springframework.cglib.proxy.InvocationHandler {
        InvocationHandler invocationHandler;

        public CglibHandlerWrapper(InvocationHandler invocationHandler) {
            this.invocationHandler = invocationHandler;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            return invocationHandler.invoke(o, method, objects);
        }
    }

    @Override
    public <T> Object createProxyInstance(Class<T> clazz, final InvocationHandler handler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new CglibHandlerWrapper(handler));
        return enhancer.create();
    }
}
