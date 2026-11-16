package com.universidad.authserver.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sirve la página de login de Spring Security (form login).
 * El endpoint de token (grant_type=password) no usa esta página:
 * se llama directamente desde Postman. Esta página permite
 * verificar visualmente la configuración de seguridad.
 */
@RestController
public class LoginController {

    @GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    public String loginPage() {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Auth Server - Login</title>
                <style>
                    body { font-family: Arial, sans-serif; background: #f4f6f8; display: flex; justify-content: center; margin-top: 80px; }
                    .card { background: #fff; padding: 32px 40px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,.15); width: 340px; }
                    h2 { margin-top: 0; color: #2c3e50; }
                    label { display: block; margin: 12px 0 4px; color: #34495e; }
                    input { width: 100%; padding: 8px; border: 1px solid #bdc3c7; border-radius: 4px; box-sizing: border-box; }
                    button { margin-top: 18px; width: 100%; padding: 10px; background: #2980b9; color: #fff; border: none; border-radius: 4px; cursor: pointer; }
                </style>
            </head>
            <body>
                <div class="card">
                    <h2>Spring Security Login</h2>
                    <form action="/login" method="post">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" autofocus>
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password">
                        <button type="submit">Sign In</button>
                    </form>
                </div>
            </body>
            </html>
            """;
    }
}
