package databases.paginator;

/**
 * @author
 */
public class PageInfo {
    /** 현재 페이지 **/
    private int page = 0;
    /** 현재 블럭 **/
    private int pageBlock = 0;
    /** 한페이당 보여질 로우 **/
    private int rowPerPage = 10;
    /** 한블럭당 보여질 페이지 수 **/
    private int pagePerBlock = 10;
    /** 총 로우수 **/
    private long totalRow = 0;
    /** 가상 번호 **/
    private long virtualNum = 0;
    /** 총 페이지 수 **/
    private int totalPage = 0;
    /** 총 블럭 수 **/
    private int totalBlock = 0;
    /** 현재 블럭에서 시작 페이지 **/
    private int startPage = 0;
    /** 현재 블럭에서 끝 페이지 **/
    private int endPage = 0;
    /** 이전 블럭 여부 **/
    private int isPrevBlock = 0;
    /** 다음 블럭 여부 **/
    private int isNextBlock = 0;
    
    public PageInfo() { }

    public PageInfo(int page)	{
        this(10,10, page);
    }

    public PageInfo(int rowPerPage ,int page) {
        this(10, rowPerPage, page);
    }

    public PageInfo(int pagePerBlock, int rowPerPage ,int page)	{
        this.pagePerBlock = pagePerBlock;
        this.rowPerPage = rowPerPage;
        this.page = page;
    }

    /**
     * A Method for committing the changes on pagination information
     * @param totalRow
     */
    public void commit(long totalRow) {
        this.totalRow = totalRow;
        this.totalPage = (int)( totalRow / (long)rowPerPage );
        this.totalPage += ( (totalRow % rowPerPage) != 0 ) ? 1 : 0;
        this.page = ( page > totalPage )	? 1 : page;
        this.page = ( page < 1 )			? 1 : page;
        int na		= this.page % pagePerBlock;
        int hae		= this.page / pagePerBlock;
        this.pageBlock	= ( na != 0 ) ? hae + 1 : hae;
        this.virtualNum = this.totalRow - ( rowPerPage * (this.page - 1) );
        this.totalBlock	= totalPage / pagePerBlock;
        this.totalBlock += ( (totalPage % pagePerBlock) != 0 ) ? 1: 0;
        this.startPage = (pagePerBlock * (pageBlock - 1)) + 1;
        this.endPage = ( pageBlock == totalBlock ) ? totalPage : ( pagePerBlock * pageBlock );
        if( totalBlock == 0 ) this.endPage = 0;
        this.isPrevBlock = ( this.pageBlock == 1 ) ? 0 : 1;
        this.isNextBlock = ( this.pageBlock != this.totalBlock && this.totalBlock != 0 ) ? 1 : 0;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
        commit(this.totalRow);
    }

    public int getPageBlock() {
        return pageBlock;
    }

    public void setPageBlock(int pageBlock) {
        this.pageBlock = pageBlock;
        commit(this.totalRow);
    }

    public int getRowPerPage() {
        return rowPerPage;
    }

    public void setRowPerPage(int rowPerPage) {
        this.rowPerPage = rowPerPage;
        commit(this.totalRow);
    }

    public int getPagePerBlock() {
        return pagePerBlock;
    }

    public void setPagePerBlock(int pagePerBlock) {
        this.pagePerBlock = pagePerBlock;
        commit(this.totalRow);
    }

    public long getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(long totalRow) {
        this.totalRow = totalRow;
        commit(this.totalRow);
    }

    public long getVirtualNum() {
        return virtualNum;
    }

    public void setVirtualNum(long virtualNum) {
        this.virtualNum = virtualNum;
        commit(this.totalRow);
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        commit(this.totalRow);
    }

    public int getTotalBlock() {
        return totalBlock;
    }

    public void setTotalBlock(int totalBlock) {
        this.totalBlock = totalBlock;
        commit(this.totalRow);
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
        commit(this.totalRow);
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
        commit(this.totalRow);
    }

    public int getIsPrevBlock() {
        return isPrevBlock;
    }

    public void setIsPrevBlock(int isPrevBlock) {
        this.isPrevBlock = isPrevBlock;
        commit(this.totalRow);
    }

    public int getIsNextBlock() {
        return isNextBlock;
    }

    public void setIsNextBlock(int isNextBlock) {
        this.isNextBlock = isNextBlock;
        commit(this.totalRow);
    }
}
