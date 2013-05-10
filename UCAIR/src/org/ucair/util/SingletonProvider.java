package org.ucair.util;

import javax.inject.Provider;

public class SingletonProvider<E> implements Provider<E> {

    private final E singleton;

    public SingletonProvider(final E singleton) {
        this.singleton = singleton;
    }

    @Override
    public E get() {
        return singleton;
    }
}
