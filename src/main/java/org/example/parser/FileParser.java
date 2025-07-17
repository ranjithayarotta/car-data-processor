package org.example.parser;

import java.util.List;
import java.io.File;

public interface FileParser<T> {
    List<T> parse(File file) throws Exception;
}