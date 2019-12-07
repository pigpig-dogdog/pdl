package cn.lj.pdl.component;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.api.model.batch.JobBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.Collections;
import java.util.List;

/**
 * @author luojian
 * @date 2019/12/6
 */
public class K8sComponent {
    private static KubernetesClient client = new DefaultKubernetesClient();
    private static final String NAMESPACE_DEFAULT = "default";
    private static final String LABEL_APP = "app";
    private static final String LABEL_JOB_NAME = "job_name";
    private static final String PORT_NAME = "port-name";
    public static final String IMAGE_ALGO_SERVICE = "pdl-algo:0.1";

    public static void runJob(String name, String image, String command, List<String> args) {
        Job job = new JobBuilder()
                .withApiVersion("batch/v1")
                .withNewMetadata()
                    .withName(name)
                    .withLabels(Collections.singletonMap(LABEL_APP, name))
                .endMetadata()
                .withNewSpec()
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

        client.batch().jobs().inNamespace(NAMESPACE_DEFAULT).create(job);
    }

    public static String deployAndService(String name, Integer replicas, String image, Integer containerPort) {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                    .withName(name)
                .endMetadata()
                .withNewSpec()
                    .withReplicas(replicas)
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels(LABEL_APP, name)
                        .endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName(name)
                                .withImage(image)
                                .addNewPort()
                                    .withContainerPort(containerPort)
                                .endPort()
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                    .withNewSelector()
                        .addToMatchLabels(LABEL_APP, name)
                    .endSelector()
                .endSpec()
                .build();

        client.apps().deployments().inNamespace(NAMESPACE_DEFAULT).create(deployment);

        Service service = new ServiceBuilder()
                .withNewMetadata()
                    .withName(name)
                .endMetadata()
                .withNewSpec()
                    .withType("NodePort")
                    .withSelector(Collections.singletonMap(LABEL_APP, name))
                    .withPorts()
                    .addNewPort()
                        .withName(PORT_NAME)
                        .withPort(containerPort)
                    .endPort()
                .endSpec()
                .build();

        client.services().inNamespace(NAMESPACE_DEFAULT).create(service);

        String serviceUrl = client.services().inNamespace(NAMESPACE_DEFAULT).withName(name).getURL(PORT_NAME);
        return serviceUrl.replaceAll("tcp", "http");
    }

    public static String deployAndService(String name, Integer replicas, String image, Integer containerPort, String command, List<String> args) {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                    .withName(name)
                .endMetadata()
                .withNewSpec()
                    .withReplicas(replicas)
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels(LABEL_APP, name)
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
                        .addToMatchLabels(LABEL_APP, name)
                    .endSelector()
                .endSpec()
                .build();

        client.apps().deployments().inNamespace(NAMESPACE_DEFAULT).create(deployment);

        Service service = new ServiceBuilder()
                .withNewMetadata()
                .withName(name)
                .endMetadata()
                .withNewSpec()
                .withType("NodePort")
                .withSelector(Collections.singletonMap(LABEL_APP, name))
                .withPorts()
                .addNewPort()
                .withName(PORT_NAME)
                .withPort(containerPort)
                .endPort()
                .endSpec()
                .build();

        client.services().inNamespace(NAMESPACE_DEFAULT).create(service);

        String serviceUrl = client.services().inNamespace(NAMESPACE_DEFAULT).withName(name).getURL(PORT_NAME);
        return serviceUrl.replaceAll("tcp", "http");
    }

    public static void scale(String deploymentName, int scale) {
        client.apps().deployments().inNamespace(NAMESPACE_DEFAULT).withName(deploymentName).scale(scale);
    }

    public static String getPodLog(String podName) {
        return client.pods().inNamespace(NAMESPACE_DEFAULT).withName(podName).getLog(true);
    }

    public static String getJobLog(String jobName) {
        return client.batch().jobs().inNamespace(NAMESPACE_DEFAULT).withName(jobName).getLog(true);
    }

    public static PodList listPodByJobName(String jobName) {
//        podList.getItems().forEach(pod -> {
//            System.out.println(pod.getMetadata().getName());
//        });
        return client.pods().inNamespace(NAMESPACE_DEFAULT).withLabel(LABEL_JOB_NAME, jobName).list();
    }
}
