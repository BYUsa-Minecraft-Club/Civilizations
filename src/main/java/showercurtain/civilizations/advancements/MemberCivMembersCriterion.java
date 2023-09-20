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
import showercurtain.civilizations.data.Civilization;
import showercurtain.civilizations.data.pack.CivRank;
import showercurtain.civilizations.data.pack.ResourceLoader;

public class MemberCivMembersCriterion extends AbstractCriterion<MemberCivMembersCriterion.Condition> {
    public static final Identifier ID = new Identifier("civilizations","member_civ_members");

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate pred, AdvancementEntityPredicateDeserializer deserializer) {
        int num = obj.get("players").getAsInt();
        return new Condition(num);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        for (Integer civ : Civs.data.getPlayer(player).civs) {
            Civilization c = Civs.data.civs.get(civ);
            trigger(player, condition -> condition.test(c.players.size()));
        }
    }

    public static class Condition extends AbstractCriterionConditions {
        int players;

        public Condition(int players) {
            super(ID, LootContextPredicate.EMPTY);
            this.players = players;
        }

        public boolean test(int players) {
            return this.players <= players;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject jsonObject = super.toJson(serializer);
            jsonObject.add("players", new JsonPrimitive(players));
            return jsonObject;
        }
    }
}
