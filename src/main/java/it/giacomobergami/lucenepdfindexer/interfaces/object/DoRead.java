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
package it.giacomobergami.lucenepdfindexer.interfaces.object;

/**
 * Allows to read an object that has been opened for this purpose. Any opened object could be closed.
 */
public interface DoRead<T> extends Opened<T> {

    public T readObject();
    public boolean hasNext();
    public void rewind();
    public int getPos();
    public int getSize();
    public Iterable<T> asIterable();

}
