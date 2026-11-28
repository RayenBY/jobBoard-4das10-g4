package com.esprit.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@LoadBalancerClient(name = "gamification-service", configuration = RandomLoadBalancerConfig.class)
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	// PARTIE 2 - Méthode 1 : routes statiques (sans Eureka)
	/*@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("Candidat", r -> r.path("/api/users/**")
						.uri("http://localhost:8082"))     // user-service
				.route("Job", r -> r.path("/api/courses/**")
						.uri("http://localhost:8083"))     // course-group
				.route("gamification", r -> r.path("/api/gamification/**")
						.uri("http://localhost:8085"))     // gamification-service
				.build();
	}*/
}