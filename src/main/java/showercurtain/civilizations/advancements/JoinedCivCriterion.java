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

public class JoinedCivCriterion extends AbstractCriterion<JoinedCivCriterion.Condition> {
    public static final Identifier ID = new Identifier("civilizations","joined_civ");

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate pred, AdvancementEntityPredicateDeserializer deserializer) {
        return new Condition();
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        Player p = Civs.data.getPlayer(player);
        trigger(player, cond -> cond.test(p));
    }

    public static class Condition extends AbstractCriterionConditions {
        public Condition() {
            super(ID, LootContextPredicate.EMPTY);
        }

        public boolean test(Player p) {
            return !p.civs.isEmpty();
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            return super.toJson(serializer);
        }
    }
}
