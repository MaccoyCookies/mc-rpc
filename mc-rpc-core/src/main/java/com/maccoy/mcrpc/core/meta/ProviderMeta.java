package com.maccoy.mcrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * 描述provider映射关系
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderMeta {

    private Method method;

    private String methodSign;

    private Object serviceImpl;

}
