package shop.dm_nyd.cloudAuthService.vo;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Lombok 어노테이션 - 아래의 메서드들을 자동 생성해줍니다.
@Data // Getter, Setter, toString, equals, hashCode 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@NoArgsConstructor  // 기본 생성자 (파라미터 없는 생성자) 자동 생성
public class UserVo extends BaseVo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String userId;
	private String userNm;
	private String picture;
	private String email;
	private String phonNo;
	private String role;
}
