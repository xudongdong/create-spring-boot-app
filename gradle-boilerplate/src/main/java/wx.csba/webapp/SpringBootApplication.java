package wx.csba.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @function Spring Boot 应用入口函数
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
public class SpringBootApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
