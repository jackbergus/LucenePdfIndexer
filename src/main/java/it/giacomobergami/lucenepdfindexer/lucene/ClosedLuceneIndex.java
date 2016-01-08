/*
    This file is part of LucenePdfIndexer.

    LucenePdfIndexer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    any later version.

    LucenePdfIndexer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with LucenePdfIndexer.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.giacomobergami.lucenepdfindexer.lucene;

import it.giacomobergami.lucenepdfindexer.ErrOptional;
import it.giacomobergami.lucenepdfindexer.interfaces.object.Closed;
import it.giacomobergami.lucenepdfindexer.interfaces.object.DoRead;
import it.giacomobergami.lucenepdfindexer.interfaces.object.DoWrite;
import it.giacomobergami.lucenepdfindexer.lucene.LuceneWrite;
import it.giacomobergami.lucenepdfindexer.lucene.document.LucenePaper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by vasistas on 07/01/16.
 */
public class ClosedLuceneIndex implements Closed<LucenePaper> {

    private final StandardAnalyzer analyzer;
    private FSDirectory index;
    private File directory;

    public ClosedLuceneIndex(File directory) {
        this.analyzer = new StandardAnalyzer();
        this.directory = directory;
        // Link the directory on the FileSystem to the application
        try {
            this.index = FSDirectory.open(directory.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            index = null;
        }
    }

    public boolean exists() {
        try {
            return DirectoryReader.indexExists(index);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Creates the Write configuration as a Lucene object
     * @return
     */
    public ErrOptional<IndexWriter> writeConfiguration() {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try {
            return ErrOptional.of(new IndexWriter(index, config));
        } catch (IOException e) {
            return ErrOptional.raiseError(e);
        }
    }

    /**
     * Generates the Read configuration as a Lucene object
     * @return
     */
    public ErrOptional<IndexSearcher> openConfiguration() {
        try {
            IndexReader ir = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(ir);
            return ErrOptional.of(searcher);
        } catch (IOException e) {
            return ErrOptional.raiseError(e);
        }
    }

    @Override
    public ErrOptional<DoRead<LucenePaper>> openRead() {
        ErrOptional<IndexSearcher> wc = openConfiguration();
        if (wc.hasValue()) {
            DoRead<LucenePaper> d = new LuceneRead(directory,wc.get());
            return ErrOptional.of(d);
        } else {
            return wc.doCast();
        }
    }

    @Override
    public ErrOptional<DoWrite<LucenePaper>> openWrite() {
        ErrOptional<IndexWriter> wc = writeConfiguration();
        if (wc.hasValue()) {
            DoWrite<LucenePaper> d = new LuceneWrite(directory,wc.get());
            return ErrOptional.of(d);
        } else {
            return wc.doCast();
        }
    }
}
