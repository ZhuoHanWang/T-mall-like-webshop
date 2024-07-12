
package ltd.Emallix.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



// MapperScan注解指定了mapper类的路径，这样装填的时候无需手动注册为bean了
@MapperScan("ltd.Emallix.mall.dao")
@SpringBootApplication
public class EmallixApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmallixApplication.class, args);
    }
}
