package com.example.retrospect.web;

import com.example.retrospect.core.models.*;
import com.example.retrospect.core.repositories.RetrospectiveRepository;
import com.example.retrospect.web.managers.AccessDeniedAuthenticationEntryPoint;
import com.example.retrospect.web.managers.ClientResources;
import com.example.retrospect.web.managers.ClientResourcesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.CompositeFilter;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootApplication(scanBasePackages = "com.example.retrospect")
@EnableOAuth2Client
public class Application extends WebSecurityConfigurerAdapter {

    private final RetrospectiveRepository repository;
    private final ClientResourcesManager clientResourcesManager;

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Autowired
    public Application(RetrospectiveRepository repository, ClientResourcesManager clientResourcesManager) {
        this.repository = repository;
        this.clientResourcesManager = clientResourcesManager;
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
                .antMatchers("/", "/login**", "/styles/**", "/webjars/**", "/error**", "/access-denied")
                .permitAll()
            .anyRequest()
                .authenticated()
            .and().exceptionHandling().accessDeniedPage("/login")
            .and().logout().logoutSuccessUrl("/").permitAll()
            .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and().exceptionHandling().authenticationEntryPoint(new AccessDeniedAuthenticationEntryPoint())
            .and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        clientResourcesManager.getAllClientResources()
                .forEach(clientResources -> filters.add(ssoFilter(clientResources, clientResources.getLoginPath())));

        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(
                client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        filter.setTokenServices(tokenServices);
        return filter;
    }

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    @ConfigurationProperties("github")
    public ClientResources github() {
        return clientResourcesManager.add(new ClientResources("github", Application::createGithub));
    }

    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
        return clientResourcesManager.add(new ClientResources("facebook", Application::createUserFromFacebook));
    }

    private static User createUserFromFacebook(Map<String, String> userDetails){
        var username = userDetails.get("username");
        var displayName = userDetails.get("name");
        var avatar = userDetails.get("picture");

        return new User(username, displayName, avatar, "Facebook");
    }

    private static User createGithub(Map<String, String> userDetails){
        var username = userDetails.get("login");
        var displayName = userDetails.get("name");
        var avatar = userDetails.get("avatar_url");

        return new User(username, displayName, avatar, "GitHub");
    }
}
