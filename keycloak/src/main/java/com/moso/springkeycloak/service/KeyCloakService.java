//package com.moso.springkeycloak.service;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import javax.ws.rs.core.Response;
//
//import com.moso.springkeycloak.model.Users;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.message.BasicNameValuePair;
//import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.KeycloakBuilder;
//import org.keycloak.admin.client.resource.RealmResource;
//import org.keycloak.admin.client.resource.UserResource;
//import org.keycloak.admin.client.resource.UsersResource;
//import org.keycloak.representations.idm.CredentialRepresentation;
//import org.keycloak.representations.idm.RoleRepresentation;
//import org.keycloak.representations.idm.UserRepresentation;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//
//@Component
//public class KeyCloakService {
//
//    @Value("${keycloak.credentials.secret}")
//    private String SECRETKEY;
//
//    @Value("${keycloak.resource}")
//    private String CLIENTID;
//
//    @Value("${keycloak.auth-server-url}")
//    private String AUTHURL;
//
//    @Value("${keycloak.realm}")
//    private String REALM;
//    public String getToken(Users userCredentials) {
//
//        String responseToken = null;
//        try {
//
//            String username = userCredentials.getUsername();
//
//            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//            urlParameters.add(new BasicNameValuePair("grant_type", "password"));
//            urlParameters.add(new BasicNameValuePair("client_id", CLIENTID));
//            urlParameters.add(new BasicNameValuePair("username", username));
//            urlParameters.add(new BasicNameValuePair("password", userCredentials.getPassword()));
//            urlParameters.add(new BasicNameValuePair("client_secret", SECRETKEY));
//
//            responseToken = sendPost(urlParameters);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return responseToken;
//
//    }
//
//    public String getByRefreshToken(String refreshToken) {
//
//        String responseToken = null;
//        try {
//
//            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//            urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
//            urlParameters.add(new BasicNameValuePair("client_id", CLIENTID));
//            urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
//            urlParameters.add(new BasicNameValuePair("client_secret", SECRETKEY));
//
//            responseToken = sendPost(urlParameters);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//
//        return responseToken;
//    }
//
//    public int createUserInKeyCloak(Users users) {
//
//        int statusId = 0;
//        try {
//
//            UsersResource userResource = getKeycloakUserResource();
//
//            UserRepresentation user = new UserRepresentation();
//            user.setUsername(users.getUsername());
//            user.setEnabled(true);
//
//            // Create user
//            Response result = userResource.create(user);
//            System.out.println("Keycloak create user response code>>>>" + result.getStatus());
//
//            statusId = result.getStatus();
//
//            if (statusId == 201) {
//
//                String userId = result.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
//
//                System.out.println("User created with userId:" + userId);
//
//                // Define password credential
//                CredentialRepresentation passwordCred = new CredentialRepresentation();
//                passwordCred.setTemporary(false);
//                passwordCred.setType(CredentialRepresentation.PASSWORD);
//                passwordCred.setValue(users.getPassword());
//
//                // Set password credential
//                userResource.get(userId).resetPassword(passwordCred);
//
//                // set role
//                RealmResource realmResource = getRealmResource();
//                RoleRepresentation savedRoleRepresentation = realmResource.roles().get("user").toRepresentation();
//                realmResource.users().get(userId).roles().realmLevel().add(Arrays.asList(savedRoleRepresentation));
//
//                System.out.println("Username==" + users.getUsername() + " created in keycloak successfully");
//
//            }
//
//            else if (statusId == 409) {
//                System.out.println("Username==" + users.getUsername() + " already present in keycloak");
//
//            } else {
//                System.out.println("Username==" + users.getUsername() + " could not be created in keycloak");
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//
//        return statusId;
//
//    }
//
//
//    private UsersResource getKeycloakUserResource() {
//
//        Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTHURL).realm("master").username("admin").password("admin")
//                .clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
//                .build();
//
//        RealmResource realmResource = kc.realm(REALM);
//        UsersResource userRessource = realmResource.users();
//
//        return userRessource;
//    }
//
//
//    private RealmResource getRealmResource() {
//
//        Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTHURL).realm("master").username("admin").password("admin")
//                .clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
//                .build();
//
//        RealmResource realmResource = kc.realm(REALM);
//
//        return realmResource;
//
//    }
//
//    private String sendPost(List<NameValuePair> urlParameters) throws Exception {
//
//        HttpClient client = HttpClientBuilder.create().build();
//        HttpPost post = new HttpPost(AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/token");
//
//        post.setEntity(new UrlEncodedFormEntity(urlParameters));
//
//        HttpResponse response = client.execute(post);
//
//        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//
//        StringBuffer result = new StringBuffer();
//        String line = "";
//        while ((line = rd.readLine()) != null) {
//            result.append(line);
//        }
//
//        return result.toString();
//
//    }
//
//}