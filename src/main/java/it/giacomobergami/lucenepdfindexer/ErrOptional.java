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

import java.util.Optional;

/**
 * Returns a value. If there is no value, then either a Throwable or a message is returned
 */
public class ErrOptional<T>  {

    private Optional<T> opt;
    private Throwable t;
    private Message mgs;
    private boolean hasThrowable;
    private boolean hasMessage;

    private ErrOptional(T value) {
        opt = Optional.of(value);
        t = null;
        mgs = null;
        hasThrowable = false;
        hasMessage = false;
    }

    private ErrOptional(Throwable t) {
        opt = Optional.empty();
        this.t = t;
        mgs = null;
        hasThrowable = true;
        hasMessage = false;
    }

    private ErrOptional(Message errorMessageCause) {
        opt = Optional.empty();
        this.t = null;

    }

    public static <T> ErrOptional<T> of(T elem) { return new ErrOptional<T>(elem); }

    public static <T> ErrOptional<T> raiseError(Throwable t) {
        return new ErrOptional<T>(t);
    }

    public static <T> ErrOptional<T> raiseMessageError(String msg) {
        return new ErrOptional<T>(new Message(msg));
    }

    public boolean isError() { return hasMessage || hasThrowable; }

    public Throwable getError() {
        return t;
    }

    public boolean hasValue() { return opt.isPresent(); }

    public boolean isMessage() {
        return hasMessage;
    }

    public String getMessage() {
        return mgs.message;
    }

    public boolean isThrowable() {
        return hasThrowable;
    }

    public Optional<T> asOptional() {
        return opt;
    }

    public T get() { return opt.get(); }

    /**
     * Casts the ErrOptional to another class (usually then an error happens, and hence the cast is trivial)
     * @param <K>
     * @return
     */
    public <K> ErrOptional<K> doCast() {
        if (opt.isPresent()) {
            K elem = (K)opt.get();
            return new ErrOptional<K>(elem);
        } else if (hasMessage) {
            return new ErrOptional<K>(mgs);
        } else if (hasThrowable) {
            return new ErrOptional<K>(t);
        } else return new ErrOptional<K>(new Message("Error: no value"));
    }

    public static class Message {
        public String message;
        public Message(String msg) {
            this.message = msg;
        }
    }

}
