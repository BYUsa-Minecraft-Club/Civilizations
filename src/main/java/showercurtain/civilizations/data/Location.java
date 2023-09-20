package showercurtain.civilizations.data;

import java.util.Arrays;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class Location {
    public BlockPos position;
    public String dimension;

    public String tpCommand() {
        return "/execute in " + dimension + " run tp @s " + position.getX() + " " + position.getY() + " " + position.getZ();
    }

    public String toString() {
        return "x: " + position.getX() + ", y: " + position.getY() + ", z: " + position.getZ() + " in " + dimension;
    }

    public NbtCompound toNbt() {
        NbtCompound out = new NbtCompound();

        out.putIntArray("position", Arrays.asList(position.getX(),position.getY(),position.getZ()));
        out.putString("dimension", dimension);

        return out;
    }

    public static Location fromNbt(NbtCompound nbt) {
        Location out = new Location();

        out.dimension = nbt.getString("dimension");
        
        int[] pos = nbt.getIntArray("position");
        out.position = new BlockPos(pos[0],pos[1],pos[2]);

        return out;
    }

    public static Location fromPlayer(ServerPlayerEntity player) {
        Location out = new Location();

        out.position = player.getBlockPos();
        out.dimension = player.getServerWorld().getRegistryKey().getValue().getPath();

        return out;
    }
}