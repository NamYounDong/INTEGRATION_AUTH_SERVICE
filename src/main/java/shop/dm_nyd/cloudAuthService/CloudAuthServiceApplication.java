package shop.dm_nyd.cloudAuthService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("shop.dm_nyd.cloudAuthService.mapper")
public class CloudAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudAuthServiceApplication.class, args);
	}

}
