package cn.lj.pdl;

import cn.lj.pdl.constant.K8sConstants;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author luojian
 * @date 2019/12/29
 */
@Slf4j
public class Demo {

    private static KubernetesClient client = new DefaultKubernetesClient();

    public static void main(String[] args) {
//        List<io.fabric8.kubernetes.api.model.Service> services = client.services().inNamespace(K8sConstants.NAMESPACE_DEFAULT).list().getItems();
//        for (io.fabric8.kubernetes.api.model.Service service : services) {
//            System.out.println(service.getMetadata().getName());
//        }

        List<Node> nodeList = client.nodes().list().getItems();

        System.out.println(nodeList);


    }

    public static List<Node> listNodes() {
        List<Node> nodeList = client.nodes().list().getItems();
        return null;
    }
}
