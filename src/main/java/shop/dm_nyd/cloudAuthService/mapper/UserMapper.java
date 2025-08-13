package shop.dm_nyd.cloudAuthService.mapper;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Mapper;

import shop.dm_nyd.cloudAuthService.vo.UserVo;

@Mapper
public interface UserMapper {
	
	UserVo findUser(String userId) throws SQLException;

	void saveUser(UserVo userVo) throws SQLException;
}
