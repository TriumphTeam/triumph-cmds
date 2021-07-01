package dev.triumphteam.core.command.flag.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public final class FlagParser<S> {

    private final List<String> leftOver = new LinkedList<>();
    private final FlagsResult result = new FlagsResult();

    private final FlagGroup<S> flagGroup;
    private final S sender;
    private final Scanner scanner;
    private boolean skip = false;
    private boolean fail = false;

    public FlagParser(@NotNull final FlagGroup<S> flagGroup, @NotNull final S sender, @NotNull final List<String> args) {
        this.flagGroup = flagGroup;
        this.sender = sender;
        this.scanner = new Scanner(args);
        System.out.println(args);
    }

    public void parse() {
        while (scanner.hasNext()) {
            if (fail) break;

            scanner.next();
            final String token = scanner.peek();

            if (!skip && token.startsWith("--") && !"--".equals(token)) {
                handleLongFlag(token);
                continue;
            }

            if (!skip && token.startsWith("-") && !"-".equals(token)) {
                // TODO handle short
                //System.out.println("found short");
                continue;
            }

            leftOver.add(token);
        }

        System.out.println("Failed: " + fail);
        System.out.println(leftOver);
        result.test();
    }

    private void handleLongFlag(@NotNull String token) {
        int equalsIndex = token.indexOf('=');
        if (equalsIndex == -1) {
            final CommandFlag<S> flag = flagGroup.getMatchingFlag(token);
            if (flag == null) {
                leftOver.add(token);
                return;
            }

            handleArguments(flag);
            return;
        }

        String value = token.substring(equalsIndex + 1);
        String flag = token.substring(0, equalsIndex);
    }

    private void handleArguments(@NotNull final CommandFlag<S> flag, @Nullable String arg) {
        if (flag.requiresArg() && !scanner.hasNext()) {
            fail = true;
            return;
        }

        if (!flag.hasArgument()) {
            result.addFlag(flag);
            return;
        }

        scanner.next();
        final String token = scanner.peek();
        final Object argument = flag.resolveArgument(sender, token);

        if (argument == null) {
            if (flag.requiresArg()) {
                fail = true;
                return;
            }

            scanner.previous();
        }

        result.addFlag(flag, argument);
    }

}
