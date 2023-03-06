package com.ldw;

import cn.hutool.core.util.IdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class ApplicationTests {
    @Value("${server.port}")//读取文件application下的port
    private String port;
    @Test
    void contextLoads() {
        //System.out.println(IdUtil.fastSimpleUUID());
        System.out.println(port);
    }

}
