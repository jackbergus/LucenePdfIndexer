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
import it.giacomobergami.lucenepdfindexer.interfaces.object.DoReadWrite;
import it.giacomobergami.lucenepdfindexer.interfaces.object.DoWrite;
import it.giacomobergami.lucenepdfindexer.lucene.document.LucenePaper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Optional;

/**
 * Perform the write operations over the Lucene index
 */
public class LuceneWrite implements DoWrite<LucenePaper> {

    private IndexWriter w;
    private File fd;

    public LuceneWrite(File directory, IndexWriter wc) {
        this.w = wc;
        this.fd = directory;
    }

    public int writeObject(LucenePaper obj) throws IOException {
            w.addDocument(obj.asDocument());
            return 1;
    }

    public ErrOptional<Closed<LucenePaper>> close() {
        try {
            w.commit();
            w.close();
            Closed<LucenePaper> cd = new ClosedLuceneIndex(fd);
            return ErrOptional.of(cd);
        } catch (IOException e) {
            return ErrOptional.raiseError(e);
        }
    }
}
