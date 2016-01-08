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
package it.giacomobergami.lucenepdfindexer.lucene.document;

import org.apache.lucene.document.Document;

/**
 * Maps a paper into a Lucene Document and vice versa
 */
public class LucenePaper extends LuceneDocument {

    public String text;
    public String bibtexkey;

    public LucenePaper(String text, String bibtexkey) {
        this.text = text;
        this.bibtexkey = bibtexkey;
    }

    public LucenePaper(Document doc) {
        if (doc == null) {
            text = bibtexkey = null;
        } else {
            this.text = doc.get("text");
            this.bibtexkey = doc.get("bibtexkey");
        }
    }

    public static void main(String[] args) {
        LucenePaper lp = new LucenePaper("questo è un testo lungo","key");
        Document doc = lp.asDocument();
        System.out.println(new LucenePaper(doc).bibtexkey);
    }

}
