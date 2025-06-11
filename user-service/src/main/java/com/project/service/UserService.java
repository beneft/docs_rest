package com.project.service;

import com.project.config.KeycloakProperties;
import com.project.dto.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final Keycloak keycloak;
    private final KeycloakProperties props;
    private final EmailVerificationService emailVerificationService;
    private final WebClient web = WebClient.builder().build();

    public void register(RegisterRequest request) {
        UserRepresentation u = new UserRepresentation();
        u.setEmail(request.email());
        u.setUsername(request.email());
        u.setFirstName(request.firstName());
        u.setLastName(request.lastName());
        u.setEnabled(true);

        if (request.organization() != null && !request.organization().isBlank())
            u.singleAttribute("organization", request.organization());

        if (request.position() != null && !request.position().isBlank())
            u.singleAttribute("position", request.position());

        if (request.phone() != null && !request.phone().isBlank())
            u.singleAttribute("phone", request.phone());

        if (request.iin() != null && !request.iin().isBlank())
            u.singleAttribute("iin", request.iin());

        Response resp = keycloak.realm(props.getRealm()).users().create(u);
        if (resp.getStatus() != 201) throw new IllegalStateException("KC create: " + resp);

        String id = resp.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        CredentialRepresentation pass = new CredentialRepresentation();
        pass.setType(CredentialRepresentation.PASSWORD);
        pass.setValue(request.password());
        keycloak.realm(props.getRealm()).users().get(id).resetPassword(pass);

        emailVerificationService.createOrUpdateVerification(request.email());
    }

    public AuthResponse login(LoginRequest loginRequest) {
        if (!emailVerificationService.isEmailConfirmed(loginRequest.email())) {
            throw new IllegalStateException("Email не подтвержден.");
        }

        Map tok = web.post()
                .uri(props.tokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id",     props.getClientId())
                        .with("client_secret", props.getClientSecret())
                        .with("username",      loginRequest.email())
                        .with("password",      loginRequest.password()))
                .exchangeToMono(resp -> {
                    if (resp.statusCode().isError())
                        return resp.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new IllegalStateException("KC /token error: " + body)));
                    return resp.bodyToMono(Map.class);
                })
                .block();

        return new AuthResponse(
                (String) tok.get("access_token"),
                (String) tok.get("refresh_token"),
                ((Number) tok.get("expires_in")).longValue(),
                (String) tok.get("token_type"));
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        try {
            login(new LoginRequest(email, oldPassword));
        } catch (Exception e) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        List<UserRepresentation> found = keycloak.realm(props.getRealm()).users().searchByEmail(email, true);
        if (found.isEmpty()) throw new NoSuchElementException("User not found: " + email);
        String id = found.get(0).getId();

        CredentialRepresentation pass = new CredentialRepresentation();
        pass.setTemporary(false);
        pass.setType(CredentialRepresentation.PASSWORD);
        pass.setValue(newPassword);
        keycloak.realm(props.getRealm()).users().get(id).resetPassword(pass);
    }

    public UserDto getById(String id) { return map(kcUser(id)); }
    public UserDto findByEmail(String email, boolean exact) {
        List<UserRepresentation> brief =
                keycloak.realm(props.getRealm())
                        .users()
                        .searchByEmail(email, exact);
        if (brief.isEmpty())
            throw new NoSuchElementException("User not found: " + email);
        String id = brief.get(0).getId();
        UserRepresentation full = keycloak.realm(props.getRealm())
                .users()
                .get(id)
                .toRepresentation();
        UserDto dto = map(full);
        return new UserDto(dto.id(), dto.email(),
                dto.firstName(), dto.lastName(),
                dto.organization(), dto.position(),
                dto.phone(),dto.iin());
    }
    public UserDto findByIIN(String iin) {
        List<UserRepresentation> brief =
                keycloak.realm(props.getRealm())
                        .users()
                        .searchByAttributes("iin:" + iin);
        if (brief.isEmpty())
            throw new NoSuchElementException("User not found: " + iin);
        String id = brief.get(0).getId();
        UserRepresentation full = keycloak.realm(props.getRealm())
                .users()
                .get(id)
                .toRepresentation();
        UserDto dto = map(full);
        return new UserDto(dto.id(), dto.email(),
                dto.firstName(), dto.lastName(),
                dto.organization(), dto.position(),
                dto.phone(),dto.iin());
    }

    public UserDto update(String id, UpdateProfileRequest request) {
        UserRepresentation u = kcUser(id);
        if (request.email() != null) { u.setEmail(request.email()); u.setUsername(request.email()); }
        if (request.firstName() != null) u.setFirstName(request.firstName());
        if (request.lastName()  != null) u.setLastName(request.lastName());

        Map<String, List<String>> at = u.getAttributes() == null ?
                new HashMap<>() : u.getAttributes();
        if (request.organization() != null) at.put("organization", List.of(request.organization()));
        if (request.position()     != null) at.put("position",     List.of(request.position()));
        if (request.phone()        != null) at.put("phone",        List.of(request.phone()));
        if (request.iin()        != null) at.put("iin",        List.of(request.iin()));
        u.setAttributes(at);

        keycloak.realm(props.getRealm()).users().get(id).update(u);
        return map(u);
    }

    public void delete(String id) {
        keycloak.realm(props.getRealm()).users().get(id).remove();
    }

    private UserRepresentation kcUser(String id) {
        return keycloak.realm(props.getRealm()).users().get(id).toRepresentation();
    }
    private UserDto map(UserRepresentation u) {
        Map<String, List<String>> at = Optional.ofNullable(u.getAttributes())
                .orElse(Map.of());
        return new UserDto(
                u.getId(), u.getEmail(), u.getFirstName(), u.getLastName(),
                at.getOrDefault("organization", List.of("")).get(0),
                at.getOrDefault("position",     List.of("")).get(0),
                at.getOrDefault("phone",        List.of("")).get(0),
                at.getOrDefault("iin",        List.of("")).get(0));
    }
}
