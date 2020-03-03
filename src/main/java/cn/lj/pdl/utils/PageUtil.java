package cn.lj.pdl.utils;

import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.dto.PageResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luojian
 * @date 2020/1/6
 */
public class PageUtil {

    public static <T> PageResponse<T> convertToPageResponse(List<T> totalList, PageInfo pageInfo) {
        // 统计数据行数
        Integer totalItemsNumber = totalList.size();

        // 计算总页数, 起码 1 页(即使数据行数 == 0)
        Integer totalPagesNumber = Math.max(1, (int) Math.ceil(totalItemsNumber * 1.0 / pageInfo.getPageSize()));

        // 获取子数据
        int fromIndex = Math.min(pageInfo.getStartIndex(), totalList.size());
        int toIndex = Math.min(pageInfo.getStartIndex() + pageInfo.getPageSize(), totalList.size());
        List<T> subList = totalList.subList(fromIndex, toIndex);

        PageResponse<T> response = new PageResponse<>();
        response.setPageNumber(pageInfo.getPageNumber());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotalItemsNumber(totalItemsNumber);
        response.setTotalPagesNumber(totalPagesNumber);
        response.setList(subList);
        return response;
    }
}
