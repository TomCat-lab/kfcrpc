package com.cola.kfcrpc.core.api;

import java.util.List;

public interface Router<T> {
    List<T> route(List<T> providers);

    Router Default = p -> p;
}
