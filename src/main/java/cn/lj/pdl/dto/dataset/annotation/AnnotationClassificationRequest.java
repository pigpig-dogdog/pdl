package cn.lj.pdl.dto.dataset.annotation;

import lombok.Data;

import java.util.List;

/**
 * @author luojian
 * @date 2020/1/6
 */
@Data
public class AnnotationClassificationRequest {
    private List<ImageIdToClassName> imageIdToClassNameList;
}
