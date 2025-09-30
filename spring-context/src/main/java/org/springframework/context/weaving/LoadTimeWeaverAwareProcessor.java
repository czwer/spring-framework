/*
 * Copyright 2002-2023 the original author or authors.
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

package org.springframework.context.weaving;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor}
 * implementation that passes the context's default {@link LoadTimeWeaver}
 * to beans that implement the {@link LoadTimeWeaverAware} interface.
 *
 * <p>{@link org.springframework.context.ApplicationContext Application contexts}
 * will automatically register this with their underlying {@link BeanFactory bean factory},
 * provided that a default {@code LoadTimeWeaver} is actually available.
 *
 * <p>Applications should not use this class directly.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see LoadTimeWeaverAware
 * @see org.springframework.context.ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME
 */
public class LoadTimeWeaverAwareProcessor implements BeanPostProcessor, BeanFactoryAware {
	protected final Log logger = LogFactory.getLog(LoadTimeWeaverAwareProcessor.class);

	@Nullable
	private LoadTimeWeaver loadTimeWeaver;

	@Nullable
	private BeanFactory beanFactory;


	/**
	 * Create a new {@code LoadTimeWeaverAwareProcessor} that will
	 * auto-retrieve the {@link LoadTimeWeaver} from the containing
	 * {@link BeanFactory}, expecting a bean named
	 * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
	 */
	public LoadTimeWeaverAwareProcessor() {
	}

	/**
	 * Create a new {@code LoadTimeWeaverAwareProcessor} for the given
	 * {@link LoadTimeWeaver}.
	 * <p>If the given {@code loadTimeWeaver} is {@code null}, then a
	 * {@code LoadTimeWeaver} will be auto-retrieved from the containing
	 * {@link BeanFactory}, expecting a bean named
	 * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
	 * @param loadTimeWeaver the specific {@code LoadTimeWeaver} that is to be used
	 */
	public LoadTimeWeaverAwareProcessor(@Nullable LoadTimeWeaver loadTimeWeaver) {
		this.loadTimeWeaver = loadTimeWeaver;
	}

	/**
	 * Create a new {@code LoadTimeWeaverAwareProcessor}.
	 * <p>The {@code LoadTimeWeaver} will be auto-retrieved from
	 * the given {@link BeanFactory}, expecting a bean named
	 * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
	 * @param beanFactory the BeanFactory to retrieve the LoadTimeWeaver from
	 */
	public LoadTimeWeaverAwareProcessor(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof LoadTimeWeaverAware loadTimeWeaverAware) {
			logger.info("[SPRING] 自定义日志---实现BeanPostProcessor：在Bean初始化之前，检查并回调实现了LoadTimeWeaverAware接口的Bean，将Spring容器中的LoadTimeWeaver实例注入给它们："+beanName);
			LoadTimeWeaver ltw = this.loadTimeWeaver;
			if (ltw == null) {
				Assert.state(this.beanFactory != null,
						"BeanFactory required if no LoadTimeWeaver explicitly specified");
				logger.info("[SPRING] 自定义日志---调用getBean："+ConfigurableApplicationContext.LOAD_TIME_WEAVER_BEAN_NAME);
				ltw = this.beanFactory.getBean(
						ConfigurableApplicationContext.LOAD_TIME_WEAVER_BEAN_NAME, LoadTimeWeaver.class);
			}
			loadTimeWeaverAware.setLoadTimeWeaver(ltw);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String name) {
		logger.info("[SPRING] 自定义日志---实现BeanPostProcessor：目前是空方法："+name);
		return bean;
	}

}
