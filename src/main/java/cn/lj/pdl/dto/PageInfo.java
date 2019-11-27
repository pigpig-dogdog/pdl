package cn.lj.pdl.dto;

import lombok.Data;

/**
 * @author luojian
 * @date 2019/11/27
 */
@Data
public class PageInfo {
    /**
     * 页号
     */
    private Integer pageNumber;

    /**
     * 每页记录数
     */
    private Integer pageSize;

    /**
     * 数据库分页起始坐标，limit startIndex, size
     * 由前两项计算得出，并不是直接赋值
     */
    private Integer startIndex;

    public PageInfo() {
        this.pageNumber = 1;
        this.pageSize = 10;
        this.startIndex = 0;
    }

    public PageInfo(Integer pageNumber, Integer pageSize) {
        this.pageNumber = (pageNumber == null || pageNumber < 1) ? 1 : pageNumber;
        this.pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        this.startIndex = (this.pageNumber - 1) * this.pageSize;
    }
}
