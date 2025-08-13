package shop.dm_nyd.cloudAuthService.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN,
    MANAGER,
    USER;
}