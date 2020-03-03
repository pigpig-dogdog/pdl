package cn.lj.pdl.service;

import cn.lj.pdl.constant.DeployStatus;
import cn.lj.pdl.constant.TrainStatus;
import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.k8s.ContainerImageResponse;
import cn.lj.pdl.dto.k8s.DeploymentResponse;
import cn.lj.pdl.dto.k8s.JobResponse;
import cn.lj.pdl.dto.k8s.ServiceResponse;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.Job;
import org.apache.commons.lang3.tuple.Pair;
import sun.jvm.hotspot.debugger.Page;

import java.util.List;

/**
 * @author luojian
 * @date 2019/12/17
 */
public interface K8sService {

    /**
     * 创建 K8s Job
     *
     * @param name K8s Job uuid name
     * @param image 镜像名称
     * @param command 容器运行的命令
     * @param args 参数
     */
    void createJob(String name, String image, String command, List<String> args);

    /**
     * 删除 K8s Job
     *
     * @param name K8s Job uuid name
     */
    void deleteJob(String name);

    /**
     * 创建 K8s Deployment 和 K8s Service
     *
     * @param name K8s Deployment 和 K8s Service uuid name
     * @param replicas 副本数
     * @param image 镜像名称
     * @param containerPort 容器port
     * @param command 容器运行的命令
     * @param args 参数
     * @return String K8S Service URL
     */
    String createDeploymentAndService(String name, Integer replicas, String image, Integer containerPort, String command, List<String> args);

    /**
     * 删除 K8s Deployment 和 K8s Service
     *
     * @param name K8s Deployment 和 K8s Service uuid name
     */
    void deleteDeploymentAndService(String name);

    /**
     * Scale K8s Deployment
     *
     * @param deploymentName K8s Deployment name
     * @param replicas 副本数
     */
    void scaleDeployment(String deploymentName, int replicas);

    /**
     * 重启 K8s Deployment
     *
     * @param deploymentName K8s Deployment name
     * @param args 容器执行参数
     */
    void replaceDeploymentWithNewArgs(String deploymentName, List<String> args);

    /**
     * 获取 K8s Job 日志
     *
     * @param jobName K8s Job Name
     * @return String 日志
     */
    String getJobLog(String jobName);

    /**
     * 获取 K8s Job 状态
     *
     * @param jobName K8s Job Name
     * @return TrainStatus
     */
    TrainStatus getJobStatus(String jobName);

    /**
     * 获取 K8s Service 状态
     * @param serviceName K8s Service Name
     * @return DeployStatus
     */
    DeployStatus getServiceStatus(String serviceName);

    /**
     * 获取 K8s Deployment replicas 与 availableReplicas
     * @param deploymentName K8s Deployment Name
     * @return Pair<Integer, Integer> 分别是 replicas 与 availableReplicas
     */
    Pair<Integer, Integer> getDeploymentReplicas(String deploymentName);

    /**
     * 获取 K8s Service 列表
     *
     * @return List<ServiceResponse>
     */
    List<ServiceResponse> listServices();

    /**
     * 获取 K8s Deployment 列表
     *
     * @return List<DeploymentResponse>
     */
    List<DeploymentResponse> listDeployments();

    /**
     * 获取 K8s Job 列表
     *
     * @return List<JobResponse>
     */
    List<JobResponse> listJobs();

    /**
     * 获取 K8s Image 列表
     *
     * @return List<ContainerImageResponse>
     */
    List<ContainerImageResponse> listImages();

    PageResponse<Node> nodeList(PageInfo pageInfo);

    Node nodeDetail(String nodeName);

//    PageResponse<Job> jobList(PageInfo pageInfo);
//
//    Job jobDetail(String jobName);
//
//    PageResponse<Deployment> deploymentList(PageInfo pageInfo);
//
//    Deployment deploymentDetail(String deploymentName);
//
//    PageResponse<io.fabric8.kubernetes.api.model.Service> serviceList(PageInfo pageInfo);
//
//    io.fabric8.kubernetes.api.model.Service serviceDetail(String serviceName);
//
//    PageResponse<Pod> podList(PageInfo pageInfo);
//
//    Pod podDetail(String podName);

}
