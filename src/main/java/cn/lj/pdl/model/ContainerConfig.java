package cn.lj.pdl.model;

import lombok.Data;

/**
 * @author luojian
 * @date 2019/11/30
 */
@Data
public class ContainerConfig {
    Integer memory;
    Integer cpuNumber;
    Integer gpuNumber;
    Integer gpuMemory;

}
