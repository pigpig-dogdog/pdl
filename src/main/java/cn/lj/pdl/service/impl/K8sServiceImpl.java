package cn.lj.pdl.service.impl;

import cn.lj.pdl.constant.DeployStatus;
import cn.lj.pdl.constant.K8sConstants;
import cn.lj.pdl.constant.TrainStatus;
import cn.lj.pdl.dto.k8s.ContainerImageResponse;
import cn.lj.pdl.dto.k8s.DeploymentResponse;
import cn.lj.pdl.dto.k8s.JobResponse;
import cn.lj.pdl.dto.k8s.ServiceResponse;
import cn.lj.pdl.service.K8sService;
import cn.lj.pdl.utils.CommonUtil;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerImage;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.api.model.batch.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.JobStatus;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author luojian
 * @date 2019/12/17
 */
@Service
@Slf4j
public class K8sServiceImpl implements K8sService {
    private static KubernetesClient client = new DefaultKubernetesClient();

    @Override
    public void createJob(String name, String image, String command, List<String> args, String creator, String algoTrainName) {
        Job job = new JobBuilder()
                .withApiVersion("batch/v1")
                .withNewMetadata()
                    .withName(name)
                    .addToLabels(K8sConstants.LABEL_APP, name)
                    .addToLabels(K8sConstants.LABEL_CREATOR, creator)
                    .addToLabels(K8sConstants.LABEL_ALGO_TRAIN_NAME, algoTrainName)
                .endMetadata()
                .withNewSpec()
                    // 错误重试0次
                    .withBackoffLimit(0)
                    .withNewTemplate()
                        .withNewSpec()
                            .addNewContainer()
                                .withName(name)
                                .withImage(image)
                                .withCommand(command)
                                .withArgs(args)
                            .endContainer()
                            .withRestartPolicy("Never")
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build();

        client.batch().jobs().inNamespace(K8sConstants.NAMESPACE_DEFAULT).create(job);
    }

    @Override
    public void deleteJob(String name) {
        client.batch().jobs().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(name).delete();
    }

    @Override
    public String createDeploymentAndService(String name, Integer replicas, String image, Integer containerPort, String command, List<String> args, String creator, String algoDeployName) {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                    .withName(name)
                    .addToLabels(K8sConstants.LABEL_CREATOR, creator)
                    .addToLabels(K8sConstants.LABEL_ALGO_DEPLOY_NAME, algoDeployName)
                .endMetadata()
                .withNewSpec()
                    .withReplicas(replicas)
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels(K8sConstants.LABEL_APP, name)
                        .endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName(name)
                                .withImage(image)
                                .withCommand(command)
                                .withArgs(args)
                                .addNewPort()
                                    .withContainerPort(containerPort)
                                .endPort()
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                    .withNewSelector()
                        .addToMatchLabels(K8sConstants.LABEL_APP, name)
                    .endSelector()
                .endSpec()
                .build();

        client.apps().deployments().inNamespace(K8sConstants.NAMESPACE_DEFAULT).create(deployment);

        io.fabric8.kubernetes.api.model.Service service = new ServiceBuilder()
                .withNewMetadata()
                    .withName(name)
                    .addToLabels(K8sConstants.LABEL_CREATOR, creator)
                    .addToLabels(K8sConstants.LABEL_ALGO_DEPLOY_NAME, algoDeployName)
                .endMetadata()
                .withNewSpec()
                    .withType("NodePort")
                    .withSelector(Collections.singletonMap(K8sConstants.LABEL_APP, name))
                    .withPorts()
                    .addNewPort()
                        .withName(K8sConstants.PORT_NAME)
                        .withPort(containerPort)
                    .endPort()
                .endSpec()
                .build();

        client.services().inNamespace(K8sConstants.NAMESPACE_DEFAULT).create(service);

        String serviceUrl = client.services().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(name).getURL(K8sConstants.PORT_NAME);
        return serviceUrl.replaceAll("tcp", "http");
    }

    @Override
    public void deleteDeploymentAndService(String name) {
        client.services().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(name).delete();
        client.apps().deployments().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(name).delete();
    }

