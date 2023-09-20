package showercurtain.civilizations.advancements;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Civilization;
import showercurtain.civilizations.data.pack.CivRank;
import showercurtain.civilizations.data.pack.ResourceLoader;

public class OwnedCivRankCriterion extends AbstractCriterion<OwnedCivRankCriterion.Condition> {
    public static final Identifier ID = new Identifier("civilizations","owned_civ_rank");

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate pred, AdvancementEntityPredicateDeserializer deserializer) {
        Identifier id = new Identifier(obj.get("civRank").getAsString());
        return new Condition(id);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        for (Integer civ : Civs.data.getPlayer(player).civs) {
            Civilization c = Civs.data.civs.get(civ);
            if (c.owner.equals(player.getUuid())) {
                trigger(player, condition -> condition.test(ResourceLoader.civRanks.get(c.rank)));
            }
        }
    }

    public static class Condition extends AbstractCriterionConditions {
        Identifier rank;

        public Condition(Identifier rank) {
            super(ID, LootContextPredicate.EMPTY);
            this.rank = rank;
        }

        public boolean test(CivRank rank) {
            return ResourceLoader.civRanks.get(this.rank).pointReq() <= rank.pointReq();
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject jsonObject = super.toJson(serializer);
            jsonObject.add("civRank", new JsonPrimitive(rank.toString()));
            return jsonObject;
        }
    }
}
