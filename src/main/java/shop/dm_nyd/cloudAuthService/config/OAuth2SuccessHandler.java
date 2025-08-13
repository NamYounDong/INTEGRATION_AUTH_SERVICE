package shop.dm_nyd.cloudAuthService.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import shop.dm_nyd.cloudAuthService.service.SrvcService;
import shop.dm_nyd.cloudAuthService.service.UserService;
import shop.dm_nyd.cloudAuthService.vo.Role;
import shop.dm_nyd.cloudAuthService.vo.UserVo;

/**
 * OAuth2SuccessHandler
 * Google OAuth2 인증 성공 클래스
 */
@RequiredArgsConstructor // @RequiredArgsConstructor 선언해두면 @Autowired 안쓰고 그냥 final 때려서 가져오는 듯?
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	
	private final UserService userService;
	private final SrvcService srvcService;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authReqRepo;
	
    private final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		String state = request.getParameter("state"); // 콜백 URL에 있음
		Integer srvcSeq = null;
		if (state != null) {
			String json = new String(java.util.Base64.getUrlDecoder().decode(state), java.nio.charset.StandardCharsets.UTF_8);
			var m = java.util.regex.Pattern.compile("\"srvcSeq\"\\s*:\\s*(\\d+)").matcher(json); // 차후 추가 값 조회가 필요한 경우 방법 변경 필요
			if (m.find()) srvcSeq = Integer.valueOf(m.group(1));
		}
		
		try {
			// Google OAuth2 인증 정보 객체 
			OidcUser oidc = (OidcUser) authentication.getPrincipal();

			// User 정보 객체
			UserVo user = null;
			
			user = userService.findUser(oidc.getEmail());
			if(user == null) {
				
				
				UserVo userVo = new UserVo();
				userVo.setUserId(oidc.getEmail()); // 예: 이메일을 ID로 사용
				userVo.setUserNm((String) oidc.getAttributes().getOrDefault("name", ""));
				userVo.setPicture((String) oidc.getAttributes().getOrDefault("picture", ""));
				userVo.setEmail(oidc.getEmail());
				userVo.setRole(Role.USER.toString());
				
				userService.saveUser(userVo);
				
				user = userService.findUser(oidc.getEmail());
			}
			
			// 직렬화 저장 방법 - 타 서비스에서 활용할 수 있도록 다음과 같은 방법으로 사용자 정보 저장
			// ex) FASTAPI, nodejs(확인 필요)
			// 가변 맵(LinkedHashMap/HashMap) 사용
			var payload = new java.util.LinkedHashMap<String, Object>();
			payload.put("userId", user.getUserId());
			payload.put("email", user.getEmail());
			payload.put("userNm", user.getUserNm());
			payload.put("picture", user.getPicture());
			payload.put("role", user.getRole());
			
			request.getSession(true).setAttribute("USER", payload);
			
			
			if(srvcSeq == null || srvcSeq == 0) {
				response.sendRedirect("/pg/home");
			}else{
				String srvcClbckUrl = srvcService.findSrvcClbckUrl(srvcSeq);
				response.sendRedirect(srvcClbckUrl);
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
        
	}

}