    @Override
    public void scaleDeployment(String deploymentName, int replicas) {
        client.apps().deployments().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(deploymentName).scale(replicas);
    }

    @Override
    public void replaceDeploymentWithNewArgs(String deploymentName, List<String> args) {
        if (client.apps().deployments().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(deploymentName).get() == null) {
            return;
        }

        client.apps().deployments().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(deploymentName).edit()
                .editSpec()
                    .editTemplate()
                        .editSpec()
                            .editContainer(0)
                                .withArgs(args)
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .done();
    }

    @Override
    public String getJobLog(String jobName) {
        return client.batch().jobs().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(jobName).getLog(true);
    }

    @Override
    public TrainStatus getJobStatus(String jobName) {
        Job job = client.batch().jobs().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(jobName).get();
        JobStatus jobStatus = job.getStatus();
        if (jobStatus.getActive() != null) {
            return TrainStatus.RUNNING;
        } else if (jobStatus.getSucceeded() != null) {
            return TrainStatus.SUCCESS;
        } else {
            return TrainStatus.FAILED;
        }
    }

    @Override
    public DeployStatus getServiceStatus(String serviceName) {
        io.fabric8.kubernetes.api.model.Service service = client.services().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(serviceName).get();
        return service == null ? DeployStatus.EXITED : DeployStatus.SERVING;
    }

    @Override
    public Pair<Integer, Integer> getDeploymentReplicas(String deploymentName) {
        Deployment deployment = client.apps().deployments().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(deploymentName).get();
        if (deployment == null) {
            return new ImmutablePair<>(0, 0);
        }

        Integer replicas = deployment.getStatus().getReplicas();
        if (replicas == null) {
            replicas = 0;
        }

        Integer availableReplicas = deployment.getStatus().getAvailableReplicas();
        if (availableReplicas == null) {
            availableReplicas = 0;
        }

        return new ImmutablePair<>(replicas, availableReplicas);
    }

    @Override
    public List<ServiceResponse> listServices() {
        List<ServiceResponse> list = new ArrayList<>();
        List<io.fabric8.kubernetes.api.model.Service> services = client.services().inNamespace(K8sConstants.NAMESPACE_DEFAULT).list().getItems();

        for (io.fabric8.kubernetes.api.model.Service service : services) {
            String serviceName = service.getMetadata().getName();
            if ("kubernetes".equals(serviceName)) {
                continue;
            }
            try {
                String creator = CommonUtil.decodeChinese(service.getMetadata().getLabels().get(K8sConstants.LABEL_ALGO_DEPLOY_NAME));
                String algoDeployName = CommonUtil.decodeChinese(service.getMetadata().getLabels().get(K8sConstants.LABEL_CREATOR));
                String serviceUrl = client.services().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(serviceName).getURL(K8sConstants.PORT_NAME).replaceAll("tcp", "http");
                String createTime = service.getMetadata().getCreationTimestamp();
                ServiceResponse serviceResponse = new ServiceResponse();
                serviceResponse.setAlgoDeployName(algoDeployName);
                serviceResponse.setCreator(creator);
                serviceResponse.setServiceUrl(serviceUrl);
                serviceResponse.setCreateTime(createTime);
                list.add(serviceResponse);
            } catch (DecoderException | NullPointerException e) {
                log.error(e.getMessage());
            }
        }

        return list;
    }

