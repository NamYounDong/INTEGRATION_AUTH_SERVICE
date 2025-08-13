package shop.dm_nyd.cloudAuthService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import shop.dm_nyd.cloudAuthService.service.SrvcService;
import shop.dm_nyd.cloudAuthService.service.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(UserService userService, SrvcService srvcService,  AuthorizationRequestRepository<OAuth2AuthorizationRequest> authReqRepo) {
        return new OAuth2SuccessHandler(userService, srvcService, authReqRepo);
    }
	
	
	@Autowired
	private OAuth2FailHandler oAuth2FailHandler;
	
	// 요청 무시 설정 (정적 리소스, JSP 직접 접근 등)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .requestMatchers("/css/**", "/js/**", "/images/**", "/WEB-INF/**"); // 정적 자원 + JSP 직접 접근 무시
    }
    
    @Bean
    AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }
    
    // Spring Session Redis 직렬화기 (default typing 끄고, 시큐리티 모듈만 등록)
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
    
    
    /**
     * OAuth2AuthorizationRequestResolver
     * 추가 파라미터를 SuccessHandler로 전달하기 위해 활용
     **/
    @Bean
    OAuth2AuthorizationRequestResolver authorizationRequestResolver(ClientRegistrationRepository repo) {
        var delegate = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest req = delegate.resolve(request);
                if (req == null) return null;

                String srvcSeq = request.getParameter("srvcSeq");
                
                if (srvcSeq != null) {
                    String state = java.util.Base64.getUrlEncoder()
                        .encodeToString(("{\"srvcSeq\":" + srvcSeq + "}")
                        .getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    return OAuth2AuthorizationRequest.from(req)
                            .state(state)
                            .build();
                }
                return req;
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                return delegate.resolve(request, clientRegistrationId);
            }
        };
    }
    
	
    @Bean
    public SecurityFilterChain filterChain(
    			HttpSecurity http, 
    			AuthorizationRequestRepository<OAuth2AuthorizationRequest> authReqRepo, 
//    			OAuth2Resolver oAuth2Resolver,
    			OAuth2AuthorizationRequestResolver oAuth2Resolver,
    			OAuth2SuccessHandler oAuth2SuccessHandler) throws Exception {
        http
        	.csrf(csrf -> csrf.disable())
        	.cors(cors -> cors.disable())
	        .authorizeHttpRequests(auth -> 
	            auth
	            	.requestMatchers("/css/**", "/js/**", "/images/**", "/pg/login", "/WEB-INF/views/**", "/error", "/auth", "/").permitAll()
	            	.anyRequest().authenticated()
	        )
	        .oauth2Login(oauth -> 
	        	oauth
	        		.loginPage("/pg/login")
	        		.redirectionEndpoint(redir -> redir.baseUri("/auth"))
	        		
	        		.authorizationEndpoint(a -> a
                        .authorizationRequestResolver(oAuth2Resolver)
                        .authorizationRequestRepository(authReqRepo)
                    )
	        		
	            	.successHandler(oAuth2SuccessHandler)
	            	.failureHandler(oAuth2FailHandler)
	        )
	        .logout(logout -> logout.logoutSuccessUrl("/pg/login"))
	        .sessionManagement(session -> session
	            .maximumSessions(1)
	            .maxSessionsPreventsLogin(true)
	        );
        return http.build();
    }
}
