package com.github.smartretry.core.util;

import com.github.smartretry.core.IllegalRetryException;
import com.github.smartretry.core.RetryFunction;
import com.github.smartretry.core.RetryHandler;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RetryHandlerUtilsTest {

    static class OrderService {

        public void insert() {

        }

        @RetryFunction(identity = "order.payment")
        public void payment() {

        }

        @RetryFunction(identity = "order.payment2")
        public void payment2(String orderId) {

        }

        @RetryFunction(identity = "order.payment3")
        public void payment3(String orderId, String userId) {

        }

        @RetryFunction(identity = "order.payment4")
        public void payment4(Object order) {

        }

        @RetryFunction(identity = "")
        public void payment5(List list) {

        }

        @RetryFunction(identity = "")
        public void payment6(Map map) {

        }
    }

    @Test
    public void testIsRetryFunctionMethod() {
        Assertions.assertThat(RetryHandlerUtils.isRetryFunctionMethod(getMethodByName(OrderService.class, "insert"))).isFalse();
        Assertions.assertThat(RetryHandlerUtils.isRetryFunctionMethod(getMethodByName(OrderService.class, "payment"))).isFalse();
        Assertions.assertThat(RetryHandlerUtils.isRetryFunctionMethod(getMethodByName(OrderService.class, "payment2"))).isTrue();
        Assertions.assertThat(RetryHandlerUtils.isRetryFunctionMethod(getMethodByName(OrderService.class, "payment3"))).isFalse();
        Assertions.assertThat(RetryHandlerUtils.isRetryFunctionMethod(getMethodByName(OrderService.class, "payment4"))).isFalse();
    }

    static class ObjectRetryHandler implements RetryHandler {
        @Override
        public String identity() {
            return null;
        }

        @Override
        public Object handle(Object arg) {
            return null;
        }
    }

    static class User {

    }

    static class UserRetryHandler implements RetryHandler<User, Void> {
        @Override
        public String identity() {
            return null;
        }

        @Override
        public Void handle(User arg) {
            return null;
        }
    }

    static class GenericUserRetryHandler extends UserRetryHandler {

    }

    static class GenericRetryHandler extends ObjectRetryHandler {

    }

    static class ListRetryHandler implements RetryHandler<List, Void> {
        @Override
        public String identity() {
            return null;
        }

        @Override
        public Void handle(List arg) {
            return null;
        }
    }

    static class ListUserRetryHandler implements RetryHandler<List<User>, Void> {
        @Override
        public String identity() {
            return null;
        }

        @Override
        public Void handle(List<User> arg) {
            return null;
        }
    }

    static class MapRetryHandler implements RetryHandler<Map, Void> {
        @Override
        public String identity() {
            return null;
        }

        @Override
        public Void handle(Map arg) {
            return null;
        }
    }

    @Test
    public void testIsRetryHandlerMethod() {
        Assertions.assertThat(RetryHandlerUtils.isRetryHandlerMethod(ObjectRetryHandler.class, getMethodByName(ObjectRetryHandler.class, "handle"))).isTrue();
        Assertions.assertThat(RetryHandlerUtils.isRetryHandlerMethod(GenericRetryHandler.class, getMethodByName(GenericRetryHandler.class, "handle"))).isTrue();
    }

    @Test(expected = IllegalRetryException.class)
    public void testValidateRetryFunction() {
        RetryHandlerUtils.validateRetryFunction(getMethodByName(OrderService.class, "payment4"));
    }

    @Test(expected = IllegalRetryException.class)
    public void testValidateRetryFunctionWithList() {
        RetryHandlerUtils.validateRetryFunction(getMethodByName(OrderService.class, "payment5"));
    }

    @Test(expected = IllegalRetryException.class)
    public void testValidateRetryFunctionWithMap() {
        RetryHandlerUtils.validateRetryFunction(getMethodByName(OrderService.class, "payment6"));
    }

    @Test(expected = IllegalRetryException.class)
    public void testValidateRetryFunctionWithNothing() {
        RetryHandlerUtils.validateRetryFunction(getMethodByName(OrderService.class, "insert"));
    }

    @Test
    public void testGetMethodIdentity() {
        String identityName = "com.github.smartretry.core.util.RetryHandlerUtilsTest$OrderService.insert";
        Assertions.assertThat(RetryHandlerUtils.getMethodIdentity(getMethodByName(OrderService.class, "insert"))).isEqualTo(identityName);
    }

    @Test(expected = IllegalRetryException.class)
    public void testValidateRetryHandlerWithObject() {
        RetryHandlerUtils.validateRetryHandler(ObjectRetryHandler.class);
    }

    @Test(expected = IllegalRetryException.class)
    public void testValidateRetryHandlerWithList() {
        RetryHandlerUtils.validateRetryHandler(ListRetryHandler.class);
    }

    @Test(expected = IllegalRetryException.class)
    public void testValidateRetryHandlerWithListUser() {
        RetryHandlerUtils.validateRetryHandler(ListUserRetryHandler.class);
    }

    @Test(expected = IllegalRetryException.class)
    public void testValidateRetryHandlerWithMap() {
        RetryHandlerUtils.validateRetryHandler(MapRetryHandler.class);
    }

    @Test
    public void testValidateRetryHandlerWithUser() {
        RetryHandlerUtils.validateRetryHandler(UserRetryHandler.class);
        RetryHandlerUtils.validateRetryHandler(GenericUserRetryHandler.class);
    }

    private Method getMethodByName(Class<?> clazz, String name) {
        return Stream.of(clazz.getMethods()).filter(m -> m.getName().equals(name)).findAny().get();
    }
}
