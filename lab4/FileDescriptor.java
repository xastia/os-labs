
import java.util.ArrayList;
import java.util.List;


class FileDescriptor {
    enum FileType { REGULAR, DIRECTORY }

    FileType type;
    int size; // Розмір файлу в байтах
    int hardLinks; // Кількість жорстких посилань
    List<Integer> blockMap; // Карта блоків
    Integer singleIndirectBlock; // Для багаторівневої карти блоків

    public FileDescriptor(FileType type) {
        this.type = type;
        this.size = 0;
        this.hardLinks = 1;
        this.blockMap = new ArrayList<>();
        this.singleIndirectBlock = null;
    }
}
