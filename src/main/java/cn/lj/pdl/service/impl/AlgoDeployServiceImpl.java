package cn.lj.pdl.service.impl;

import cn.lj.pdl.constant.DeployStatus;
import cn.lj.pdl.constant.Framework;
import cn.lj.pdl.constant.K8sConstants;
import cn.lj.pdl.constant.StorageConstants;
import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.algodeploy.AlgoDeployCreateRequest;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.mapper.AlgoDeployMapper;
import cn.lj.pdl.model.AlgoDeployDO;
import cn.lj.pdl.service.AlgoDeployService;
import cn.lj.pdl.service.K8sService;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author luojian
 * @date 2019/12/2
 */
@Service
public class AlgoDeployServiceImpl implements AlgoDeployService {

    private AlgoDeployMapper algoDeployMapper;
    private StorageService storageService;
    private K8sService k8sService;

    @Autowired
    public AlgoDeployServiceImpl(AlgoDeployMapper algoDeployMapper,
                                 StorageService storageService,
                                 K8sService k8sService) {
        this.algoDeployMapper = algoDeployMapper;
        this.storageService = storageService;
        this.k8sService = k8sService;
    }

    @Override
    public void create(AlgoDeployCreateRequest request, MultipartFile codeZipFile, String requestUsername) throws IOException {
        String uuid = CommonUtil.generateUuidStartWithAlphabet();

        // 文件服务 创建目录
        storageService.createDirs(
                StorageConstants.getAlgoDeployRootPath(),
                StorageConstants.getAlgoDeployDirPath(uuid)
        );

        // 文件服务 上传代码文件
        String codeZipFilePath = StorageConstants.getAlgoDeployCodeZipFilePath(uuid, codeZipFile.getOriginalFilename());
        storageService.uploadFile(codeZipFilePath, codeZipFile.getBytes());

        create(request, codeZipFilePath, uuid, requestUsername);
    }

    @Override
    public Long create(AlgoDeployCreateRequest request, String codeZipFilePath, String algoDeployUuid, String requestUsername) {

        // 文件服务 创建目录
        storageService.createDirs(
                StorageConstants.getAlgoDeployRootPath(),
                StorageConstants.getAlgoDeployDirPath(algoDeployUuid)
        );

        // k8s服务
        List<String> args = new ArrayList<String>() {{
            add(K8sConstants.ALGO_DEPLOY_ENTRY);
            add("--user_code_oss_path=" + codeZipFilePath);
            add("--main_class_path=" + request.getMainClassPath());
        }};
        String serviceUrl = k8sService.createDeploymentAndService(
                algoDeployUuid,
                request.getReplicas(),
                K8sConstants.getAlgoDeployImageName(request.getLanguage(), request.getFramework()),
                K8sConstants.ALGO_DEPLOY_PORT,
                K8sConstants.ALGO_DEPLOY_COMMAND,
                args
        );

        // 数据库
        AlgoDeployDO algoDeployDO = new AlgoDeployDO();
        algoDeployDO.setCreatorName(requestUsername);
        algoDeployDO.setName(request.getName());
        algoDeployDO.setLanguage(request.getLanguage());
        algoDeployDO.setFramework(request.getFramework());
        algoDeployDO.setCodeZipFilePath(codeZipFilePath);
        algoDeployDO.setMainClassPath(request.getMainClassPath());
        algoDeployDO.setUuid(algoDeployUuid);
        algoDeployDO.setStatus(DeployStatus.SERVING);
        algoDeployDO.setServiceUrl(serviceUrl);
        algoDeployDO.setReplicas(request.getReplicas());
        algoDeployDO.setAvailableReplicas(0);
        algoDeployMapper.insert(algoDeployDO);
        return algoDeployDO.getId();
    }

    @Override
    public void stop(Long id, String requestUsername) {

        AlgoDeployDO algoDeployDO = algoDeployMapper.findById(id);

        // id 不存在
        if (algoDeployDO == null) {
            throw new BizException(BizExceptionEnum.ALGO_DEPLOY_NOT_EXIST);
        }

        // 不是该算法部署的创建者
        if (!Objects.equals(requestUsername, algoDeployDO.getCreatorName())) {
            throw new BizException(BizExceptionEnum.USER_IS_NOT_CREATOR);
        }

        // 已在退出状态中
        if (algoDeployDO.getStatus().equals(DeployStatus.EXITED)) {
            throw new BizException(BizExceptionEnum.ALGO_DEPLOY_ALREADY_IN_EXITED);
        }

        // k8s服务
        k8sService.deleteDeploymentAndService(algoDeployDO.getUuid());

        // 数据库
        algoDeployMapper.updateStatus(id, DeployStatus.EXITED);
        algoDeployMapper.updateServiceUrl(id, null);
        algoDeployMapper.updateReplicas(id, 0);
        algoDeployMapper.updateAvailableReplicas(id, 0);
    }

