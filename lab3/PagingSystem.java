public class PagingSystem {
    public static void main(String[] args) {
        PageTable pageTable = new PageTable(5);
        for (int i = 1; i <= 5; i++) {
            pageTable.addPage(i);
        }


        MMU mmu = new MMU(pageTable, 7);




        int[] accessPattern = {1, 2, 3, 4, 2, 5, 3, 1, 4, 2, 5, 1};


        System.out.println("Simulating access with NRU algorithm:");
        for (int pageId : accessPattern) {
            mmu.accessPage(pageId, ReplacementAlgorithm.NRU);
        }
        mmu.report();


        System.out.println("\nSimulating access with Random Replacement algorithm:");
        MMU mmuRandom = new MMU(pageTable, 7);
        for (int pageId : accessPattern) {
            mmuRandom.accessPage(pageId, ReplacementAlgorithm.RANDOM);
        }
        mmuRandom.report();
    }
}





