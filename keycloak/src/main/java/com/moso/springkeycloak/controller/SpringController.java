package com.moso.springkeycloak.controller;

import com.moso.springkeycloak.model.Users;
import com.moso.springkeycloak.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class SpringController {

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private KeyCloakService keyCloakService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    private String responseToken;

//    @RequestMapping(value = "/token", method = RequestMethod.POST)
//    public ResponseEntity<?> getTokenUsingCredentials(@RequestBody Users userCredentials) {
//        try {
//            responseToken = keyCloakService.getToken(userCredentials);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(responseToken, HttpStatus.OK);
//
//    }

    /*
     * When access token get expired than send refresh token to get new access
     * token. We will receive new refresh token also in this response.Update
     * client cookie with updated refresh and access token
     */
//
    @CrossOrigin
    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    public String login(Model model,Principal principal){
        return "index";
    }

//    @RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
//    public ResponseEntity<?> getTokenUsingRefreshToken(@RequestHeader(value = "Authorization") String refreshToken) {
//        try {
//            responseToken = keyCloakService.getByRefreshToken(refreshToken);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(responseToken, HttpStatus.OK);
//    }
//
//    @ModelAttribute("users")
//    public Users users() {
//        return new Users();
//    }

    /***
     * Get list details user before login success.
     */
    @RequestMapping("/users")
    public String user(Principal principal, Model model) {
        Iterable<Users> users = userRepository.findUserByUsername(principal.getName());
        Iterable<Users> listUsers = userRepository.findAll();
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("users", users);
        model.addAttribute("userLogin", principal.getName());
        return "users";
    }

    /***
     * Register new user
     */
    @RequestMapping("/registration")
    public String register() {
        return "register";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registerAccount(@ModelAttribute("users") @Valid Users users, BindingResult result) {

        try {
            Users checkExists = userRepository.findByUsername((users.getUsername()));
            if (checkExists != null) {
                result.rejectValue("username", "This is already an account registered with that username ");
            }
            if (result.hasErrors()) {
                return "register";
            }
            userRepository.save(users);
//            createUser(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:users";
    }


    /***
     * Create User keycloak
     */
//    public ResponseEntity<?> createUser(@RequestBody Users users) {
//        try {
//
//            keyCloakService.createUserInKeyCloak(users);
//            return new ResponseEntity<>(HttpStatus.OK);
//        }
//
//        catch (Exception ex) {
//
//            ex.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//
//        }
//
//    }
}