    @Override
    public void start(Long id, String requestUsername) {

        AlgoDeployDO algoDeployDO = algoDeployMapper.findById(id);

        // id 不存在
        if (algoDeployDO == null) {
            throw new BizException(BizExceptionEnum.ALGO_DEPLOY_NOT_EXIST);
        }

        // 不是该算法部署的创建者
        if (!Objects.equals(requestUsername, algoDeployDO.getCreatorName())) {
            throw new BizException(BizExceptionEnum.USER_IS_NOT_CREATOR);
        }

        // 已在服务状态中
        if (algoDeployDO.getStatus().equals(DeployStatus.SERVING)) {
            throw new BizException(BizExceptionEnum.ALGO_DEPLOY_ALREADY_IN_SERVING);
        }

        // k8s服务
        List<String> args = new ArrayList<String>() {{
            add(K8sConstants.ALGO_DEPLOY_ENTRY);
            add("--user_code_oss_path=" + algoDeployDO.getCodeZipFilePath());
            add("--main_class_path=" + algoDeployDO.getMainClassPath());
        }};
        String serviceUrl = k8sService.createDeploymentAndService(
                algoDeployDO.getUuid(),
                1,
                K8sConstants.getAlgoDeployImageName(algoDeployDO.getLanguage(), algoDeployDO.getFramework()),
                K8sConstants.ALGO_DEPLOY_PORT,
                K8sConstants.ALGO_DEPLOY_COMMAND,
                args
        );

        // 数据库
        algoDeployMapper.updateStatus(id, DeployStatus.SERVING);
        algoDeployMapper.updateServiceUrl(id, serviceUrl);
        algoDeployMapper.updateReplicas(id, 1);
        algoDeployMapper.updateAvailableReplicas(id, 0);
    }

    @Override
    public void scale(Long id, int replicas, String requestUsername) {
        AlgoDeployDO algoDeployDO = algoDeployMapper.findById(id);

        // id 不存在
        if (algoDeployDO == null) {
            throw new BizException(BizExceptionEnum.ALGO_DEPLOY_NOT_EXIST);
        }

        // 不是该算法部署的创建者
        if (!Objects.equals(requestUsername, algoDeployDO.getCreatorName())) {
            throw new BizException(BizExceptionEnum.USER_IS_NOT_CREATOR);
        }

        // 已在退出状态中
        if (algoDeployDO.getStatus().equals(DeployStatus.EXITED)) {
            throw new BizException(BizExceptionEnum.ALGO_DEPLOY_ALREADY_IN_EXITED);
        }

        k8sService.scaleDeployment(algoDeployDO.getUuid(), replicas);
    }

    @Override
    public void updateCodeModel(Long id, MultipartFile codeZipFile, String mainClassPath, String requestUsername) throws IOException {
        AlgoDeployDO algoDeployDO = algoDeployMapper.findById(id);

        // id 不存在
        if (algoDeployDO == null) {
            throw new BizException(BizExceptionEnum.ALGO_DEPLOY_NOT_EXIST);
        }

        // 不是该算法部署的创建者
        if (!Objects.equals(requestUsername, algoDeployDO.getCreatorName())) {
            throw new BizException(BizExceptionEnum.USER_IS_NOT_CREATOR);
        }

        // 文件服务 删除原来的代码文件，更新代码文件
        storageService.deleteFile(algoDeployDO.getCodeZipFilePath());
        String codeZipFilePath = StorageConstants.getAlgoDeployCodeZipFilePath(algoDeployDO.getUuid(), codeZipFile.getOriginalFilename());
        storageService.uploadFile(codeZipFilePath, codeZipFile.getBytes());

        mainClassPath = StringUtils.trimToNull(mainClassPath);
        String finalMainClassPath = mainClassPath == null ? algoDeployDO.getMainClassPath() : mainClassPath;

        // k8s服务
        List<String> args = new ArrayList<String>() {{
            add(K8sConstants.ALGO_DEPLOY_ENTRY);
            add("--user_code_oss_path=" + codeZipFilePath);
            // 更新主类路径，若为null则沿用之前的主类路径
            add("--main_class_path=" + finalMainClassPath);
            // 为了确保可以滚动更新成功，添加一个随机的参数 update_random_uuid，以方便 K8s Deployment 滚动更新
            add("--update_random_uuid=" + CommonUtil.generateUuid());
        }};
        k8sService.replaceDeploymentWithNewArgs(algoDeployDO.getUuid(), args);

        // 数据库
        algoDeployMapper.updateCodeZipFilePath(algoDeployDO.getId(), codeZipFilePath);
        if (!algoDeployDO.getMainClassPath().equals(finalMainClassPath)) {
            algoDeployMapper.updateMainClassPath(algoDeployDO.getId(), finalMainClassPath);
        }
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
        Integer totalItemsNumber = algoDeployMapper.countByCondition(condition);

        // 计算总页数, 起码 1 页(即使数据行数 == 0)
        Integer totalPagesNumber = Math.max(1, (int) Math.ceil(totalItemsNumber * 1.0 / pageSize));

        // 获取符合条件的数据，如果查询的页面超出了数据条数范围，那就无需再查数据库
        List<AlgoDeployDO> list = pageInfo.getStartIndex() >= totalItemsNumber
                ? new ArrayList<>()
                : algoDeployMapper.findByCondition(condition, pageInfo);

        return new PageResponse<>(pageNumber, pageSize, totalItemsNumber, totalPagesNumber, list);

    }
}
