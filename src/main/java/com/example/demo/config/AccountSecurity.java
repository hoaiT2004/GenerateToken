package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.example.demo.filter.JwtTokenFilter;
import com.example.demo.service.AccountService;
import lombok.RequiredArgsConstructor;
@Configuration
@EnableWebSecurity
public class AccountSecurity {
	
	@Autowired 
	@Lazy
	/*
	 * Nếu kh có anotation Lazy sẽ xảy ra lỗi:
	 * The dependencies of some of the beans in the application context form a cycle:
	 *  ┌─────┐ 	| jwtTokenFilter (field private com.example.demo.service.AccountService com.example.demo.filter.JwtTokenFilter.accountService)
	 *   		↑ ↓ | accountService (field private org.springframework.security.crypto.password.PasswordEncoder com.example.demo.service.AccountService.passwordEncoder)
	 *    		↑ ↓ | accountSecurity (field private com.example.demo.filter.JwtTokenFilter com.example.demo.config.AccountSecurity.jwtTokenFilter)
	 */
	private JwtTokenFilter jwtTokenFilter;
	
	@Bean // Lấy dữ liệu từ view sau đó mã hóa mật khẩu để kiểm tra với mật khẩu ở db(do ở db mật khẩu cũng mã hóa)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();    
    }
	
    @Bean
    public UserDetailsService detailsService() {
        return new AccountService();
    }

    // Cung cấp thông tin người dùng và password đã mã hóa truyền cho
    // AuthenticationManager để xác thực
    @Bean // dịch vụ cung cấp passwordEncoder để đọc BCrypt
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(detailsService());
        return authenticationProvider;
    }

    // xác thực người dùng
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
        return authenticationManagerBuilder.build();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http	
        		.cors().disable()
        		.csrf().disable()
        		.authorizeRequests()
				.requestMatchers(HttpMethod.POST,"/api/test/**")
				.permitAll()
				.requestMatchers(HttpMethod.GET,"/api/test/user").hasAuthority("USER")
				.requestMatchers(HttpMethod.GET,"/api/test/admin").hasAuthority("ADMIN")
				.requestMatchers(HttpMethod.GET,"/api/test/home").hasAnyAuthority("ADMIN","USER")
                .anyRequest().authenticated()
                .and()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        		.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
