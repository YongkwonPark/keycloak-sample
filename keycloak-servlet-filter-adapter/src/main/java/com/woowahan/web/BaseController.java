package com.woowahan.web;

import org.keycloak.representations.IDToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class BaseController {

    @RequestMapping("/")
    @ResponseBody
    public IDToken root(IDToken token) {
        return token;
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();

        return "redirect:/";
    }

}
