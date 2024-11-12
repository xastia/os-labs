public class PhysicalPage {
    Integer pageId;
    boolean referenced;
    boolean modified;

    public PhysicalPage() {
        this.pageId = null;
        this.referenced = false;
        this.modified = false;
    }
}