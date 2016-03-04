package org.tiestvilee.deft.grammar;

import java.util.List;

import static java.util.Arrays.asList;

public class Attribute extends Node {
    public final String tagName;
    public final Node value;

    public Attribute(String tagName, Node value) {
        this.tagName = tagName;
        this.value = value;
    }
}
