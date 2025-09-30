/* Licensed under the <LICENSE> */
package dev.kiddo.animalinfo.client;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

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
            Entity entity = source.getClient().targetedEntity;


            switch (entity) {
                case DonkeyEntity donkey -> {
                    MutableText movementSpeedValue = getMovementSpeed(donkey);
                    MutableText jumpHeightValue = getJumpHeight(donkey);
                    MutableText healthValue = getHealthValue(donkey);

                    MutableText message = formatText(healthValue, jumpHeightValue, movementSpeedValue, null, null, null, null, null);

                    sendInfoMessage(source, "Donkey", message);
                    return 1;
                }
                case MuleEntity mule -> {
                    MutableText movementSpeedValue = getMovementSpeed(mule);
                    MutableText jumpHeightValue = getJumpHeight(mule);
                    MutableText healthValue = getHealthValue(mule);

                    MutableText message = formatText(healthValue, jumpHeightValue, movementSpeedValue, null, null, null, null, null);

                    sendInfoMessage(source, "Mule", message);
                    return 1;
                }
                case HorseEntity horse -> {
                    MutableText movementSpeedValue = getMovementSpeed(horse);
                    MutableText jumpHeightValue = getJumpHeight(horse);
                    MutableText healthValue = getHealthValue(horse);

                    MutableText patternVariantValue = Text.literal(entityColorMap.get(horse.getMarking().name().toLowerCase()))
                            .styled(style -> style.withColor(Formatting.LIGHT_PURPLE));


                    MutableText horseColorValue = Text.literal(entityColorMap.get(horse.getVariant().name().toLowerCase()))
                            .styled(style -> style.withColor(Formatting.BLUE));

                    MutableText message = formatText(healthValue, jumpHeightValue, movementSpeedValue, patternVariantValue, horseColorValue, null, null, null);

                    sendInfoMessage(source, "Horse", message);
                    return 1;

                }
                case LlamaEntity llama -> {
                    MutableText healthValue = getHealthValue(llama);
                    MutableText llamaColorValue = Text.literal(entityColorMap.get(llama.getVariant().name().toLowerCase()))
                            .styled(style -> style.withColor(Formatting.BLUE));

                    MutableText llamaStrengthValue = Text.literal(String.valueOf(llama.getStrength())).styled(style -> style.withColor(Formatting.AQUA));
                    MutableText message = formatText(healthValue, null, null, null, llamaColorValue, llamaStrengthValue, null, null);

                    sendInfoMessage(source, "Llama", message);
                    return 1;
                }
                case PandaEntity panda -> {
                    PandaEntity.Gene mainGene = panda.getMainGene();
                    PandaEntity.Gene hiddenGene = panda.getHiddenGene();
                    String mainGeneRecessive = mainGene.isRecessive() ? "(recessive)" : "(dominant)";
                    String hiddenGeneRecessive = hiddenGene.isRecessive() ? "(recessive)" : "(dominant)";
                    MutableText mainGeneValue = Text.literal(panda.getMainGene().asString().toLowerCase() + " " + mainGeneRecessive).styled(style -> style.withColor(TextColor.fromRgb(0x05a0aa)));
                    MutableText hiddenGeneValue = Text.literal(panda.getHiddenGene().asString().toLowerCase() + " " + hiddenGeneRecessive).styled(style -> style.withColor(TextColor.fromRgb(0x05a0aa)));

                    MutableText message = formatText(null, null, null, null, null, null, hiddenGeneValue, mainGeneValue);
                    sendInfoMessage(source, "Panda", message);
                    return 1;
                }
                case SkeletonHorseEntity skeletonHorse -> {
                    MutableText movementSpeedValue = getMovementSpeed(skeletonHorse);
                    MutableText jumpHeightValue = getJumpHeight(skeletonHorse);
                    MutableText healthValue = getHealthValue(skeletonHorse);

                    MutableText message = formatText(healthValue, jumpHeightValue, movementSpeedValue, null, null, null, null, null);

                    sendInfoMessage(source, "Skeleton Horse", message);
                    return 1;
                }
                case ZombieHorseEntity zombieHorse -> {
                    MutableText movementSpeedValue = getMovementSpeed(zombieHorse);
                    MutableText jumpHeightValue = getJumpHeight(zombieHorse);
                    MutableText healthValue = getHealthValue(zombieHorse);

                    MutableText message = formatText(healthValue, jumpHeightValue, movementSpeedValue, null, null, null, null, null);

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
        MutableText message = Text.literal("No valid entity selected or not an animal").styled(style ->
                style.withColor(TextColor.fromRgb(0xFF0000)));
        source.sendFeedback(message);
    }

    private static void sendInfoMessage(FabricClientCommandSource source, String entity, MutableText message) {
        MutableText baseText = Text.literal(entity + " Information").styled(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableText comma = Text.literal(": ").styled(style -> style.withColor(Formatting.WHITE));

        source.sendFeedback(baseText.append(comma).append(message));
    }

    public static MutableText getMovementSpeed(LivingEntity entity) {
        DecimalFormat df = new DecimalFormat("#.###");
        double baseValue = entity.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        String formattedSpeed = df.format(baseValue * 42.1629629629629);
        return Text.literal(formattedSpeed).styled(style -> style.withColor(TextColor.fromRgb(0x79BAEC)));
    }

    public static MutableText getJumpHeight(LivingEntity entity) {
        DecimalFormat df = new DecimalFormat("#.###");
        double baseValue = entity.getAttributeBaseValue(EntityAttributes.GENERIC_JUMP_STRENGTH);
        double convertedValue = -0.1817584952 * baseValue * baseValue * baseValue + 3.689713992 * baseValue * baseValue + 2.128599134 * baseValue - 0.343930367;
        String formattedHeight = df.format(convertedValue);
        return Text.literal(formattedHeight).styled(style -> style.withColor(TextColor.fromRgb(0x4dd676)));
    }

    public static MutableText getHealthValue(LivingEntity entity) {
        DecimalFormat df = new DecimalFormat("#.###");
        double baseValue = entity.getMaxHealth();
        String formattedHealth = df.format(baseValue);
        return Text.literal(formattedHealth).styled(style -> style.withColor(Formatting.RED));
    }

    public static MutableText formatText(MutableText healthValue, MutableText jumpHeightValue, MutableText movementSpeedValue, MutableText patternVariantValue, MutableText colorValue, MutableText strengthValue, MutableText hiddenGene, MutableText mainGene) {
        MutableText colonChar = Text.literal(": ").styled(style -> style.withColor(Formatting.WHITE));
        MutableText healthMessage = Text.literal("\nHealth").styled(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableText jumpHeightMessage = Text.literal("\nJump Height").styled(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableText movementSpeedMessage = Text.literal("\nSpeed").styled(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableText patternVariantMessage = Text.literal("\nPattern Variant").styled(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableText colorMessage = Text.literal("\nColor").styled(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableText strengthMessage = Text.literal("\nStrength").styled(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableText hiddenGeneMessage = Text.literal("\nHidden Gene").styled(style -> style.withColor(TextColor.fromRgb(0xffca800)));
        MutableText mainGeneMessage = Text.literal("\nMain Gene").styled(style -> style.withColor(TextColor.fromRgb(0xffca800)));


        MutableText message = Text.empty();

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
