package org.example.orderservice.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Xóa cookie JWT
        Cookie cookie = new Cookie("jwt_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // Redirect về login frontend (cổng 8080)
        return "redirect:http://localhost:8080/login";
    }
}
