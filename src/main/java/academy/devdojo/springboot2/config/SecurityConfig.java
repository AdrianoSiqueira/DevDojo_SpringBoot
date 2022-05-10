package academy.devdojo.springboot2.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Esse método irá criar um usuário em memória com a senha criptografada.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        auth.inMemoryAuthentication()
            .withUser("Adriano")
            .password(encoder.encode("123"))
            .roles("USER")
            .and()
            .withUser("Devdojo")
            .password(encoder.encode("123"))
            .roles("USER", "ADMIN");
    }

    /**
     * Sobrescrevemos esse método para informar que todas as requisições ao
     * servidor deverão passar por uma autenticação básica via http.
     *
     * CSRF protege contra ações usando credenciais de outro usuário. Setando o
     * token para HttpOnlyFalse impedimos que alguém use cookies de forma
     * indevida.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
//            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//            .and()
            .disable()
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
            .and()
            .httpBasic();
    }
}
