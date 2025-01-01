
import java.util.Arrays;
import java.util.BitSet;

class BlockStorage {
    private final int blockSize;
    private final byte[][] blocks;
    private final BitSet allocationMap;

    public BlockStorage(int numBlocks, int blockSize) {
        this.blockSize = blockSize;
        this.blocks = new byte[numBlocks][blockSize];
        this.allocationMap = new BitSet(numBlocks);
    }

    public int allocateBlock() {
        int freeBlock = allocationMap.nextClearBit(0);
        if (freeBlock >= blocks.length) throw new IllegalStateException("No free blocks available.");
        allocationMap.set(freeBlock);
        Arrays.fill(blocks[freeBlock], (byte) 0); 
        return freeBlock;
    }

    public void freeBlock(int blockNumber) {
        allocationMap.clear(blockNumber);
        Arrays.fill(blocks[blockNumber], (byte) 0); 
    }

    public byte[] getBlock(int blockNumber) {
        return blocks[blockNumber];
    }

    public int getBlockSize() {
        return blockSize;
    }
}
