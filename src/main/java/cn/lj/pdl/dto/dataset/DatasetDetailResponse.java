package cn.lj.pdl.dto.dataset;

import cn.lj.pdl.model.DatasetDO;
import lombok.Data;

/**
 * @author luojian
 * @date 2020/1/3
 */
@Data
public class DatasetDetailResponse {
    private DatasetDO datasetDO;
    private DatasetImagesNumberDetailResponse datasetImagesNumberDetailResponse;
}
