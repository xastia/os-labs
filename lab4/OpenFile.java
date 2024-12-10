class OpenFile {
    int descriptorIndex;
    int offset;

    OpenFile(int descriptorIndex) {
        this.descriptorIndex = descriptorIndex;
        this.offset = 0;
    }
}
