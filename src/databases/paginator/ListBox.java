package databases.paginator;

import server.comm.DataMap;

import java.util.List;

public class ListBox {
    private PageInfo pageInfo;
    private List<DataMap> list;

    public ListBox(PageInfo pageInfo, List<DataMap> list) {
        this.pageInfo = pageInfo;
        this.list = list;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<DataMap> getList() {
        return list;
    }

    public void setList(List<DataMap> list) {
        this.list = list;
    }
}
