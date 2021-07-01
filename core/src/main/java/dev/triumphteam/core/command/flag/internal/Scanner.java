package dev.triumphteam.core.command.flag.internal;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class Scanner {

    private final List<String> tokens;
    private int index = -1;

    private String current = null;

    public Scanner(@NotNull final List<String> tokens) {
        this.tokens = tokens;
    }

    @NotNull
    public String peek() {
        return current;
    }

    public boolean hasNext() {
        return index < tokens.size() - 1;
    }

    public void next() {
        if (index < tokens.size()) index++;
        setNode(tokens.get(index));
    }

    public void previous() {
        if (index > 0) index--;
        setNode(tokens.get(index));
    }

    private void setNode(@NotNull final String token) {
        this.current = token;
    }

}
