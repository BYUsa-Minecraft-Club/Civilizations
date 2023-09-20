package showercurtain.civilizations.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import showercurtain.civilizations.data.requests.Request;

public class CreateRequestCriterion extends AbstractCriterion<CreateRequestCriterion.Condition> {
    public static final Identifier ID = new Identifier("civilizations","create_request");

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate pred, AdvancementEntityPredicateDeserializer deserializer) {
        return new Condition();
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, cond -> true);
    }

    public static class Condition extends AbstractCriterionConditions {

        public Condition() {
            super(ID, LootContextPredicate.EMPTY);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            return super.toJson(serializer);
        }
    }
}
