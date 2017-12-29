package com.get.edgepay.fms.config;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.get.edgepay.fms.common.Application;
import com.get.edgepay.fms.filter.AuthenticationFilter;

@EntityScan("com.get.edgepay.fms.domain.model")
@EnableJpaRepositories("com.get.edgepay.fms.repository")
@SpringBootApplication(scanBasePackages = "com.get.edgepay.fms.*")
public class FmsApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(FmsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FmsApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean authenticationFilterRegistration() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new AuthenticationFilter());

		return registrationBean;
	}

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
				tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer() {
					@Override
					public void customize(Connector connector) {
						log.info("************ Tomcat Thread Count= " + Application.TOMCAT_THREAD_COUNT);
						connector.setAttribute("keepAliveTimeout", 60000);
						connector.setAttribute("maxConnections", 10000);
						connector.setAttribute("maxKeepAliveRequests", 200);
						connector.setAttribute("enableLookups", "false");
						connector.setAttribute("compression", "off");
						connector.setAttribute("server", "Latest Version");
						connector.setAttribute("maxThreads", Application.TOMCAT_THREAD_COUNT);
					}
				});
			}
		};
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("In run()...");
	}

}
