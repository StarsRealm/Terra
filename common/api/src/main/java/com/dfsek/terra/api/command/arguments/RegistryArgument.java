package com.dfsek.terra.api.command.arguments;

import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.component.TypedCommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.dfsek.terra.api.registry.Registry;
import com.dfsek.terra.api.registry.exception.NoSuchEntryException;
import com.dfsek.terra.api.registry.key.RegistryKey;
import com.dfsek.terra.api.util.reflection.TypeKey;


public class RegistryArgument {
    public static <T, R> Builder<T, R> builder(String name, Registry<R> registry) {
        return new Builder<>(name, registry);
    }

    public static <T, R> TypedCommandComponent<T, R> of(String name, Registry<R> registry) {
        return RegistryArgument.<T, R>builder(name, registry).build();
    }

    public static <T, R> TypedCommandComponent<T, R> optional(String name, Registry<R> registry) {
        return RegistryArgument.<T, R>builder(name, registry).optional().build();
    }

    public static <T, R> TypedCommandComponent<T, R> optional(String name, Registry<R> registry, String defaultKey) {
        return RegistryArgument.<T, R>builder(name, registry).optional(DefaultValue.parsed(defaultKey)).build();
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Builder<T, R> builder(String name, Function<CommandContext<T>, Registry<R>> registryFunction,
                                               TypeKey<R> registryType) {
        return new Builder<>(name, registryFunction, (TypeToken<R>) TypeToken.get(registryType.getType()));
    }

    public static <T, R> TypedCommandComponent<T, R> of(String name, Function<CommandContext<T>, Registry<R>> registryFunction,
                                                        TypeKey<R> registryType) {
        return RegistryArgument.builder(name, registryFunction, registryType).build();
    }

    public static <T, R> TypedCommandComponent<T, R> optional(String name, Function<CommandContext<T>, Registry<R>> registryFunction,
                                                              TypeKey<R> registryType) {
        return RegistryArgument.builder(name, registryFunction, registryType).optional().build();
    }

    public static <T, R> TypedCommandComponent<T, R> optional(String name, Function<CommandContext<T>, Registry<R>> registryFunction,
                                                              TypeKey<R> registryType, String defaultKey) {
        return RegistryArgument.builder(name, registryFunction, registryType).optional(DefaultValue.parsed(defaultKey)).build();
    }

    public static final class Builder<T, R> extends TypedCommandComponent.Builder<T, R> {
        @SuppressWarnings("unchecked")
        private Builder(@NonNull String name, Registry<R> registry) {
            super();
            name(name);
            valueType((TypeToken<R>) TypeToken.get(registry.getType().getType()));
            parser(new RegistryArgumentParser<>(commandContext -> registry));
        }

        private Builder(@NonNull String name, Function<CommandContext<T>, Registry<R>> registryFunction, TypeToken<R> typeToken) {
            super();
            name(name);
            valueType(typeToken);
            parser(new RegistryArgumentParser<>(registryFunction));
        }
    }


    private static final class RegistryArgumentParser<C, R> implements ArgumentParser<C, R>, SuggestionProvider<C> {
        private final Function<CommandContext<C>, Registry<R>> registryFunction;

        private RegistryArgumentParser(Function<CommandContext<C>, Registry<R>> registryFunction) {
            this.registryFunction = registryFunction;
        }

        @Override
        @NonNull
        public ArgumentParseResult<@NonNull R> parse(@NonNull CommandContext<@NonNull C> commandContext,
                                                     @NonNull CommandInput commandInput) {
            String input = commandInput.readString();
            Registry<R> registry = registryFunction.apply(commandContext);
            Optional<R> result;
            try {
                result = registry.get(RegistryKey.parse(input));
            } catch(IllegalArgumentException e) {
                try {
                    result = registry.getByID(input);
                } catch(IllegalArgumentException e1) {
                    return ArgumentParseResult.failure(e1);
                }
            }

            return result
                .map(ArgumentParseResult::success)
                .orElse(ArgumentParseResult.failure(new NoSuchEntryException("No such entry: " + input)));
        }


        @Override
        public @NonNull CompletableFuture<@NonNull Iterable<@NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<C> context,
                                                                                                    @NonNull CommandInput input) {
            return CompletableFuture.completedFuture(registryFunction.apply(context)
                .keys()
                .stream()
                .map(RegistryKey::toString)
                .sorted()
                .map(Suggestion::suggestion)
                .toList());
        }
    }
}
