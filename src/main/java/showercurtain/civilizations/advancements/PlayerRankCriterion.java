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
import showercurtain.civilizations.data.pack.PlayerRank;
import showercurtain.civilizations.data.pack.ResourceLoader;

public class PlayerRankCriterion extends AbstractCriterion<PlayerRankCriterion.Condition> {
    public static final Identifier ID = new Identifier("civilizations","player_rank");

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate pred, AdvancementEntityPredicateDeserializer deserializer) {
        Identifier id = new Identifier(obj.get("playerRank").getAsString());
        return new Condition(id);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, condition -> condition.test(ResourceLoader.playerRanks.get(Civs.data.getPlayer(player).rank)));
    }

    public static class Condition extends AbstractCriterionConditions {
        Identifier rank;

        public Condition(Identifier rank) {
            super(ID, LootContextPredicate.EMPTY);
            this.rank = rank;
        }

        public boolean test(PlayerRank rank) {
            return ResourceLoader.playerRanks.get(this.rank).pointReq() <= rank.pointReq();
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject jsonObject = super.toJson(serializer);
            jsonObject.add("playerRank", new JsonPrimitive(rank.toString()));
            return jsonObject;
        }
    }
}
