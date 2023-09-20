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

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class JudgeAwardCriterion extends AbstractCriterion<JudgeAwardCriterion.Condition> {
    public static final Identifier ID = new Identifier("civilizations","awarded");
    public static HashSet<String> names = new HashSet<>();

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate pred, AdvancementEntityPredicateDeserializer deserializer) {
        return new Condition(obj.get("name").getAsString());
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, String name) {
        trigger(player, cond -> cond.test(name));
    }

    public static class Condition extends AbstractCriterionConditions {
        String name;

        public Condition(String name) {
            super(ID, LootContextPredicate.EMPTY);
            this.name = name;
        }

        public boolean test(String name){
            return name.equals(this.name);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject jsonObject = super.toJson(serializer);
            jsonObject.add("name", new JsonPrimitive(name));
            return jsonObject;
        }
    }
}
