package com.zy.rpcclient.init;

import com.zy.rpccommon.annotation.RpcClient;
import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * Created by Horizon Time: 11:03 2019-02-10 Description:
 */
public class RpcInitConfig implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata,
            BeanDefinitionRegistry beanDefinitionRegistry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.addIncludeFilter(new AnnotationTypeFilter(RpcClient.class));
        Set<BeanDefinition> beanDefinitions = scanner
                .findCandidateComponents("com.zy.rpcclient.service");
        for (BeanDefinition beanDefinition : beanDefinitions) {
            String className = beanDefinition.getBeanClassName();
            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                    .genericBeanDefinition(RpcClientFactoryBean.class);
            builder.addConstructorArgValue(className);
            builder.getBeanDefinition().setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            beanDefinitionRegistry.registerBeanDefinition(className, builder.getBeanDefinition());
        }

    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata()
                        .isIndependent();
            }
        };
    }


}
