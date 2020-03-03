package cn.lj.pdl.dto.dataset.annotation;

import cn.lj.pdl.model.ImageDO;
import lombok.Data;

import java.util.List;

/**
 * @author luojian
 * @date 2020/1/6
 */
@Data
public class GetPrevOrNextImageResponse {
    private ImageDO imageDO;
    private List<DetectionBbox> bboxes;
}
