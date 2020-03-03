package cn.lj.pdl.dto.dataset;

import lombok.Data;

import java.util.Map;

/**
 * @author luojian
 * @date 2020/1/3
 */
@Data
public class DatasetImagesNumberDetailResponse {
    private Integer totalNumber;
    private Integer annotatedNumber;
    private Integer unAnnotatedNumber;
    private Map<String, Integer> classNameToAnnotatedNumber;
}
