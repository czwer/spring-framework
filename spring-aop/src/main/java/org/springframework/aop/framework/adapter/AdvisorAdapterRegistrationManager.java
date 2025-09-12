/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.framework.adapter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * BeanPostProcessor that registers {@link AdvisorAdapter} beans in the BeanFactory with
 * an {@link AdvisorAdapterRegistry} (by default the {@link GlobalAdvisorAdapterRegistry}).
 *
 * <p>The only requirement for it to work is that it needs to be defined
 * in application context along with "non-native" Spring AdvisorAdapters
 * that need to be "recognized" by Spring's AOP framework.
 *
 * @author Dmitriy Kopylenko
 * @author Juergen Hoeller
 * @since 27.02.2004
 * @see #setAdvisorAdapterRegistry
 * @see AdvisorAdapter
 */
public class AdvisorAdapterRegistrationManager implements BeanPostProcessor {
	protected final Log logger = LogFactory.getLog(AdvisorAdapterRegistrationManager.class);

	private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();


	/**
	 * Specify the AdvisorAdapterRegistry to register AdvisorAdapter beans with.
	 * Default is the global AdvisorAdapterRegistry.
	 * @see GlobalAdvisorAdapterRegistry
	 */
	public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
		this.advisorAdapterRegistry = advisorAdapterRegistry;
	}


	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		logger.info("[SPRING] 自定义日志---实现BeanPostProcessor：目前是空方法："+beanName);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof AdvisorAdapter advisorAdapter) {
			logger.info("[SPRING] 自定义日志---实现BeanPostProcessor：确保Spring AOP框架中的Advisor适配器（AdvisorAdapter）被正确注册到全局注册表中，从而支持不同类型的Advice（如BeforeAdvice、AfterReturningAdvice、ThrowsAdvice等）能够被正确地识别和执行："+beanName);
			this.advisorAdapterRegistry.registerAdvisorAdapter(advisorAdapter);
		}
		return bean;
	}

}
