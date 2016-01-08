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
package it.giacomobergami.lucenepdfindexer;

import it.giacomobergami.lucenepdfindexer.lucene.LuceneStateMachine;
import it.giacomobergami.lucenepdfindexer.lucene.document.LucenePaper;

import java.io.File;


public class TestLuceneStateMachineClass {

    //Gets the value that has been stored. If it has an error, it throws a RunTimeException
    public static <T> T get(ErrOptional<T> e)  {
        if (e.isError()) {
            e.getError().printStackTrace();
            throw new RuntimeException("HALT");
        } else if (e.isMessage()) {
            throw new RuntimeException(e.getMessage());
        }
        return e.get();
    }

    public static void main(String args[]) {
        //Ok: if there are no documents, there are no documents added yet
        //In Java7, this statement ensures that the object will be closed at the end of the road
        try (LuceneStateMachine state = new LuceneStateMachine(new File("/Users/vasistas/luceneindexstate"))) {
            if (state.getSize().isError()) {
                //add some papers
                state.write(new LucenePaper("this is some long text","key1"));
                state.write(new LucenePaper("this is some short text","key2"));
                System.out.println("Added some papers and closed");
            } else {
                //performing some read operations
                int size = get(state.getSize());
                System.out.println("Has size");
                System.out.println("size: "+size);

                Iterable<LucenePaper> it = get(state.asIterable());
                for (LucenePaper p : it) {
                    System.out.println(p.text +" -- " + p.bibtexkey);
                }

                //performing a write operation
                size++;
                state.write(new LucenePaper("This is the new paper, number "+size,"key"+size));
                System.out.println("Added some new paper and closed");


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Closing the object automatically: Java7
    }

}
