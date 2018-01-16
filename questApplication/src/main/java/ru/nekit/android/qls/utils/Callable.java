package ru.nekit.android.qls.utils;

public interface Callable<V, T> {

    T call(V value);
}