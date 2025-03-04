package kl1nge5.create_build_gun;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kl1nge5.create_build_gun.data.StageData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static net.minecraft.commands.Commands.literal;

public class AllCommands {
    public static void init() {
        NeoForge.EVENT_BUS.addListener(AllCommands::handle);
    }

    @SubscribeEvent
    public static void handle(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        LiteralArgumentBuilder<CommandSourceStack> root = literal("buildgun")
                .requires(cs -> cs.hasPermission(0))
                .then(LevelStageCommand.register());
        dispatcher.register(root);
    }
}

class LevelStageCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        // add set/get stage command
        return literal("stage").then(literal("set").requires(cs -> cs.hasPermission(3)).then(Commands.argument("stage", IntegerArgumentType.integer()).executes(ctx -> {
            CommandSourceStack source = ctx.getSource();
            int stage = IntegerArgumentType.getInteger(ctx, "stage");
            StageData state = (StageData) source.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<SavedData>(StageData::new, StageData::load), BuildGun.MODID);
            state.stage = stage;
            state.setDirty();
            source.sendSuccess(()-> Component.translatable("create_build_gun.command.set_stage_success").append(" [" + stage + "]"), true);
            return Command.SINGLE_SUCCESS;
        }))).then(literal("get").requires(cs -> cs.hasPermission(0)).executes(ctx -> {
            StageData state = (StageData) ctx.getSource().getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<SavedData>(StageData::new, StageData::load), BuildGun.MODID);
            ctx.getSource().sendSuccess(()->Component.translatable("create_build_gun.command.get_stage_success").append(" [" + state.stage + "]"), true);
            return Command.SINGLE_SUCCESS;
        }));
    }
}