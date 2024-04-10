package com.maccoy.mcrpc.core.annotation;

import com.maccoy.mcrpc.core.config.ConsumerConfig;
import com.maccoy.mcrpc.core.config.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConfig.class, ConsumerConfig.class})
public @interface EnableMcRpc {


}
