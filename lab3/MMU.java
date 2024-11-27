import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MMU {
    private final PageTable pageTable;
    private final List<PhysicalPage> physicalMemory;
    private int pageFaults;
    private final StringBuilder report;
    private final Random random;
    private final List<Process> processes;


    public MMU(PageTable pageTable, int memorySize) {
        this.pageTable = pageTable;
        this.physicalMemory = new ArrayList<>(memorySize);
        for (int i = 0; i < memorySize; i++) {
            physicalMemory.add(new PhysicalPage());
        }
        this.pageFaults = 0;
        this.report = new StringBuilder();
        this.random = new Random();
        this.processes = new ArrayList<>();
    }


   
    public void createProcess(int processId, int[] pageIds) {
        Process newProcess = new Process(processId, pageIds);
        processes.add(newProcess);
        for (Page page : newProcess.getWorkingSet()) {
            pageTable.addPage(page.id);
        }
        report.append("Process ").append(processId).append(" created.\n");
    }


 
    public void terminateProcess(int processId) {
        Process processToRemove = processes.stream()
                .filter(p -> p.id == processId)
                .findFirst()
                .orElse(null);


        if (processToRemove != null) {
            processes.remove(processToRemove);
            for (Page page : processToRemove.getWorkingSet()) {
                pageTable.getPages().remove(page);
            }
            report.append("Process ").append(processId).append(" terminated.\n");
        } else {
            report.append("Process ").append(processId).append(" not found.\n");
        }
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
            replacePage(pageId, algorithm);  // Замінюємо сторінку
        } else {
            physicalPage.referenced = true;
            report.append("Page ").append(pageId).append(" accessed in memory. No page fault.\n");
        }
    }



    private void replacePage(int pageId, ReplacementAlgorithm algorithm) {
        int victimPageIndex = (algorithm == ReplacementAlgorithm.RANDOM) ?
            random.nextInt(physicalMemory.size()) : findNRUVictimPage();


         PhysicalPage victimPage = physicalMemory.get(victimPageIndex);
         if (victimPage.pageId == null) {  
            victimPage.pageId = pageId;
            victimPage.referenced = true;
            victimPage.modified = false;
            report.append("Page ").append(pageId)
                  .append(" loaded into frame ").append(victimPageIndex).append(".\n");
        } else {
            report.append("Replacing page ").append(victimPage.pageId)
            .append(" with page ").append(pageId)
            .append(" in frame ").append(victimPageIndex).append(".\n");
            victimPage.pageId = pageId;
            victimPage.referenced = true;
            victimPage.modified = false;
        }
    }



    private int findNRUVictimPage() {
        List<Integer> class0Pages = new ArrayList<>();
        List<Integer> class1Pages = new ArrayList<>();
        List<Integer> class2Pages = new ArrayList<>();
        List<Integer> class3Pages = new ArrayList<>();


 
        for (int i = 0; i < physicalMemory.size(); i++) {
            PhysicalPage page = physicalMemory.get(i);
            if (!page.referenced && !page.modified) {
                class0Pages.add(i); // R=0, M=0
            } else if (page.referenced && !page.modified) {
                class1Pages.add(i); // R=1, M=0
            } else if (!page.referenced && page.modified) {
                class2Pages.add(i); // R=0, M=1
            } else if (page.referenced && page.modified) {
                class3Pages.add(i); // R=1, M=1
            }
        }


       
        if (!class0Pages.isEmpty()) {
            return class0Pages.get(random.nextInt(class0Pages.size()));
        } else if (!class1Pages.isEmpty()) {
            return class1Pages.get(random.nextInt(class1Pages.size()));
        } else if (!class2Pages.isEmpty()) {
            return class2Pages.get(random.nextInt(class2Pages.size()));
        } else {
            return class3Pages.get(random.nextInt(class3Pages.size()));
        }
    }


    public void report() {
        report.append("Total page faults: ").append(pageFaults).append("\n");
        for (int i = 0; i < physicalMemory.size(); i++) {
            PhysicalPage page = physicalMemory.get(i);
            if (page.pageId != null) {
                report.append("Frame ").append(i).append(": Page ID ")
                      .append(page.pageId).append(", Referenced ")
                      .append(page.referenced).append(", Modified ")
                      .append(page.modified).append("\n");
            } else {
                report.append("Frame ").append(i).append(": Empty\n");
            }
        }
    System.out.println(report.toString());
    }

}





