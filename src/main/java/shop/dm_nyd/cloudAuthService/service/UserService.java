package shop.dm_nyd.cloudAuthService.service;

import shop.dm_nyd.cloudAuthService.vo.UserVo;

public interface UserService {

	UserVo findUser(String email) throws Exception;

	void saveUser(UserVo userVo) throws Exception;

}
