package shop.dm_nyd.cloudAuthService.service.impl;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.dm_nyd.cloudAuthService.mapper.UserMapper;
import shop.dm_nyd.cloudAuthService.service.UserService;
import shop.dm_nyd.cloudAuthService.vo.UserVo;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	
	private final UserMapper userMapper;

	@Override
	public UserVo findUser(String email) throws SQLException {
		return userMapper.findUser(email);
	}

	@Override
	public void saveUser(UserVo userVo) throws Exception {
		userMapper.saveUser(userVo);		
	}
	
	
}
