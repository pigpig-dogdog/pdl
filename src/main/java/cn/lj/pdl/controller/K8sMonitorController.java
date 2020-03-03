package cn.lj.pdl.controller;

import cn.lj.pdl.dto.Body;
import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.k8s.ContainerImageResponse;
import cn.lj.pdl.dto.k8s.DeploymentResponse;
import cn.lj.pdl.dto.k8s.JobResponse;
import cn.lj.pdl.dto.k8s.ServiceResponse;
import cn.lj.pdl.service.K8sService;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luojian
 * @date 2019/12/9
 */
@Slf4j
@RestController
@RequestMapping("/container")
@Api(tags = "K8S监控台相关接口")
public class K8sMonitorController {
    private K8sService k8sService;

    @Autowired
    public K8sMonitorController(K8sService k8sService) {
        this.k8sService = k8sService;
    }

    @GetMapping("/listServices")
    public Body<List<ServiceResponse>> listServices() {
        List<ServiceResponse> list = k8sService.listServices();
        return Body.buildSuccess(list);
    }

    @GetMapping("/listDeployments")
    public Body<List<DeploymentResponse>> listDeployments() {
        List<DeploymentResponse> list = k8sService.listDeployments();
        return Body.buildSuccess(list);
    }

    @GetMapping("/listJobs")
    public Body<List<JobResponse>> listJobs() {
        List<JobResponse> list = k8sService.listJobs();
        return Body.buildSuccess(list);

    }

    @GetMapping("/listImages")
    public Body<List<ContainerImageResponse>> listImages() {
        List<ContainerImageResponse> list = k8sService.listImages();
        return Body.buildSuccess(list);
    }

    @GetMapping("/node/list")
    public Body<PageResponse<Node>> nodeList(Integer pageNumber, Integer pageSize) {
        PageResponse<Node> list = k8sService.nodeList(new PageInfo(pageNumber, pageSize));
        return Body.buildSuccess(list);
    }

    @GetMapping("/node/detail")
    public Body<Node> nodeDetail(String nodeName) {
        Node node = k8sService.nodeDetail(nodeName);
        return Body.buildSuccess(node);
    }

//    @GetMapping("/job/list")
//    public Body<PageResponse<Job>> jobList(Integer pageNumber, Integer pageSize) {
//        PageResponse<Job> list = k8sService.jobList(new PageInfo(pageNumber, pageSize));
//        return Body.buildSuccess(list);
//    }
//
//    @GetMapping("/job/detail")
//    public Body<Job> jobDetail(String jobName) {
//        Job job = k8sService.jobDetail(jobName);
//        return Body.buildSuccess(job);
//    }
}
