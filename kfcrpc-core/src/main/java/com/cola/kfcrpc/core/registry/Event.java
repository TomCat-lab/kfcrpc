package com.cola.kfcrpc.core.registry;

import com.cola.kfcrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

@Data
public class Event {

    public Event(List<InstanceMeta> data) {
        this.data = data;
    }

    private List<InstanceMeta> data;
}
