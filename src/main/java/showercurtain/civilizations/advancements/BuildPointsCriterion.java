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
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Player;

public class BuildPointsCriterion extends AbstractCriterion<BuildPointsCriterion.Condition> {
    public static final Identifier ID = new Identifier("civilizations","build_points");

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate pred, AdvancementEntityPredicateDeserializer deserializer) {
        return new Condition(obj.get("points").getAsInt());
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        Player p = Civs.data.getPlayer(player);
        trigger(player, cond -> cond.test(p.points));
    }

    public static class Condition extends AbstractCriterionConditions {
        int points;

        public Condition(int points) {
            super(ID, LootContextPredicate.EMPTY);
            this.points = points;
        }

        public boolean test(int points) {
            return this.points <= points;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject jsonObject = super.toJson(serializer);
            jsonObject.add("points", new JsonPrimitive(points));
            return jsonObject;
        }
    }
}
