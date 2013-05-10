package org.ucair.util;

import java.io.File;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringUtil implements ApplicationContextAware {

    private SpringUtil() {
    }

    private static class SingletonHolder {
        public static SpringUtil instance = new SpringUtil();
    }

    public static SpringUtil getInstance() {
        return SingletonHolder.instance;
    }

    private ApplicationContext context;

    public static ApplicationContext initContext(final String springConfigPath) {
        final File file = new File(springConfigPath);
        return new FileSystemXmlApplicationContext(file.getAbsoluteFile()
                .toURI().toString());
    }

    @Override
    public void setApplicationContext(final ApplicationContext context)
            throws BeansException {
        this.context = context;
    }

    public static ApplicationContext getContext() {
        return getInstance().context;
    }
}
