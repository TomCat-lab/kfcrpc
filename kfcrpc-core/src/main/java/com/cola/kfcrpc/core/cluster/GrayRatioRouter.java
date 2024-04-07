package com.cola.kfcrpc.core.cluster;

import com.cola.kfcrpc.core.api.Router;
import com.cola.kfcrpc.core.meta.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class GrayRatioRouter implements Router<InstanceMeta> {
    private int grayRatio;

    private Random random = new Random();

    public GrayRatioRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }



    @Override
    public List<InstanceMeta> route(List<InstanceMeta> providers) {
        List<InstanceMeta> gray = new ArrayList<>();
        List<InstanceMeta> normal = new ArrayList<>();
        if (providers == null || providers.size() ==1) return providers;
        providers.stream().forEach(p->{
            String s = p.getParameters().get("gray");
            if (StringUtils.equals(s,"true")){
                gray.add(p);
            }else {
                normal.add(p);
            }
        });
        if(normal.isEmpty() || gray.isEmpty()) return providers;
        if (grayRatio<=0){
            return normal;
        }else if (grayRatio>100){
            return gray;
        }

        int res = random.nextInt(100);
        if (res<=grayRatio){
            return gray;
        }else if (res>grayRatio){
            return normal;
        }


        return null;
    }


}
