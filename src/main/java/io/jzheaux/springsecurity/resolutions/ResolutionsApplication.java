package io.jzheaux.springsecurity.resolutions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import static org.springframework.http.HttpMethod.GET;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@SpringBootApplication
public class ResolutionsApplication extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(ResolutionsApplication.class, args);
	}

	@Bean
	public UserDetailsService userDetailsService(UserRepository userRepository) {
		return new UserRepositoryUserDetailsService(userRepository);
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.authorizeRequests(authz -> authz
					.mvcMatchers(GET, "/resolutions", "/resolution/**").hasAuthority("resolution:read")
					.anyRequest().hasAuthority("resolution:write"))
				.httpBasic(basic -> {});
	}
}
