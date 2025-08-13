package shop.dm_nyd.cloudAuthService.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.dm_nyd.cloudAuthService.mapper.SrvcMapper;
import shop.dm_nyd.cloudAuthService.service.SrvcService;
import shop.dm_nyd.cloudAuthService.vo.SrvcVo;
import shop.dm_nyd.cloudAuthService.vo.UserVo;

@Service
@RequiredArgsConstructor
public class SrvcServiceImpl implements SrvcService{
	
	private final SrvcMapper srvcMapper;
	
	@Override
	public List<SrvcVo> selectSrvcList(SrvcVo param) throws SQLException {
		return srvcMapper.selectSrvcList(param);
	}

	@Override
	public String findSrvcClbckUrl(Integer srvcSeq) throws Exception {
		return srvcMapper.findSrvcClbckUrl(srvcSeq);
	}
}
