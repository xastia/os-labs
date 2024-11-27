import java.util.ArrayList;
import java.util.List;


public class Process {
    int id;
    List<Page> workingSet;


    public Process(int id, int[] pageIds) {
        this.id = id;
        this.workingSet = new ArrayList<>();
        for (int pageId : pageIds) {
            this.workingSet.add(new Page(pageId));
        }
    }


    public List<Page> getWorkingSet() {
        return workingSet;
    }
}









