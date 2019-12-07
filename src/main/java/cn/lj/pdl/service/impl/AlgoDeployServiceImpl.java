package cn.lj.pdl.service.impl;

import cn.lj.pdl.component.K8sComponent;
import cn.lj.pdl.constant.Constants;
import cn.lj.pdl.constant.DeployStatus;
import cn.lj.pdl.constant.Framework;
import cn.lj.pdl.constant.WriteMode;
import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.algodeploy.AlgoDeployCreateRequest;
import cn.lj.pdl.mapper.AlgoDeployMapper;
import cn.lj.pdl.model.AlgoDeployDO;
import cn.lj.pdl.service.AlgoDeployService;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luojian
 * @date 2019/12/2
 */
@Service
public class AlgoDeployServiceImpl implements AlgoDeployService {

    private AlgoDeployMapper algoDeployMapper;
    private StorageService storageService;

    @Autowired
    public AlgoDeployServiceImpl(AlgoDeployMapper algoDeployMapper,
                                 StorageService storageService) {
        this.algoDeployMapper = algoDeployMapper;
        this.storageService = storageService;
    }

    @Override
    public void create(AlgoDeployCreateRequest request, byte[] codeZipFile, String requestUsername) {
        String uuid = CommonUtil.generateUuid();

        // 文件服务 创建目录
        storageService.createDirs(
                Constants.getAlgoDeployRootPath(),
                Constants.getAlgoDeployDirPath(uuid)
        );

        // 文件服务 上传代码文件
        String codeZipFilePath = Constants.getAlgoDeployCodeZipFilePath(uuid);
        storageService.uploadFile(codeZipFilePath, codeZipFile);

        // 文件服务 设置部署任务状态
        String statusFilePath = Constants.getAlgoDeployStatusFilePath(uuid);
        storageService.write(statusFilePath, DeployStatus.SERVING.toString(), WriteMode.OVERWRITE);

        // k8s服务
        List<String> args = new ArrayList<String>() {{
            add("service/main.py");
            add("--user_code_oss_path=" + codeZipFilePath);
            add("--main_class_path=" + request.getMainClassPath());
        }};
        String serviceUrl = K8sComponent.deployAndService(uuid, request.getInstanceNumber(), K8sComponent.IMAGE_ALGO_SERVICE, 5000, "python", args);

        AlgoDeployDO algoDeployDO = new AlgoDeployDO();
        algoDeployDO.setCreatorName(requestUsername);
        algoDeployDO.setName(request.getName());
        algoDeployDO.setFramework(request.getFramework());
        algoDeployDO.setCodeZipFilePath(codeZipFilePath);
        algoDeployDO.setMainClassPath(request.getMainClassPath());
        algoDeployDO.setUuid(uuid);
        algoDeployDO.setStatus(DeployStatus.SERVING);
        algoDeployDO.setServiceUrl(serviceUrl);
        algoDeployDO.setInstanceNumber(request.getInstanceNumber());

        algoDeployMapper.insert(algoDeployDO);
    }

    @Override
    public PageResponse<AlgoDeployDO> list(Integer pageNumber, Integer pageSize,
                                           String creatorName, String name, Framework framework, DeployStatus status) {
        // 分页信息
        PageInfo pageInfo = new PageInfo(pageNumber, pageSize);

        // 条件查询信息
        AlgoDeployDO condition = new AlgoDeployDO();
        condition.setCreatorName(creatorName);
        condition.setName(name);
        condition.setFramework(framework);
        condition.setStatus(status);

        // 统计符合条件的数据行数
        Integer totalItemsNumber = algoDeployMapper.countByCondition(condition, pageInfo);

        // 计算总页数, 起码 1 页(即使数据行数 == 0)
        Integer totalPagesNumber = Math.max(1, (int) Math.ceil(totalItemsNumber * 1.0 / pageSize));

        // 获取符合条件的数据，如果查询的页面超出了数据条数范围，那就无需再查数据库
        List<AlgoDeployDO> list = pageInfo.getStartIndex() >= totalItemsNumber
                ? new ArrayList<>()
                : algoDeployMapper.findByCondition(condition, pageInfo);

        return new PageResponse<>(pageNumber, pageSize, totalItemsNumber, totalPagesNumber, list);

    }
}
