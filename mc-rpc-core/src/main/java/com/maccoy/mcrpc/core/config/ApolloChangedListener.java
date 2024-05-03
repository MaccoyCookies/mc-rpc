package com.maccoy.mcrpc.core.config;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Maccoy
 * @date 2024/4/14 17:55
 * Description
 */
@Data
@Slf4j
public class ApolloChangedListener implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @ApolloConfigChangeListener({"application"})
    private void changeHandler(ConfigChangeEvent configChangeEvent) {

        for (String changedKey : configChangeEvent.changedKeys()) {
            ConfigChange change = configChangeEvent.getChange(changedKey);
            log.info("Found change - {}", change.toString());
        }
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(configChangeEvent.changedKeys()));
    }

}
