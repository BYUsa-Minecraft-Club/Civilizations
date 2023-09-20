package showercurtain.civilizations.mixins;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.pack.PlayerTitle;
import showercurtain.civilizations.data.pack.ResourceLoader;

import java.util.Map;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow
    public abstract AdvancementProgress getProgress(Advancement advancement);

    @Shadow
    private ServerPlayerEntity owner;

    @Inject(method="onStatusUpdate(Lnet/minecraft/advancement/Advancement;)V", at=@At("TAIL"))
    void onStatusUpdate(Advancement advancement, CallbackInfo ci) {
        if (getProgress(advancement).isDone()) {
            for (Map.Entry<Identifier, PlayerTitle> e : ResourceLoader.titles.entrySet()) {
                if (e.getValue().requires().equals(advancement.getId().toString())) {
                    e.getValue().addTitle(owner.getUuid());
                    Civs.data.players.get(owner.getUuid()).obtainedTitles.add(e.getKey());
                }
            }
        } else {
            for (Map.Entry<Identifier, PlayerTitle> e : ResourceLoader.titles.entrySet()) {
                if (e.getValue().requires().equals(advancement.getId().toString())) {
                    e.getValue().removeTitle(owner.getUuid());
                    Civs.data.players.get(owner.getUuid()).obtainedTitles.remove(e.getKey());
                }
            }
        }
    }
}
