package common;

import java.io.Serializable;
import java.util.List;

/**
 * @author: scyang
 * @date: 2019-09-04 20:17:32
 */
public class PageBean<T> implements Serializable {
    private Integer pageNum;
    private Integer pageSize;
    private Integer totallCount;
    private Integer totalPage;
    private List<T> data;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotallCount() {
        return totallCount;
    }

    public void setTotallCount(Integer totallCount) {
        this.totallCount = totallCount;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
