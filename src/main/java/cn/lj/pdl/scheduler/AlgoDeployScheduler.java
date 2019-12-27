package cn.lj.pdl.scheduler;

import cn.lj.pdl.constant.DeployStatus;
import cn.lj.pdl.mapper.AlgoDeployMapper;
import cn.lj.pdl.model.AlgoDeployDO;
import cn.lj.pdl.service.K8sService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author luojian
 * @date 2019/12/19
 */
@Component
@Slf4j
public class AlgoDeployScheduler {
    private AlgoDeployMapper algoDeployMapper;
    private K8sService k8sService;

    @Autowired
    public AlgoDeployScheduler(AlgoDeployMapper algoDeployMapper,
                               K8sService k8sService) {
        this.algoDeployMapper = algoDeployMapper;
        this.k8sService = k8sService;
    }

    @Scheduled(fixedDelay = 1000)
    public void refreshAlgoDeployStatus() {
        // 获取所有状态为 SERVING 的 AlgoDeployDO
        List<AlgoDeployDO> list = algoDeployMapper.findByStatus(DeployStatus.SERVING);

        for (AlgoDeployDO algoDeployDO : list) {
            // 调用k8s服务，查询status, replicas, availableReplicas
            DeployStatus newStatus = k8sService.getServiceStatus(algoDeployDO.getUuid());
            Pair<Integer, Integer> p = k8sService.getDeploymentReplicas(algoDeployDO.getUuid());
            Integer newReplicas = p.getLeft();
            Integer newAvailableReplicas = p.getRight();

            if (!newStatus.equals(DeployStatus.SERVING)) {
                algoDeployMapper.updateStatus(algoDeployDO.getId(), newStatus);
                algoDeployMapper.updateServiceUrl(algoDeployDO.getId(), null);
                log.info("更新AlgoDeploy status, id:{}, name:{}, old:{}, new:{}.", algoDeployDO.getId(), algoDeployDO.getName(), DeployStatus.SERVING.toString(), newStatus);
                log.info("更新AlgoDeploy serviceUrl, id:{}, name:{}, old:{}, new:{}.", algoDeployDO.getId(), algoDeployDO.getName(), algoDeployDO.getServiceUrl(), null);
            }

            if (!Objects.equals(newReplicas, algoDeployDO.getReplicas())) {
                algoDeployMapper.updateReplicas(algoDeployDO.getId(), newReplicas);
                log.info("更新AlgoDeploy replicas, id:{}, name:{}, old:{}, new:{}.", algoDeployDO.getId(), algoDeployDO.getName(), algoDeployDO.getReplicas(), newReplicas);
            }

            if (!Objects.equals(newAvailableReplicas, algoDeployDO.getAvailableReplicas())) {
                algoDeployMapper.updateAvailableReplicas(algoDeployDO.getId(), newAvailableReplicas);
                log.info("更新AlgoDeploy availableReplicas, id:{}, name:{}, old:{}, new:{}.", algoDeployDO.getId(), algoDeployDO.getName(), algoDeployDO.getAvailableReplicas(), newAvailableReplicas);
            }
        }
    }
}
