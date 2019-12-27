package com.example.retrospect.web;

import com.example.retrospect.core.models.User;
import com.example.retrospect.web.managers.AccessDeniedAuthenticationEntryPoint;
import com.example.retrospect.web.managers.ClientResources;
import com.example.retrospect.web.managers.ClientResourcesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication(scanBasePackages = "com.example.retrospect")
@EnableOAuth2Client
@EnableRedisHttpSession
public class Application extends WebSecurityConfigurerAdapter {

    private final ClientResourcesManager clientResourcesManager;
    private final OAuth2ClientContext clientContext;
    private final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    public Application(ClientResourcesManager clientResourcesManager, OAuth2ClientContext clientContext) {
        this.clientResourcesManager = clientResourcesManager;
        this.clientContext = clientContext;
    }

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .antMatcher("/**")
            .authorizeRequests()
                .antMatchers("/", "/login**", "/loginProviders", "/styles/**", "/webjars/**", "/error**", "/built/**")
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
        var filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        var template = new OAuth2RestTemplate(client.getClient(), clientContext);
        filter.setRestTemplate(template);

        var tokenServices = new UserInfoTokenServices(
                client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);

        filter.setTokenServices(tokenServices);
        filter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/"));
        return filter;
    }

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        var registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    @ConfigurationProperties("github")
    public ClientResources github() {
        var github = new ClientResources(Application::createGithub);
        return clientResourcesManager.add(github);
    }

    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
        var facebook = new ClientResources(Application::createUserFromFacebook);
        return clientResourcesManager.add(facebook);
    }

    @SuppressWarnings("unused")
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        var redisServerHost = System.getProperty("RedisServerAddress");
        var redisServerPort = System.getProperty("RedisServerPort");

        var config = new RedisStandaloneConfiguration(
                redisServerHost != null ? redisServerHost : "localhost",
                redisServerPort != null ? Integer.parseInt(redisServerPort) : 6379
        );

        logger.info("Using redis-server - " + config.getHostName() + ":" + config.getPort());

        return new LettuceConnectionFactory(config);
    }

    private static User createUserFromFacebook(Map<String, String> userDetails, String providerDisplayName){
        var username = userDetails.get("username");
        var displayName = userDetails.get("name");
        var avatar = userDetails.get("picture");

        return new User(username, displayName, avatar, providerDisplayName);
    }

    private static User createGithub(Map<String, String> userDetails, String providerDisplayName){
        var username = userDetails.get("login");
        var displayName = userDetails.get("name");
        var avatar = userDetails.get("avatar_url");

        return new User(username, displayName, avatar, providerDisplayName);
    }
}
