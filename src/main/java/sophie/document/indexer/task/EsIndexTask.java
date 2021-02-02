package sophie.document.indexer.task;

import sophie.document.indexer.contract.Document;

public class EsIndexTask {
    private String id;
    private Document document;

    public EsIndexTask(String id, Document document) {
        this.id = id;
        this.document = document;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return "EsIndexTask{" +
                "id='" + id + '\'' +
                ", document=" + document +
                '}';
    }
}
