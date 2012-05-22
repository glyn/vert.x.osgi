/*
 * Copyright 2012 the original author or authors.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vertx.osgi.sample.basic;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

import org.vertx.java.core.impl.Context;

public final class HttpServerRequestHandler implements Handler<HttpServerRequest> {

    private abstract class PathUnimpl implements Path {

        @Override
        public FileSystem getFileSystem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAbsolute() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getRoot() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getFileName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getParent() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getNameCount() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getName(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path subpath(int beginIndex, int endIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean startsWith(Path other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean startsWith(String other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean endsWith(Path other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean endsWith(String other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path normalize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path resolve(Path other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path resolve(String other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path resolveSibling(Path other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path resolveSibling(String other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path relativize(Path other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public URI toUri() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path toAbsolutePath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path toRealPath(LinkOption... options) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public File toFile() {
            throw new UnsupportedOperationException();
        }

        @Override
        public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<Path> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(Path other) {
            throw new UnsupportedOperationException();
        }
    }

    private final class ResolvingPath extends PathUnimpl {

        @Override
        public Path resolve(Path other) {
            URL resource = this.getClass().getClassLoader().getResource(other.toString());
            try {
                URI uri = resource.toURI();
                return Paths.get(uri);
            } catch (URISyntaxException e) {
            }
            return null;
        }

    }

    public void handle(HttpServerRequest req) {
        setPathAdjuster();
        String file = req.path.equals("/") ? "index.html" : req.path;
        req.response.sendFile("webroot/" + file);
    }

    private void setPathAdjuster() {
        Context vertxContext = Context.getContext();
        if (vertxContext.getPathAdjustment() == null) {
            vertxContext.setPathAdjustment(new ResolvingPath());
        }
    }
}