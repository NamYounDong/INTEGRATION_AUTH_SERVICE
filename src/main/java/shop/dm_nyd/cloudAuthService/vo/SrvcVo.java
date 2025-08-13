package shop.dm_nyd.cloudAuthService.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getter, Setter, toString, equals, hashCode 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@NoArgsConstructor  // 기본 생성자 (파라미터 없는 생성자) 자동 생성
public class SrvcVo {
	private Integer srvcSeq;
	private String srvcNm;
	private String clbckUrl;
}
