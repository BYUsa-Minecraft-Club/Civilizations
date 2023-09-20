package showercurtain.civilizations;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import showercurtain.civilizations.advancements.*;
import showercurtain.civilizations.commands.*;
import showercurtain.civilizations.data.Player;
import showercurtain.civilizations.data.SaveData;
import showercurtain.civilizations.data.Config;
import showercurtain.civilizations.data.pack.PlayerRank;
import showercurtain.civilizations.data.pack.PlayerTitle;
import showercurtain.civilizations.data.pack.ResourceLoader;
import showercurtain.civilizations.ui.cli.Traceback;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Civs implements DedicatedServerModInitializer, ServerStarted {
    public static final Logger LOGGER = LoggerFactory.getLogger("civilizations");

	public static Config config;
	public static SaveData data;
	public static LuckPerms perms;

	public static final OwnedCivRankCriterion OWNS_RANKED_CIV_CRITERION = Criteria.register(new OwnedCivRankCriterion());
	public static final MemberCivRankCriterion MEMBER_RANKED_CIV_CRITERION = Criteria.register(new MemberCivRankCriterion());
	public static final OwnedCivMembersCriterion OWNED_CIV_MEMBERS_CRITERION = Criteria.register(new OwnedCivMembersCriterion());
	public static final MemberCivMembersCriterion MEMBER_CIV_MEMBERS_CRITERION = Criteria.register(new MemberCivMembersCriterion());
	public static final BuildPointsCriterion POINTS_CRITERION = Criteria.register(new BuildPointsCriterion());
	public static final BuildsCriterion BUILDS_CRITERION = Criteria.register(new BuildsCriterion());
	public static final DeveloperCriterion DEVELOPER_CRITERION = Criteria.register(new DeveloperCriterion());
	public static final JoinedCivCriterion JOINED_CIV_CRITERION = Criteria.register(new JoinedCivCriterion());
	public static final PlayerRankCriterion PLAYER_RANK_CRITERION = Criteria.register(new PlayerRankCriterion());
	public static final RequestAcceptedCriterion REQUEST_ACCEPTED_CRITERION = Criteria.register(new RequestAcceptedCriterion());
	public static final RequestDeniedCriterion REQUEST_DENIED_CRITERION = Criteria.register(new RequestDeniedCriterion());
	public static final CreateRequestCriterion CREATE_REQUEST_CRITERION = Criteria.register(new CreateRequestCriterion());
	public static final JudgeAwardCriterion JUDGE_AWARD_CRITERION = Criteria.register(new JudgeAwardCriterion());

	@Override
	public void onInitializeServer() {
		ServerLifecycleEvents.SERVER_STARTED.register(this);
		ServerPlayConnectionEvents.JOIN.register(Civs::onPlayerJoin);
		CommandRegistrationCallback.EVENT.register(Traceback::register);
		CommandRegistrationCallback.EVENT.register(ManageCommands::register);
		CommandRegistrationCallback.EVENT.register(MiscCommands::register);
		CommandRegistrationCallback.EVENT.register(RequestCommands::register);
		CommandRegistrationCallback.EVENT.register(RequestModifyCommands::register);
		CommandRegistrationCallback.EVENT.register(ViewCommands::register);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ResourceLoader());
		Placeholders.register(new Identifier("civilizations", "title"), (ctx, arg) -> {
			if (!ctx.hasPlayer()) return PlaceholderResult.invalid("No player");
			Player p = data.players.get(ctx.player().getUuid());
			if (p.title==null) return PlaceholderResult.value("");
			PlayerTitle t = ResourceLoader.titles.get(p.title);
			return PlaceholderResult.value(Text.literal(t.name()).setStyle(CivUtil.DEFAULTSTYLE.withColor(t.color().toInt())));
		});
		Placeholders.register(new Identifier("civilizations", "playerrank"), (ctx, arg) -> {
			if (!ctx.hasPlayer()) return PlaceholderResult.invalid("No player");
			Player p = data.players.get(ctx.player().getUuid());
			if (p.rank==null) return PlaceholderResult.value("");
			PlayerRank t = ResourceLoader.playerRanks.get(p.rank);
			return PlaceholderResult.value(t.displayName());
		});
		Placeholders.register(new Identifier("civilizations", "playerprefix"), (ctx, arg) -> {
			if (!ctx.hasPlayer()) return PlaceholderResult.invalid("No player");
			Player p = data.players.get(ctx.player().getUuid());
			if (p.rank==null) return PlaceholderResult.value("");
			PlayerRank r = ResourceLoader.playerRanks.get(p.rank);
			MutableText base = p.rank == null ? Text.literal("") : Text.literal(r.displayName());
			if (p.title==null) return PlaceholderResult.value(base);
			PlayerTitle t = ResourceLoader.titles.get(p.title);
			base.append(Text.literal(t.name()).setStyle(CivUtil.DEFAULTSTYLE.withColor(t.color().toInt())));
			return PlaceholderResult.value(base);
		});
	}

	@Override
	public void onServerStarted(MinecraftServer server) {
		perms = LuckPermsProvider.get();
		config = Config.loadConfig();
		data = SaveData.fromServer(server);
		data.markDirty();
	}

	public static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		ServerPlayerEntity player = handler.getPlayer();
		data.getPlayer(player);
		Traceback.newContext(player.getUuid());
		DEVELOPER_CRITERION.trigger(player);
		OWNS_RANKED_CIV_CRITERION.trigger(player);
		MEMBER_RANKED_CIV_CRITERION.trigger(player);
		POINTS_CRITERION.trigger(player);
		JOINED_CIV_CRITERION.trigger(player);
		PLAYER_RANK_CRITERION.trigger(player);
		OWNED_CIV_MEMBERS_CRITERION.trigger(player);
		MEMBER_CIV_MEMBERS_CRITERION.trigger(player);
	}
}