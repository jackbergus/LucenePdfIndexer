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
import it.giacomobergami.lucenepdfindexer.lucene.document.LucenePaper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Iterator;

/**
 * Implements the DoRead interface. A document in Lucene could not be read and written at the same time
 */
public class LuceneRead implements DoRead<LucenePaper> {

    private File directory;
    private IndexSearcher indexSearcher;
    private IndexReader reader;
    private int pos;

    public LuceneRead(File directory, IndexSearcher indexSearcher) {
        this.directory = directory;
        this.indexSearcher = indexSearcher;
        if (indexSearcher!=null) {
            reader = indexSearcher.getIndexReader();
        }
        this.pos = 0;
    }

    /**
     * Reads the object at the current position
     * @return
     */
    @Override
    public LucenePaper readObject() {
        if (pos>=reader.maxDoc())
            return null;
            Document doc = null;
            try {
                doc = reader.document(pos);
                pos++;
                return doc != null ? new LucenePaper(doc) : null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
    }

    @Override
    public boolean hasNext() {
        return (pos<reader.maxDoc());
    }

    @Override
    public void rewind() {
        pos = 0;
    }

    @Override
    public int getPos() {
        return pos;
    }

    @Override
    public int getSize() {
        return reader.maxDoc();
    }

    @Override
    public Iterable<LucenePaper> asIterable() {
        return new Iterable<LucenePaper>() {
            public Iterator<LucenePaper> iterator() {
                return new Iterator<LucenePaper>() {

                    private int localPos = 0;

                    public boolean hasNext() {
                        return (localPos<reader.maxDoc());
                    }

                    public LucenePaper next() {
                        Document doc = null;
                        try {
                            doc = reader.document(localPos);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                        LucenePaper toret =  doc != null ? new LucenePaper(doc) : null;
                        localPos++;
                        return toret;
                    }
                };
            }
        };
    }

    /**
     * Closes the reading session
     * @return
     */
    @Override
    public ErrOptional<Closed<LucenePaper>> close() {
        try {
            reader.close();
            Closed<LucenePaper> cd = new ClosedLuceneIndex(directory);
            return ErrOptional.of(cd);
        } catch (IOException e) {
            return ErrOptional.raiseError(e);
        }
    }

}
