package com.example.retrospect.web;

import com.example.retrospect.core.models.*;
import com.example.retrospect.core.repositories.RetrospectiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.Collections;

@SpringBootApplication(scanBasePackages = "com.example.retrospect")
@EnableOAuth2Sso
public class Application extends WebSecurityConfigurerAdapter {

    private final RetrospectiveRepository repository;

    @Autowired
    public Application(RetrospectiveRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void postConstruct(){
        var slaing = new LoggedInUser(new User("sl-slaing", "", "", null));
        var createdBySlaing = new Audit(OffsetDateTime.now(), slaing);

        repository.addOrReplace(
                new Retrospective(
                        "123",
                        createdBySlaing,
                        new ImmutableList<>(Collections.singletonList(new Action("1231", "action-1", createdBySlaing, false, "jira/456", slaing))),
                        new ImmutableList<>(Collections.emptyList()),
                        new ImmutableList<>(Collections.emptyList()),
                        new ImmutableList<>(Collections.singletonList(slaing)),
                        new ImmutableList<>(Collections.singletonList(new User("stennant", "", "", null)))));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .antMatcher("/**")
            .authorizeRequests()
                .antMatchers("/", "/login**", "/styles/**", "/webjars/**", "/error**")
                .permitAll()
            .anyRequest()
                .authenticated()
            .and().logout().logoutSuccessUrl("/").permitAll()
            .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }
}
