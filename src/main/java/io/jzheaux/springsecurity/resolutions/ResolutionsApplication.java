package io.jzheaux.springsecurity.resolutions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import static org.springframework.http.HttpMethod.GET;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication
public class ResolutionsApplication extends WebSecurityConfigurerAdapter {

	@Autowired
	UserRepositoryJwtAuthenticationConverter authenticationConverter;

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.authorizeRequests(authz -> authz
					.anyRequest().authenticated())
				.httpBasic(basic -> {})
				//.oauth2ResourceServer(oauth2 ->
				//		oauth2.jwt().jwtAuthenticationConverter(this.authenticationConverter))
				.oauth2ResourceServer(oauth2 -> oauth2.opaqueToken())
				.cors(cors -> {});
	}

	@Bean
	public UserDetailsService userDetailsService(UserRepository userRepository) {
		return new UserRepositoryUserDetailsService(userRepository);
	}

	@Bean
	WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:4000")
						.allowedMethods("HEAD")
						.allowedHeaders("Authorization");
			}
		};
	}

	@Bean
	public OpaqueTokenIntrospector introspector(UserRepository userRepository,
												OAuth2ResourceServerProperties properties) {
		OpaqueTokenIntrospector introspector = new NimbusOpaqueTokenIntrospector(
				properties.getOpaquetoken().getIntrospectionUri(),
				properties.getOpaquetoken().getClientId(),
				properties.getOpaquetoken().getClientSecret());
		return new UserRepositoryOpaqueTokenIntrospector(userRepository, introspector);
	}

	public static void main(String[] args) {
		SpringApplication.run(ResolutionsApplication.class, args);
	}
}
