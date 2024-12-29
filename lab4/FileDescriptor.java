
import java.util.ArrayList;
import java.util.List;

class FileDescriptor {
    enum FileType { REGULAR, DIRECTORY }

    FileType type;
    int size; 
    int hardLinks; 
    List<Integer> blockMap; 
    Integer singleIndirectBlock; 

    public FileDescriptor(FileType type) {
        this.type = type;
        this.size = 0;
        this.hardLinks = 1;
        this.blockMap = new ArrayList<>();
        this.singleIndirectBlock = null;
    }
}
