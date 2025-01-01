
import java.util.ArrayList;
import java.util.List;

class FileDescriptor {
    enum FileType { REGULAR, DIRECTORY }

    FileType type;
    int size; 
    final Directory directory;
    int hardLinks; 
    List<Integer> blockMap; 
    Integer singleIndirectBlock; 

    public FileDescriptor(FileType type) {
        this.type = type;
        this.size = 0;
        this.hardLinks = 1;
        this.directory = null;
        this.blockMap = new ArrayList<>();
        this.singleIndirectBlock = null;
    }

    public FileDescriptor(FileType type, Directory directory) {
        this.type = type;
        this.size = 0;
        this.hardLinks = 1;
        this.directory = directory;
        this.blockMap = new ArrayList<>();
        this.singleIndirectBlock = null;
    }
}