    @Override
    public List<DeploymentResponse> listDeployments() {
        List<DeploymentResponse> list = new ArrayList<>();
        List<Deployment> deployments = client.apps().deployments().inNamespace(K8sConstants.NAMESPACE_DEFAULT).list().getItems();
        for (Deployment deployment : deployments) {
            try {
                String creator = CommonUtil.decodeChinese(deployment.getMetadata().getLabels().get(K8sConstants.LABEL_CREATOR));
                String algoDeployName = CommonUtil.decodeChinese(deployment.getMetadata().getLabels().get(K8sConstants.LABEL_ALGO_DEPLOY_NAME));
                String createTime = deployment.getMetadata().getCreationTimestamp();
                Integer replicas = deployment.getStatus().getReplicas();
                if (replicas == null) {
                    replicas = 0;
                }

                Integer availableReplicas = deployment.getStatus().getAvailableReplicas();
                if (availableReplicas == null) {
                    availableReplicas = 0;
                }

                Container container = deployment.getSpec().getTemplate().getSpec().getContainers().get(0);
                String image = container.getImage();
                String command = container.getCommand().get(0);
                String args = String.join(" ", container.getArgs());

                DeploymentResponse deploymentResponse = new DeploymentResponse();
                deploymentResponse.setAlgoDeployName(algoDeployName);
                deploymentResponse.setCreator(creator);
                deploymentResponse.setCreateTime(createTime);
                deploymentResponse.setReplicas(replicas);
                deploymentResponse.setAvailableReplicas(availableReplicas);
                deploymentResponse.setImage(image);
                deploymentResponse.setCommand(command);
                deploymentResponse.setArgs(args);

                list.add(deploymentResponse);
            } catch (DecoderException | NullPointerException e) {
                log.error(e.getMessage());
            }

        }

        return list;
    }

    @Override
    public List<JobResponse> listJobs() {
        List<JobResponse> list = new ArrayList<>();
        List<Job> jobs = client.batch().jobs().inNamespace(K8sConstants.NAMESPACE_DEFAULT).list().getItems();

        for (Job job : jobs) {

            try {
                String creator = CommonUtil.decodeChinese(job.getMetadata().getLabels().get(K8sConstants.LABEL_CREATOR));
                String algoTrainName = CommonUtil.decodeChinese(job.getMetadata().getLabels().get(K8sConstants.LABEL_ALGO_TRAIN_NAME));
                String createTime = job.getMetadata().getCreationTimestamp();
                String endTime = null;

                JobStatus jobStatus = job.getStatus();
                String status;
                if (jobStatus.getActive() != null) {
                    status = "RUNNING";
                } else if (jobStatus.getSucceeded() != null) {
                    status = "SUCCESS";
                    endTime = jobStatus.getCompletionTime();
                } else {
                    status = "FAILED";
                    endTime = jobStatus.getConditions().get(0).getLastProbeTime();
                }

                Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
                String image = container.getImage();
                String command = container.getCommand().get(0);
                String args = String.join(" ", container.getArgs());

                JobResponse jobResponse = new JobResponse();
                jobResponse.setAlgoTrainName(algoTrainName);
                jobResponse.setCreator(creator);
                jobResponse.setCreateTime(createTime);
                jobResponse.setEndTime(endTime);
                jobResponse.setStatus(status);
                jobResponse.setImage(image);
                jobResponse.setCommand(command);
                jobResponse.setArgs(args);

                list.add(jobResponse);
            } catch (NullPointerException | DecoderException e) {
                log.error(e.getMessage());
            }


        }

        return list;
    }

    @Override
    public List<ContainerImageResponse> listImages() {
        List<ContainerImageResponse> list = new ArrayList<>();
        List<ContainerImage> containerImages = client.nodes().withName("minikube").get().getStatus().getImages();
        DecimalFormat df = new DecimalFormat("0.0");
        for (ContainerImage containerImage : containerImages) {
            String containerShortestName = containerImage.getNames().get(0);
            for (int i = 1; i < containerImage.getNames().size(); i++) {
                String s = containerImage.getNames().get(i);
                if (containerShortestName.length() > s.length()) {
                    containerShortestName = s;
                }
            }

            String sizeMb = df.format(containerImage.getSizeBytes() * 1.0 / 1024 / 1024) + " MB";
            ContainerImageResponse containerImageResponse = new ContainerImageResponse();
            containerImageResponse.setName(containerShortestName);
            containerImageResponse.setSizeMb(sizeMb);
            list.add(containerImageResponse);
        }
        return list;
    }

    private String getPodLog(String podName) {
        return client.pods().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withName(podName).getLog(true);
    }

    private PodList listPodByJobName(String jobName) {
//        podList.getItems().forEach(pod -> {
//            System.out.println(pod.getMetadata().getName());
//        });
        return client.pods().inNamespace(K8sConstants.NAMESPACE_DEFAULT).withLabel(K8sConstants.LABEL_JOB_NAME, jobName).list();
    }
}
