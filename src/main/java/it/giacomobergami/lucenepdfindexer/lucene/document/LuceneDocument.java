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
import org.apache.lucene.document.TextField;

import java.lang.reflect.Field;

/**
 * Abstraction over any class that textends this class. It uses reflection in order to read
 */
public class LuceneDocument {

    public Document asDocument() {
        Document toret = new Document();
        for (Field f : getClass().getDeclaredFields()) {        //for each field
            f.setAccessible(true);
            String label = f.getName();
            try {
                //Creates a field like the attirbute name, and uses the values for the stored value
                toret.add(new TextField(label, f.get(this).toString(), org.apache.lucene.document.Field.Store.YES));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return toret;
    }


}
