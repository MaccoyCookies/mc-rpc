package com.maccoy.mcrpc.core.cluster;

import com.maccoy.mcrpc.core.api.Router;
import com.maccoy.mcrpc.core.meta.InstanceMeta;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Maccoy
 * @date 2024/04/02 08:11
 * Description 灰度路由
 */
@Slf4j
public class GrayRouter implements Router<InstanceMeta> {

    private int grayRatio;

    private Random random = new Random();

    public GrayRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    @Override
    public List<InstanceMeta> router(List<InstanceMeta> providers) {
        if (providers == null || providers.size() < 2) return providers;
        List<InstanceMeta> normals = new ArrayList<>();
        List<InstanceMeta> grays = new ArrayList<>();
        providers.forEach(meta -> {
            boolean flag = Boolean.parseBoolean(meta.getParameters().get("gray"));
            if (flag) {
                grays.add(meta);
            } else {
                normals.add(meta);
            }
        });
        if (normals.isEmpty() || grays.isEmpty()) return providers;

        if (grayRatio <= 0) {
            return normals;
        } else if (grayRatio >= 100) {
            return grays;
        }

        // grayRatio -> 10
        if (random.nextInt(100) <= grayRatio) {
            return grays;
        } else {
            return normals;
        }
//        return providers;
    }
}
