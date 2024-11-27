public class Page {
    int id;
    boolean referenced;
    boolean modified;

    public Page(int id) {
        this.id = id;
        this.referenced = false;
        this.modified = false;
    }
}




