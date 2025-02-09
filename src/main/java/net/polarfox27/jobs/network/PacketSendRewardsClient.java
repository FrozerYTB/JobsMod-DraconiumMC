package net.polarfox27.jobs.network;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.polarfox27.jobs.data.ClientJobsData;

public class PacketSendRewardsClient implements IMessage {

    private final List<ItemStack> stacks = new ArrayList<>();

    public PacketSendRewardsClient(){}
    public PacketSendRewardsClient(List<ItemStack> rewards) {
        for(ItemStack s : rewards)
            stacks.add(s.copy());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            Item item = Item.getItemById(buf.readInt());
            int count = buf.readInt();
            stacks.add(new ItemStack(item, count));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(stacks.size());
        for(ItemStack s : stacks) {
            buf.writeInt(Item.getIdFromItem(s.getItem()));
            buf.writeInt(s.getCount());
        }

    }

    public static class MessageHandler implements IMessageHandler<PacketSendRewardsClient, IMessage> {

        @Override
        public IMessage onMessage(PacketSendRewardsClient m, MessageContext ctx) {
            if(ctx.side == Side.CLIENT)
            {
                ClientJobsData.CURRENT_REWARDS.clear();
                for(ItemStack s : m.stacks)
                    ClientJobsData.CURRENT_REWARDS.add(s.copy());
            }
            return null;
        }
    }
}
