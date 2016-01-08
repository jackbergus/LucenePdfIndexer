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
import it.giacomobergami.lucenepdfindexer.interfaces.object.ReadWriteDocument;
import it.giacomobergami.lucenepdfindexer.lucene.document.LucenePaper;

import java.io.File;
import java.io.IOException;

/**
 * Using automatic close (since Java7).
 * Implements the state machine: all the transactions are closed by the ReadWriteDocument interface.
 * This class implements the check of the transactions that have to be perform in order to execute the correct action
 */
public class LuceneStateMachine implements AutoCloseable {

    private ReadWriteDocument<LucenePaper> state;

    public LuceneStateMachine(File file) {
        this.state = new ClosedLuceneIndex(file);
    }

    /**
     * Closes the index through the state machine.
     */
    public ErrOptional<Boolean> stateClose() {
        ErrOptional<Closed<LucenePaper>> elem = ErrOptional.raiseMessageError("Error: unmatched case in closing element");

        if (state instanceof Closed)
            return ErrOptional.of(true);
        else if (state instanceof DoRead)
            elem = ((LuceneRead) state).close();
        else if (state instanceof DoWrite)
            elem = ((LuceneWrite) state).close();

        if (elem.hasValue()) {
            state = elem.get();
            return ErrOptional.of(true);
        } else {
            return elem.doCast();
        }
    }

    private boolean isOk(ErrOptional<Boolean> elem) {
        if (elem==null)
            return false;
        return elem.hasValue() ? elem.get() : false;
    }

    private ErrOptional<Boolean> prepareRead() {
        if (!(state instanceof DoRead)) {
            ErrOptional<Boolean> l = stateClose();
            //returns the error if not correct execution
            if (!l.hasValue()) return l.doCast();
            if (l.get()==false) return ErrOptional.raiseMessageError("Error while closing the element");

            ErrOptional<DoRead<LucenePaper>> result = ((ClosedLuceneIndex) state).openRead();
            if (result.hasValue())
                state = result.get();
            else
                return result.doCast();
        }
        return ErrOptional.of(true);
    }

    private ErrOptional<Boolean> prepareWrite() {
        if (!(state instanceof DoWrite)) {
            ErrOptional<Boolean> l = stateClose();
            //returns the error if not correct execution
            if (!l.hasValue()) return l.doCast();
            if (l.get()==false) return ErrOptional.raiseMessageError("Error while closing the element");

            ErrOptional<DoWrite<LucenePaper>> result = ((ClosedLuceneIndex) state).openWrite();
            if (result.hasValue())
                state = result.get();
            else
                return result.doCast();
        }
        return ErrOptional.of(true);
    }

    public ErrOptional<LucenePaper> readObject() {
        ErrOptional<Boolean> l = prepareRead();
        if (!isOk(l)) {
            if (l.hasValue())
                return ErrOptional.raiseMessageError("Error while closing the element");
            else
                return l.doCast();
        }
        LuceneRead lr = (LuceneRead)state;
        LucenePaper toret = lr.readObject();

        return toret == null? ErrOptional.<LucenePaper>raiseMessageError("Null Document returned") : ErrOptional.of(toret);
    }

    public ErrOptional<Boolean> hasNext() {
        ErrOptional<Boolean> l = prepareRead();
        if (!isOk(l)) {
            if (l.hasValue())
                return ErrOptional.raiseMessageError("Error while closing the element");
            else
                return l.doCast();
        }
        LuceneRead lr = (LuceneRead)state;
        return ErrOptional.of(lr.hasNext());
    }

    public boolean rewind() {
        ErrOptional<Boolean> l = prepareRead();
        if (!isOk(l))
            return false;
        LuceneRead lr = (LuceneRead)state;
        lr.rewind();
        return true;
    }

    public ErrOptional<Integer> getPos() {
        ErrOptional<Boolean> l = prepareRead();
        if (!isOk(l)) {
            if (l.hasValue())
                return ErrOptional.raiseMessageError("Error while closing the element");
            else
                return l.doCast();
        }
        LuceneRead lr = (LuceneRead)state;
        int val = lr.getPos();
        return ErrOptional.of(val);
    }

    public ErrOptional<Integer> getSize() {
        ErrOptional<Boolean> l = prepareRead();
        if (!isOk(l)) {
            if (l.hasValue())
                return ErrOptional.raiseMessageError("Error while closing the element");
            else
                return l.doCast();
        }
        LuceneRead lr = (LuceneRead)state;
        int val = lr.getSize();
        return ErrOptional.of(val);
    }

    public ErrOptional<Iterable<LucenePaper>> asIterable() {
        ErrOptional<Boolean> l = prepareRead();
        if (!isOk(l)) {
            if (l.hasValue())
                return ErrOptional.raiseMessageError("Error while closing the element");
            else
                return l.doCast();
        }
        LuceneRead lr = (LuceneRead)state;
        return ErrOptional.of(lr.asIterable());
    }

    public ErrOptional<Boolean> write(LucenePaper lp) {
        ErrOptional<Boolean> l = prepareWrite();
        if (!isOk(l)) {
            if (l.hasValue())
                return ErrOptional.raiseMessageError("Error while closing the element");
            else
                return l.doCast();
        }
        LuceneWrite lw = (LuceneWrite)state;
        try {
            lw.writeObject(lp);
            return ErrOptional.of(true);
        } catch (IOException e) {
            return ErrOptional.raiseError(e);
        }
    }


    /**
     * Automatically closes the object
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        ErrOptional<Boolean> cs = stateClose();
        if (cs.isError()) {
            if (cs.isThrowable())
                cs.getError().printStackTrace();
            else if (cs.isMessage())
                throw new RuntimeException(cs.getMessage());
            throw new RuntimeException("HALT");
        }
    }
}
