package showercurtain.civilizations.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class DeveloperCriterion extends AbstractCriterion<DeveloperCriterion.Condition> {
    public static final Identifier ID = new Identifier("civilizations","developer");

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate pred, AdvancementEntityPredicateDeserializer deserializer) {
        return new Condition();
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, cond -> cond.test(player));
    }

    public static class Condition extends AbstractCriterionConditions {
        public Condition() {
            super(ID, LootContextPredicate.EMPTY);
        }

        public boolean test(ServerPlayerEntity pl) {
            return pl.getUuid().equals(UUID.fromString("b84818fb-eb59-4c01-a1b9-08e3d8accbcf"));
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            return super.toJson(serializer);
        }
    }
}
