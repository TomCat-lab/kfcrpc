package com.cola.kfcrpc.core.meta;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Class: ServiceMeta
 * Author: cola
 * Date: 2024/4/4
 * Description: 描述服务元数据
 */

@Data
@Builder
public class ServiceMeta {
    private String app; // 服务的应用
    private String namespace; // 命名空间

    private String env; // 服务所处的环境

    private String name ; // service name

    private Map<String,String> parameters;
    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }

    public String toMetas(){
        return JSON.toJSONString(parameters);
    }
}
