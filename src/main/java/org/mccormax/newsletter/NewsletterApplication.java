package org.mccormax.newsletter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories
public class NewsletterApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsletterApplication.class, args);
	}
/*
	@Bean
	public org.neo4j.ogm.config.Configuration getConfiguration() {
		org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
		config.driverConfiguration()
				.setDriverClassName
		("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")
				.setURI("file:/Users/mccormax/Develop/newsletter/target/neo4j_graph.db");

		return config;
	} */

}
