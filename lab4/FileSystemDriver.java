import java.util.*;

class FileSystemDriver {
    private final List<FileDescriptor> descriptors;
    private final BlockStorage storage;
    private final Directory rootDirectory;
    private final Map<Integer, OpenFile> openFileTable;

    public FileSystemDriver(int maxDescriptors, int numBlocks, int blockSize) {
        this.descriptors = new ArrayList<>(Collections.nCopies(maxDescriptors, null));
        this.storage = new BlockStorage(numBlocks, blockSize);
        this.rootDirectory = new Directory();
        this.openFileTable = new HashMap<>();
    }

    public void mkfs() {
        descriptors.set(0, new FileDescriptor(FileDescriptor.FileType.DIRECTORY));
        System.out.println("Filesystem initialized.");
    }

    public void create(String name) {
        int freeIndex = descriptors.indexOf(null);
        if (freeIndex == -1) throw new IllegalStateException("No free descriptors available.");
        descriptors.set(freeIndex, new FileDescriptor(FileDescriptor.FileType.REGULAR));
        rootDirectory.addEntry(name, freeIndex);
        System.out.println("File \"" + name + "\" created.");
    }

    public void truncate(String name, int size) {
        Integer descriptorIndex = rootDirectory.getEntry(name);
        if (descriptorIndex == null) throw new IllegalArgumentException("File not found.");
        FileDescriptor descriptor = descriptors.get(descriptorIndex);

        int currentBlocks = (int) Math.ceil((double) descriptor.size / storage.getBlockSize());
        int requiredBlocks = (int) Math.ceil((double) size / storage.getBlockSize());

        if (requiredBlocks < currentBlocks) {
            for (int i = requiredBlocks; i < descriptor.blockMap.size(); i++) {
                storage.freeBlock(descriptor.blockMap.get(i));
            }
            descriptor.blockMap = descriptor.blockMap.subList(0, requiredBlocks);
        } else if (requiredBlocks > currentBlocks) {
            for (int i = currentBlocks; i < requiredBlocks; i++) {
                descriptor.blockMap.add(storage.allocateBlock());
            }
        }

        descriptor.size = size;
        System.out.println("File \"" + name + "\" truncated to size " + size + " bytes.");
    }

    public void stat(String name) {
        Integer descriptorIndex = rootDirectory.getEntry(name);
        if (descriptorIndex == null) throw new IllegalArgumentException("File not found.");
        FileDescriptor descriptor = descriptors.get(descriptorIndex);
    
        System.out.println("File: " + name);
        System.out.println("Type: " + descriptor.type);
        System.out.println("Size: " + descriptor.size + " bytes");
        System.out.println("Hard Links: " + descriptor.hardLinks);
        System.out.println("Blocks: " + descriptor.blockMap.size());
    }
    

    public void ls() {
        rootDirectory.listEntries().forEach((name, descriptorIndex) -> {
            System.out.println(name + " -> Descriptor " + descriptorIndex);
        });
    }

    public int open(String name) {
        Integer descriptorIndex = rootDirectory.getEntry(name);
        if (descriptorIndex == null) throw new IllegalArgumentException("File not found.");
        int fd = openFileTable.size();
        openFileTable.put(fd, new OpenFile(descriptorIndex));
        System.out.println("File \"" + name + "\" opened with descriptor " + fd);
        return fd;
    }

    public void close(int fd) {
        if (!openFileTable.containsKey(fd)) throw new IllegalArgumentException("Invalid file descriptor.");
        openFileTable.remove(fd);
        System.out.println("File descriptor " + fd + " closed.");
    }

    public void seek(int fd, int offset) {
        OpenFile openFile = openFileTable.get(fd);
        if (openFile == null) throw new IllegalArgumentException("Invalid file descriptor.");
        FileDescriptor descriptor = descriptors.get(openFile.descriptorIndex);
        if (offset < 0 || offset > descriptor.size) {
            throw new IllegalArgumentException("Offset out of bounds.");
        }
        openFile.offset = offset;
        System.out.println("File descriptor " + fd + " offset set to " + offset);
    }

    public void write(int fd, int size) {
        OpenFile openFile = openFileTable.get(fd);
        if (openFile == null) throw new IllegalArgumentException("Invalid file descriptor.");
        FileDescriptor descriptor = descriptors.get(openFile.descriptorIndex);
    
        byte[] dataToWrite = new byte[size];
        Arrays.fill(dataToWrite, (byte) 'A'); // Наприклад, записуємо символ 'A' (можна змінити)
    
        int remaining = size;
        int offsetInBlock = openFile.offset % storage.getBlockSize();
    
        while (remaining > 0) {
            int blockIndex = openFile.offset / storage.getBlockSize();
            if (blockIndex >= descriptor.blockMap.size()) {
                descriptor.blockMap.add(storage.allocateBlock());
            }
            int blockNumber = descriptor.blockMap.get(blockIndex);
    
            byte[] block = storage.getBlock(blockNumber);
            int writeSize = Math.min(remaining, storage.getBlockSize() - offsetInBlock);
    
            System.arraycopy(dataToWrite, size - remaining, block, offsetInBlock, writeSize);
    
            remaining -= writeSize;
            openFile.offset += writeSize;
            offsetInBlock = 0; 
        }
    
        descriptor.size = Math.max(descriptor.size, openFile.offset);
        System.out.println(size + " bytes written to file descriptor " + fd);
    }
    

    public void read(int fd, int size) {
        OpenFile openFile = openFileTable.get(fd);
        if (openFile == null) throw new IllegalArgumentException("Invalid file descriptor.");
        FileDescriptor descriptor = descriptors.get(openFile.descriptorIndex);
    
        if (openFile.offset + size > descriptor.size) {
            throw new IllegalArgumentException("Read exceeds file size.");
        }
    
        byte[] buffer = new byte[size];
        int remaining = size;
        int offsetInBlock = openFile.offset % storage.getBlockSize();
    
        while (remaining > 0) {
            int blockIndex = openFile.offset / storage.getBlockSize();
            int blockNumber = descriptor.blockMap.get(blockIndex);
    
            byte[] block = storage.getBlock(blockNumber);
            int readSize = Math.min(remaining, storage.getBlockSize() - offsetInBlock);
    
            System.arraycopy(block, offsetInBlock, buffer, size - remaining, readSize);
    
            remaining -= readSize;
            openFile.offset += readSize;
            offsetInBlock = 0; 
        }
    
        System.out.println("Read " + size + " bytes from file descriptor " + fd + " starting from offset " + (openFile.offset - size));
        System.out.println("Data: " + new String(buffer));
    }
    

    public void link(String name1, String name2) {
        Integer descriptorIndex = rootDirectory.getEntry(name1);
        if (descriptorIndex == null) throw new IllegalArgumentException("File not found.");
        rootDirectory.addEntry(name2, descriptorIndex);
        descriptors.get(descriptorIndex).hardLinks++;
        System.out.println("Hard link created: \"" + name2 + "\" -> \"" + name1 + "\"");
    }

    public void unlink(String name) {
        Integer descriptorIndex = rootDirectory.getEntry(name);
        if (descriptorIndex == null) throw new IllegalArgumentException("File not found.");
        rootDirectory.removeEntry(name);
        FileDescriptor descriptor = descriptors.get(descriptorIndex);
        descriptor.hardLinks--;
        if (descriptor.hardLinks == 0 && !openFileTable.containsValue(descriptorIndex)) {
            descriptor.blockMap.forEach(storage::freeBlock);
            descriptors.set(descriptorIndex, null);
        }
        System.out.println("File \"" + name + "\" unlinked.");
    }
}
