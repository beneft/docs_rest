package com.project.config;


import jakarta.ws.rs.client.ClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak(KeycloakProperties props) {
        return KeycloakBuilder.builder()
                .serverUrl(props.getServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .username(props.getAdminUser())
                .password(props.getAdminPassword())
                .grantType(OAuth2Constants.PASSWORD)
                .resteasyClient(ClientBuilder.newClient())
                .build();
    }
}
