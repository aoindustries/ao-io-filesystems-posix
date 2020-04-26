/*
 * ao-io-filesystems-unix - Advanced filesystem utilities for Unix.
 * Copyright (C) 2015, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-io-filesystems-unix.
 *
 * ao-io-filesystems-unix is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-io-filesystems-unix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-io-filesystems-unix.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.io.filesystems.unix;

import com.aoindustries.io.filesystems.FileSystem;
import com.aoindustries.io.filesystems.InvalidPathException;
import static com.aoindustries.io.filesystems.JavaFileSystem.MAX_PATH_NAME_LENGTH;
import com.aoindustries.io.filesystems.Path;
import com.aoindustries.io.unix.Stat;
import com.aoindustries.io.unix.UnixFile;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

/**
 * The most basic layer of what Unix file systems have in common.
 * <p>
 * Note: The JVM must be in a single-byte locale, such as "C", "POSIX", or
 * "en_US".  UnixFile makes this assumption in its JNI implementation.
 * </p>
 * 
 * @see  UnixFile
 *
 * @author  AO Industries, Inc.
 */
public interface UnixFileSystem extends FileSystem {

	/**
	 * Unix filename restrictions are:
	 * <ol>
	 * <li>Must not be longer than <code>MAX_PATH_NAME_LENGTH</code> characters</li>
	 * <li>Must not contain the NULL character</li>
	 * <li>Must not contain the '/' character</li>
	 * <li>Must not be "."</li>
	 * <li>Must not be ".."</li>
	 * </ol>
	 */
	@Override
	default void checkSubPath(Path parent, String name) throws InvalidPathException {
		if(parent.getFileSystem() != this) throw new IllegalArgumentException();
		int nameLen = name.length();
		// Must not be longer than <code>MAX_PATH_NAME_LENGTH</code> characters
		if(nameLen > MAX_PATH_NAME_LENGTH) {
			throw new InvalidPathException("Path name must not be longer than " + MAX_PATH_NAME_LENGTH + " characters: " + name);
		}
		// Must not contain the NULL character
		if(name.indexOf(0) != -1) {
			throw new InvalidPathException("Path name must not contain the NULL character: " + name);
		}
		// Must not contain the '/' character
		assert Path.SEPARATOR == '/';
		// Must not be "."
		if(".".equals(name)) {
			throw new InvalidPathException("Path name must not be \".\": " + name);
		}
		// Must not be ".."
		if("..".equals(name)) {
			throw new InvalidPathException("Path name must not be \"..\": " + name);
		}
	}

	/**
	 * @param  path  Must be from this file system.
	 */
	Stat stat(Path path) throws IOException;

	/**
	 * Atomically creates an empty file (must not have already existed) with the
	 * given permissions.
	 * 
	 * @return  returns the path
	 * 
	 * @throws UnsupportedOperationException if unable to create atomically
	 * @throws FileAlreadyExistsException if file already exists
	 * @throws IOException if an underlying I/O error occurs.
	 */
	Path createFile(Path path, int mode) throws IOException;

	/**
	 * Atomically creates a directory (must not have already existed) with the
	 * given permissions.
	 * 
	 * @return  returns the path
	 * 
	 * @throws UnsupportedOperationException if unable to create atomically
	 * @throws FileAlreadyExistsException if file already exists
	 * @throws IOException if an underlying I/O error occurs.
	 */
	Path createDirectory(Path path, int mode) throws IOException;
}