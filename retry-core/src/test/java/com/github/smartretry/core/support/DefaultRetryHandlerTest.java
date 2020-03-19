package com.github.smartretry.core.support;

import com.github.smartretry.core.IllegalRetryException;
import com.github.smartretry.core.RetryHandler;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;

public class DefaultRetryHandlerTest {

    static class MyStringRetryHandler implements RetryHandler<String, Integer> {

        @Override
        public String identity() {
            return null;
        }

        @Override
        public Integer handle(String arg) {
            return null;
        }
    }

    static class MyIntegerRetryHandler implements RetryHandler<Integer, String> {

        @Override
        public String identity() {
            return null;
        }

        @Override
        public String handle(Integer arg) {
            return null;
        }
    }

    static class MyRetryHandler implements RetryHandler {

        @Override
        public String identity() {
            return null;
        }

        @Override
        public Object handle(Object arg) {
            return null;
        }
    }

    static class MyObjectRetryHandler implements RetryHandler<Object, Object> {

        @Override
        public String identity() {
            return null;
        }

        @Override
        public Object handle(Object arg) {
            return null;
        }
    }

    static class ListRetryHandler implements RetryHandler<List, Object> {

        @Override
        public String identity() {
            return null;
        }

        @Override
        public Object handle(List arg) {
            return null;
        }
    }

    static class User {

    }

    static class UserListRetryHandler implements RetryHandler<List<User>, Object> {

        @Override
        public String identity() {
            return null;
        }

        @Override
        public Object handle(List<User> arg) {
            return null;
        }
    }

    /**
     * 有泛型
     */
    @Test
    public void testGetInputArgsType() {
        DefaultRetryHandler defaultRetryHandler = new DefaultRetryHandler(new MyStringRetryHandler());
        Assertions.assertThat(defaultRetryHandler.getInputArgsType()).isEqualTo(String.class);

        defaultRetryHandler = new DefaultRetryHandler(new MyIntegerRetryHandler());
        Assertions.assertThat(defaultRetryHandler.getInputArgsType()).isEqualTo(Integer.class);
    }

    /**
     * 无泛型
     */
    @Test(expected = IllegalRetryException.class)
    public void testGetInputArgsTypeWithNoGenerice() {
        DefaultRetryHandler defaultRetryHandler = new DefaultRetryHandler(new MyRetryHandler());
        defaultRetryHandler.getInputArgsType();
    }

    /**
     * 集合类泛型
     */
    @Test(expected = IllegalRetryException.class)
    public void testGetInputArgsTypeWithListGenerice() {
        DefaultRetryHandler defaultRetryHandler = new DefaultRetryHandler(new ListRetryHandler());
        defaultRetryHandler.getInputArgsType();
    }

    /**
     * 集合类泛型
     */
    @Test(expected = IllegalRetryException.class)
    public void testGetInputArgsTypeWithUserListGenerice() {
        DefaultRetryHandler defaultRetryHandler = new DefaultRetryHandler(new UserListRetryHandler());
        defaultRetryHandler.getInputArgsType();
    }

    /**
     * Object泛型
     */
    @Test(expected = IllegalRetryException.class)
    public void testGetInputArgsTypeWithObject() {
        DefaultRetryHandler defaultRetryHandler = new DefaultRetryHandler(new MyObjectRetryHandler());
        defaultRetryHandler.getInputArgsType();
    }
} 
