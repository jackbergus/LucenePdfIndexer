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

import it.giacomobergami.lucenepdfindexer.ErrOptional;

import java.util.Optional;

/**
 * This is the default state when an object is opened. It is neither ready to be read, nor ready to be written.
 * In order to perform a read or a write action, I have first to perform an open action
 */
public interface Closed<T> extends ReadWriteDocument<T> {

    public ErrOptional<DoRead<T>> openRead();
    public ErrOptional<DoWrite<T>> openWrite();

}
