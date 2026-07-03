/* Licensed under the <LICENSE> */
package dev.kiddo.animalinfo.client;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

import com.mojang.brigadier.CommandDispatcher;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.equine.Donkey;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.entity.animal.equine.ZombieHorse;

public class AnimalInfoCommandHandler {

    private static final Map<String, String> entityColorMap = new HashMap<>();

    static {
        // Horse Pattern Variants
        entityColorMap.put("white_field", "Whitefield");
        entityColorMap.put("white_dots", "White Spots");
        entityColorMap.put("black_dots", "Black Dots");
        entityColorMap.put("white", "White"); // same for color and pattern
        entityColorMap.put("none", "Plain");

        // Entity Colors
        entityColorMap.put("dark_brown", "Dark Brown");
        entityColorMap.put("chestnut", "Chestnut");
        entityColorMap.put("brown", "Brown");
        entityColorMap.put("black", "Black");
        entityColorMap.put("gray", "Gray");
        entityColorMap.put("creamy", "Creamy");
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("animalinfo").executes(context -> {
            FabricClientCommandSource source = context.getSource();

            // Get the entity being looked at
            Entity entity = source.getClient().crosshairPickEntity;

            switch (entity) {
                case Donkey donkey -> {
                    MutableComponent movementSpeedValue = getMovementSpeed(donkey);
                    MutableComponent jumpHeightValue = getJumpHeight(donkey);
                    MutableComponent healthValue = getHealthValue(donkey);

                    MutableComponent message = formatText(healthValue, jumpHeightValue, movementSpeedValue, null, null, null, null, null);

                    sendInfoMessage(source, "Donkey", message);
                    return 1;
                }
                case Mule mule -> {
                    MutableComponent movementSpeedValue = getMovementSpeed(mule);
                    MutableComponent jumpHeightValue = getJumpHeight(mule);
                    MutableComponent healthValue = getHealthValue(mule);

                    MutableComponent message = formatText(healthValue, jumpHeightValue, movementSpeedValue, null, null, null, null, null);

                    sendInfoMessage(source, "Mule", message);
                    return 1;
                }
                case Horse horse -> {
                    MutableComponent movementSpeedValue = getMovementSpeed(horse);
                    MutableComponent jumpHeightValue = getJumpHeight(horse);
                    MutableComponent healthValue = getHealthValue(horse);

                    MutableComponent patternVariantValue = Component.literal(entityColorMap.get(horse.getMarkings().name().toLowerCase()))
                            .withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE));

                    MutableComponent horseColorValue = Component.literal(entityColorMap.get(horse.getVariant().name().toLowerCase()))
                            .withStyle(style -> style.withColor(ChatFormatting.BLUE));

                    MutableComponent message = formatText(healthValue, jumpHeightValue, movementSpeedValue, patternVariantValue, horseColorValue, null, null, null);

                    sendInfoMessage(source, "Horse", message);
                    return 1;

                }
                case Llama llama -> {
                    MutableComponent healthValue = getHealthValue(llama);
                    MutableComponent llamaColorValue = Component.literal(entityColorMap.get(llama.getVariant().name().toLowerCase()))
                            .withStyle(style -> style.withColor(ChatFormatting.BLUE));

                    MutableComponent llamaStrengthValue = Component.literal(String.valueOf(llama.getStrength())).withStyle(style -> style.withColor(ChatFormatting.AQUA));
                    MutableComponent message = formatText(healthValue, null, null, null, llamaColorValue, llamaStrengthValue, null, null);

                    sendInfoMessage(source, "Llama", message);
                    return 1;
                }
                case Panda panda -> {
                    Panda.Gene mainGene = panda.getMainGene();
                    Panda.Gene hiddenGene = panda.getHiddenGene();
                    String mainGeneRecessive = mainGene.isRecessive() ? "(recessive)" : "(dominant)";
                    String hiddenGeneRecessive = hiddenGene.isRecessive() ? "(recessive)" : "(dominant)";
                    MutableComponent mainGeneValue = Component.literal(panda.getMainGene().name().toLowerCase() + " " + mainGeneRecessive).withStyle(style -> style.withColor(TextColor.fromRgb(0x05a0aa)));
                    MutableComponent hiddenGeneValue = Component.literal(panda.getHiddenGene().name().toLowerCase() + " " + hiddenGeneRecessive).withStyle(style -> style.withColor(TextColor.fromRgb(0x05a0aa)));

                    MutableComponent message = formatText(null, null, null, null, null, null, hiddenGeneValue, mainGeneValue);
                    sendInfoMessage(source, "Panda", message);
                    return 1;
                }
                case SkeletonHorse skeletonHorse -> {
                    MutableComponent movementSpeedValue = getMovementSpeed(skeletonHorse);
                    MutableComponent jumpHeightValue = getJumpHeight(skeletonHorse);
                    MutableComponent healthValue = getHealthValue(skeletonHorse);

                    MutableComponent message = formatText(healthValue, jumpHeightValue, movementSpeedValue, null, null, null, null, null);

                    sendInfoMessage(source, "Skeleton Horse", message);
                    return 1;
                }
                case ZombieHorse zombieHorse -> {
                    MutableComponent movementSpeedValue = getMovementSpeed(zombieHorse);
                    MutableComponent jumpHeightValue = getJumpHeight(zombieHorse);
                    MutableComponent healthValue = getHealthValue(zombieHorse);

                    MutableComponent message = formatText(healthValue, jumpHeightValue, movementSpeedValue, null, null, null, null, null);

                    sendInfoMessage(source, "Zombie Horse", message);
                    return 1;
                }
                case null, default -> {
                    sendInvalidEntityMessage(source);
                    return 0;
                }
            }
        }));
    }

    private static void sendInvalidEntityMessage(FabricClientCommandSource source) {
        MutableComponent message = Component.literal("No valid entity selected or not an animal").withStyle(style ->
                style.withColor(TextColor.fromRgb(0xFF0000)));
        source.sendError(message);
    }

    private static void sendInfoMessage(FabricClientCommandSource source, String entity, MutableComponent message) {
        MutableComponent baseText = Component.literal(entity + " Information").withStyle(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableComponent comma = Component.literal(": ").withStyle(style -> style.withColor(ChatFormatting.WHITE));

        source.sendFeedback(baseText.append(comma).append(message));
    }

    public static MutableComponent getMovementSpeed(LivingEntity entity) {
        DecimalFormat df = new DecimalFormat("#.###");
        double baseValue = entity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
        String formattedSpeed = df.format(baseValue * 42.1629629629629);
        return Component.literal(formattedSpeed).withStyle(style -> style.withColor(TextColor.fromRgb(0x79BAEC)));
    }

    public static MutableComponent getJumpHeight(LivingEntity entity) {
        DecimalFormat df = new DecimalFormat("#.###");
        double baseValue = entity.getAttributeBaseValue(Attributes.JUMP_STRENGTH);
        double convertedValue = -0.1817584952 * baseValue * baseValue * baseValue + 3.689713992 * baseValue * baseValue + 2.128599134 * baseValue - 0.343930367;
        String formattedHeight = df.format(convertedValue);
        return Component.literal(formattedHeight).withStyle(style -> style.withColor(TextColor.fromRgb(0x4dd676)));
    }

    public static MutableComponent getHealthValue(LivingEntity entity) {
        DecimalFormat df = new DecimalFormat("#.###");
        double baseValue = entity.getMaxHealth();
        String formattedHealth = df.format(baseValue);
        return Component.literal(formattedHealth).withStyle(style -> style.withColor(ChatFormatting.RED));
    }

    public static MutableComponent formatText(MutableComponent healthValue, MutableComponent jumpHeightValue, MutableComponent movementSpeedValue, MutableComponent patternVariantValue, MutableComponent colorValue, MutableComponent strengthValue, MutableComponent hiddenGene, MutableComponent mainGene) {
        MutableComponent colonChar = Component.literal(": ").withStyle(style -> style.withColor(ChatFormatting.WHITE));
        MutableComponent healthMessage = Component.literal("\nHealth").withStyle(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableComponent jumpHeightMessage = Component.literal("\nJump Height").withStyle(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableComponent movementSpeedMessage = Component.literal("\nSpeed").withStyle(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableComponent patternVariantMessage = Component.literal("\nPattern Variant").withStyle(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableComponent colorMessage = Component.literal("\nColor").withStyle(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableComponent strengthMessage = Component.literal("\nStrength").withStyle(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableComponent hiddenGeneMessage = Component.literal("\nHidden Gene").withStyle(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableComponent mainGeneMessage = Component.literal("\nMain Gene").withStyle(style -> style.withColor(TextColor.fromRgb(0xffca800)));

        MutableComponent message = Component.empty();

        if (movementSpeedValue != null) {
            message.append(movementSpeedMessage).append(colonChar).append(movementSpeedValue);
        }

        if (jumpHeightValue != null) {
            message.append(jumpHeightMessage).append(colonChar).append(jumpHeightValue);
        }

        if (healthValue != null) {
            message.append(healthMessage).append(colonChar).append(healthValue);
        }

        if (patternVariantValue != null) {
            message.append(patternVariantMessage).append(colonChar).append(patternVariantValue);
        }

        if (colorValue != null) {
            message.append(colorMessage).append(colonChar).append(colorValue);
        }

        if (strengthValue != null) {
            message.append(strengthMessage).append(colonChar).append(strengthValue);
        }

        if (mainGene != null) {
            message.append(mainGeneMessage).append(colonChar).append(mainGene);
        }

        if (hiddenGene != null) {
            message.append(hiddenGeneMessage).append(colonChar).append(hiddenGene);
        }

        return message;

    }

}
