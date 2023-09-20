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

public class BuildsCriterion extends AbstractCriterion<BuildsCriterion.Condition> {
    public static final Identifier ID = new Identifier("civilizations","builds");

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate pred, AdvancementEntityPredicateDeserializer deserializer) {
        return new Condition(obj.get("builds").getAsInt());
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        Player p = Civs.data.getPlayer(player);
        trigger(player, cond -> cond.test(p.builds.size()));
    }

    public static class Condition extends AbstractCriterionConditions {
        int builds;

        public Condition(int builds) {
            super(ID, LootContextPredicate.EMPTY);
            this.builds = builds;
        }

        public boolean test(int builds) {
            return this.builds <= builds;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject jsonObject = super.toJson(serializer);
            jsonObject.add("builds", new JsonPrimitive(builds));
            return jsonObject;
        }
    }
}
