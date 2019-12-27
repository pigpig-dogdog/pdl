package cn.lj.pdl;

import org.modelmapper.ModelMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author luojian
 * @date 2019/11/23
 */
@SpringBootApplication
@MapperScan("cn.lj.pdl.mapper")
@EnableScheduling
public class PdlApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdlApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
