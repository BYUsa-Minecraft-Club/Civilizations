package showercurtain.civilizations.advancements;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import showercurtain.civilizations.data.requests.Request;

public class RequestDeniedCriterion extends AbstractCriterion<RequestDeniedCriterion.Condition> {
    public static final Identifier ID = new Identifier("civilizations","request_denied");

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate pred, AdvancementEntityPredicateDeserializer deserializer) {
        return new Condition(Request.RequestType.valueOf(obj.get("type").getAsString().toUpperCase()));
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, Request.RequestType type) {
        trigger(player, cond -> cond.test(type));
    }

    public static class Condition extends AbstractCriterionConditions {
        Request.RequestType type;

        public Condition(Request.RequestType type) {
            super(ID, LootContextPredicate.EMPTY);
            this.type = type;
        }

        public boolean test(Request.RequestType t) {
            return t.equals(type);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject jsonObject = super.toJson(serializer);
            jsonObject.add("type", new JsonPrimitive(type.toString().toLowerCase()));
            return jsonObject;
        }
    }
}
