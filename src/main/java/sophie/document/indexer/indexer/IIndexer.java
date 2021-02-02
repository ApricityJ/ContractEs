package sophie.document.indexer.indexer;

import sophie.document.indexer.task.EsIndexTask;

import java.io.IOException;

public interface IIndexer {

    void createIndex(String indexName, String mappings, int numOfShards, int numOfReplicas) throws IOException;
    void deleteIndex(String indexName) throws IOException;
    void insertDoc(String indexName, EsIndexTask task) throws IOException;
    void insertDocBulk();
    void deleteDoc(String indexName, String id) throws IOException;
    void deleteDocAll(String indexName) throws IOException;
    boolean isExistIndex(String indexName) throws IOException;
    void close() throws IOException;
}
