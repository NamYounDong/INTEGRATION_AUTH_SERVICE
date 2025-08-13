package shop.dm_nyd.cloudAuthService.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


/**
 * OAuth2SuccessHandler
 * Google OAuth2 인증 실패 클래스
 * - 차후 실패 시나리오 확인 필요
 */
@RequiredArgsConstructor
@Component
public class OAuth2FailHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		System.out.println("OAuth2FailHandler =================================================== ");
	}

}
