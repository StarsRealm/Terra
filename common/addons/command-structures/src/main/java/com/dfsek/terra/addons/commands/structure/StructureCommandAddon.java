package com.dfsek.terra.addons.commands.structure;

import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;

import java.util.Random;

import com.dfsek.terra.addons.manifest.api.AddonInitializer;
import com.dfsek.terra.api.Platform;
import com.dfsek.terra.api.addon.BaseAddon;
import com.dfsek.terra.api.command.CommandSender;
import com.dfsek.terra.api.command.arguments.RegistryArgument;
import com.dfsek.terra.api.entity.Entity;
import com.dfsek.terra.api.event.events.platform.CommandRegistrationEvent;
import com.dfsek.terra.api.event.functional.FunctionalEventHandler;
import com.dfsek.terra.api.inject.annotations.Inject;
import com.dfsek.terra.api.registry.Registry;
import com.dfsek.terra.api.structure.Structure;
import com.dfsek.terra.api.util.Rotation;
import com.dfsek.terra.api.util.reflection.TypeKey;


public class StructureCommandAddon implements AddonInitializer {
    @Inject
    private Platform platform;

    @Inject
    private BaseAddon addon;

    private static Registry<Structure> getStructureRegistry(CommandContext<CommandSender> sender) {
        return sender.sender().getEntity().orElseThrow().world().getPack().getRegistry(Structure.class);
    }

    @Override
    public void initialize() {
        platform.getEventManager()
            .getHandler(FunctionalEventHandler.class)
            .register(addon, CommandRegistrationEvent.class)
            .then(event -> {
                CommandManager<CommandSender> manager = event.getCommandManager();

                manager.command(
                    manager.commandBuilder("structures", Description.of("Manage or generate structures"))
                        .literal("generate")
                        .argument(RegistryArgument.builder("structure",
                            StructureCommandAddon::getStructureRegistry,
                            TypeKey.of(Structure.class)))
                        .optional(
                            CommandComponent.ofType(Long.class, "seed")
                                .defaultValue(DefaultValue.constant(0L))
                        )
                        .optional(
                            CommandComponent.ofType(Rotation.class, "rotation")
                                .defaultValue(DefaultValue.constant(Rotation.NONE))
                        )
                        .handler(context -> {
                            Structure structure = context.get("structure");
                            Entity sender = context.sender().getEntity().orElseThrow();
                            structure.generate(
                                sender.position().toInt(),
                                sender.world(),
                                ((Long) context.get("seed") == 0) ? new Random() : new Random(context.get("seed")),
                                context.get("rotation")
                            );
                        })
                        .permission("terra.structures.generate")
                );
            });
    }
}
