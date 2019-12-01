package cn.lj.pdl.service.impl;

import cn.lj.pdl.constant.Constants;
import cn.lj.pdl.constant.TrainStatus;
import cn.lj.pdl.constant.WriteMode;
import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.algotrain.AlgoTrainCreateRequest;
import cn.lj.pdl.mapper.AlgoTrainMapper;
import cn.lj.pdl.model.AlgoTrainDO;
import cn.lj.pdl.service.AlgoTrainService;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luojian
 * @date 2019/11/30
 */
@Service
public class AlgoTrainServiceImpl implements AlgoTrainService {

    private AlgoTrainMapper algoTrainMapper;
    private StorageService storageService;

    @Autowired
    public AlgoTrainServiceImpl(AlgoTrainMapper algoTrainMapper,
                                StorageService storageService) {
        this.algoTrainMapper = algoTrainMapper;
        this.storageService = storageService;
    }

    @Override
    public void create(AlgoTrainCreateRequest request, byte[] codeZipFile, String requestUsername) {

        String uuid = CommonUtil.generateUuid();

        // 文件服务 创建目录
        storageService.createDirs(
                Constants.getAlgoTrainRootPath(),
                Constants.getAlgoTrainDirPath(uuid)
        );

        // 文件服务 上传代码文件
        String codeZipFilePath = Constants.getAlgoTrainCodeZipFilePath(uuid);
        storageService.uploadFile(codeZipFilePath, codeZipFile);

        // 文件服务 设置训练任务状态
        String statusFilePath = Constants.getAlgoTrainStatusFilePath(uuid);
        storageService.write(statusFilePath, TrainStatus.WAITING.toString(), WriteMode.OVERWRITE);

        AlgoTrainDO algoTrainDO = new AlgoTrainDO();
        algoTrainDO.setCreatorName(requestUsername);
        algoTrainDO.setName(request.getName());
        algoTrainDO.setFramework(request.getFramework());
        algoTrainDO.setEntryAndArgs(request.getEntryAndArgs());
        algoTrainDO.setResultDirPath(request.getResultDirPath());
        algoTrainDO.setUuid(uuid);
        algoTrainDO.setStatus(TrainStatus.WAITING);
        algoTrainDO.setCodeZipFilePath(codeZipFilePath);
        algoTrainDO.setResultZipFileUrl(null);

        algoTrainMapper.insert(algoTrainDO);
    }

    @Override
    public PageResponse<AlgoTrainDO> list(Integer pageNumber, Integer pageSize) {
        // 分页信息
        PageInfo pageInfo = new PageInfo(pageNumber, pageSize);

        // 条件查询信息
        AlgoTrainDO condition = new AlgoTrainDO();

        // 统计符合条件的数据行数
        Integer totalItemsNumber = algoTrainMapper.countByCondition(condition, pageInfo);

        // 计算总页数, 起码 1 页(即使数据行数 == 0)
        Integer totalPagesNumber = Math.max(1, (int) Math.ceil(totalItemsNumber * 1.0 / pageSize));

        // 获取符合条件的数据，如果查询的页面超出了数据条数范围，那就无需再查数据库
        List<AlgoTrainDO> list = pageInfo.getStartIndex() >= totalItemsNumber
                ? new ArrayList<>()
                : algoTrainMapper.findByCondition(condition, pageInfo);

        return new PageResponse<>(pageNumber, pageSize, totalItemsNumber, totalPagesNumber, list);
    }

}
