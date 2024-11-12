
import java.util.ArrayList;
import java.util.List;


public class PageTable {
    private List<Page> pages;

    public PageTable(int size) {
        pages = new ArrayList<>(size);
    }

    public void addPage(int pageId) {
        pages.add(new Page(pageId));
    }

    public Page getPage(int pageId) {
        return pages.stream().filter(p -> p.id == pageId).findFirst().orElse(null);
    }

    public List<Page> getPages() {
        return pages;
    }
}
