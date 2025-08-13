package shop.dm_nyd.cloudAuthService.mapper;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import shop.dm_nyd.cloudAuthService.vo.SrvcVo;


@Mapper
public interface SrvcMapper {

	List<SrvcVo> selectSrvcList(SrvcVo param) throws SQLException;

	String findSrvcClbckUrl(Integer srvcSeq) throws SQLException;
	
}
