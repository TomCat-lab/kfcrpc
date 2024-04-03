package com.cola.kfcrpc.core.registry;

import lombok.Data;

import java.util.List;

@Data
public class Event {

    public Event(List<String> data) {
        this.data = data;
    }

    private List<String> data;
}
