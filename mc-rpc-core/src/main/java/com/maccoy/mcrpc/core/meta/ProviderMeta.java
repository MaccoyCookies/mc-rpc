package com.maccoy.mcrpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * 描述provider映射关系
 */
@Data
public class ProviderMeta {

    private Method method;

    private String methodSign;

    private Object serviceImpl;

}
