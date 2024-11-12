
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MMU {
    private final PageTable pageTable;
    private final List<PhysicalPage> physicalMemory;
    private int pageFaults;
    private final StringBuilder report;
    private final Random random;

    public MMU(PageTable pageTable, int memorySize) {
        this.pageTable = pageTable;
        this.physicalMemory = new ArrayList<>(memorySize);
        for (int i = 0; i < memorySize; i++) {
            physicalMemory.add(new PhysicalPage());
        }
        this.pageFaults = 0;
        this.report = new StringBuilder();
        this.random = new Random();
    }

    public void accessPage(int pageId, ReplacementAlgorithm algorithm) {
    Page page = pageTable.getPage(pageId);
    if (page == null) {
        System.out.println("Page " + pageId + " is not in the page table.");
        return;
    }

    PhysicalPage physicalPage = physicalMemory.stream()
            .filter(p -> p.pageId != null && pageId == p.pageId) 
            .findFirst()
            .orElse(null);

    if (physicalPage == null) {
        pageFaults++;
        report.append("Page fault occurred for page ").append(pageId).append(".\n");
        replacePage(pageId, algorithm);
    } else {
        physicalPage.referenced = true;
        report.append("Page ").append(pageId).append(" accessed in memory. No page fault.\n");
    }
}


    private void replacePage(int pageId, ReplacementAlgorithm algorithm) {
        int victimPageIndex = (algorithm == ReplacementAlgorithm.RANDOM) ? 
                              random.nextInt(physicalMemory.size()) : findNRUVictimPage();

        PhysicalPage victimPage = physicalMemory.get(victimPageIndex);
        report.append("Replacing page ").append(victimPage.pageId)
              .append(" with page ").append(pageId)
              .append(" in frame ").append(victimPageIndex).append(".\n");

        victimPage.pageId = pageId;
        victimPage.referenced = true;
        victimPage.modified = false;
    }

    private int findNRUVictimPage() {
        List<Integer> unreferencedIndexes = new ArrayList<>();
        for (int i = 0; i < physicalMemory.size(); i++) {
            if (!physicalMemory.get(i).referenced) {
                unreferencedIndexes.add(i);
            }
        }

        if (!unreferencedIndexes.isEmpty()) {
            return unreferencedIndexes.get(random.nextInt(unreferencedIndexes.size()));
        } else {
            physicalMemory.forEach(page -> page.referenced = false);
            return random.nextInt(physicalMemory.size());
        }
    }

    public void report() {
        report.append("Total page faults: ").append(pageFaults).append("\n");
        for (int i = 0; i < physicalMemory.size(); i++) {
            PhysicalPage page = physicalMemory.get(i);
            report.append("Frame ").append(i).append(": Page ID ")
                  .append(page.pageId).append(", Referenced ")
                  .append(page.referenced).append(", Modified ")
                  .append(page.modified).append("\n");
        }
        System.out.println(report.toString());
    }
}