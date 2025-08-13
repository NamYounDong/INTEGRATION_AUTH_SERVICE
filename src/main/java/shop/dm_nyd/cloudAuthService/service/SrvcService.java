package shop.dm_nyd.cloudAuthService.service;

import java.util.List;

import shop.dm_nyd.cloudAuthService.vo.SrvcVo;

public interface SrvcService {
	List<SrvcVo> selectSrvcList(SrvcVo param) throws Exception;
	String findSrvcClbckUrl(Integer srvcSeq) throws Exception;
}
