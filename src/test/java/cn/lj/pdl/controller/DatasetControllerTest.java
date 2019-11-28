package cn.lj.pdl.controller;

import cn.lj.pdl.constant.AlgoType;
import cn.lj.pdl.dto.Body;
import cn.lj.pdl.dto.dataset.DatasetCreateRequest;
import cn.lj.pdl.utils.CommonUtil;
import cn.lj.pdl.utils.TestUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.tuple.Triple;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author luojian
 * @date 2019/11/26
 */
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class DatasetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private String testUsername;
    private String testUserPassword;
    private String testToken;

    @Before
    public void before() {
        Triple<String, String, String> triple = TestUtil.registerTestUser();
        testUsername = triple.getLeft();
        testUserPassword = triple.getMiddle();
        testToken = triple.getRight();
        System.out.println(String.format("Before: 测试账号添加成功!(账号:%s, 密码:%s)", testUsername, testUserPassword));
    }

    @After
    public void after() {
        TestUtil.removeTestUser(testUsername);
        System.out.println(String.format("After: 测试账号删除成功!(账号:%s, 密码:%s)", testUsername, testUserPassword));
    }

    @Test
    public void testCreateDatasetSuccess() throws Exception {
        // 请求参数
        DatasetCreateRequest param = new DatasetCreateRequest();
        param.setName(CommonUtil.generateUuid());
        param.setDescription("test_dataset_description");
        param.setAlgoType(AlgoType.CLASSIFICATION);
        param.setClassesNumber(3);
        param.setClassesNameList(Arrays.asList("cat", "dog", "pig"));
        String content = JSON.toJSONString(param);

        // 设置http请求包
        RequestBuilder requestBuilder = post("/dataset/create")
                .contentType(TestUtil.APPLICATION_JSON_UTF8) // 请求的Content-Type
                .accept(TestUtil.APPLICATION_JSON_UTF8)      // 响应的Content-Type
                .header(TestUtil.TOKEN_HEADER, testToken)   // token
                .content(content);

        // 模拟http请求，并获取http响应包
        MockHttpServletResponse httpResponse = mockMvc.perform(requestBuilder).andDo(print()).andReturn().getResponse();

        // 获取 status, body
        int status = httpResponse.getStatus();
        Body body = JSON.parseObject(httpResponse.getContentAsString(), Body.class);

        // 校验
        Assertions.assertThat(status).isEqualTo(HttpStatus.OK.value()); // 校验 status
        Assertions.assertThat(body.getSuccess()).isEqualTo(true);

    }
}
