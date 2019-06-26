package cn.onlyhi.client.server;

import cn.onlyhi.client.common.BaseMsg;
import cn.onlyhi.client.common.LoginMsg;
import cn.onlyhi.client.common.ReplyMsg;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/9/4.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);
    //channelId:token
    private static Map<String, String> channelTokenMap = new ConcurrentHashMap<>();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocketChannel socketChannel = (SocketChannel) ctx.channel();
        LOGGER.info("连接{},异常:{}", socketChannel, cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel socketChannel = (SocketChannel) ctx.channel();
        String channelId = socketChannel.id().asShortText();
        //把channel存到服务端的map中
        NettyChannelMap.add(channelId, socketChannel);
        System.out.println("建立连接成功");
        LOGGER.info("{}建立连接", socketChannel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel socketChannel = (SocketChannel) ctx.channel();
        //channel失效，从Map中移除
        String channelId = socketChannel.id().asShortText();
        NettyChannelMap.remove(channelId);
        ClientChannelMap.removeById(channelId);
        LOGGER.info("{}断开连接", socketChannel);
    }

 /*   public static void main(String[] args) {
        String msg="{\"type\":1,\"userMark\":\"18950676088teacher\",\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImY1ZWRjNzBkYTI0ZTFiY2QyNmYyMzAzMTkyMzlhYzYwYjUwOTYxNjNlYjk3YTZkZDg0YWNhZTc1ZDI5NjRmYWFiYWNmOTI2NTU5ZGE0MTI3In0.eyJhdWQiOiIxIiwianRpIjoiZjVlZGM3MGRhMjRlMWJjZDI2ZjIzMDMxOTIzOWFjNjBiNTA5NjE2M2ViOTdhNmRkODRhY2FlNzVkMjk2NGZhYWJhY2Y5MjY1NTlkYTQxMjciLCJpYXQiOjE1NTU5MDIyMTQsIm5iZiI6MTU1NTkwMjIxNCwiZXhwIjoxNTU1OTg4NjE0LCJzdWIiOiIyMDkiLCJzY29wZXMiOltdfQ.k0jasOmWy22lRItD4GRsm2tXONISY-GNZaoCBdY5sa_7fY9yNIwAlmoehuFQT8mEmLIEnopqldk7UmxPdaYbC3RVc72EbeUHE0iiwqJQV2zfETq0z1gru5Eoq37IN6aI1-nqBE4b83Gqj2ZIsdNTGMU1KdB5Tx3iv7FO0pnVtDdZy5xDQLTGyGp9lBOXwD4-KP4dmaIkeV7ZihbDzoqzX-NgWYDVZf3upPpyTqTeTCWmeWQQ_UCncsKYHJQddnoViX-JCbLT3ys1z4qprxj-QOeaq_AuhcKDfQWtChX8oncrV0Xsy6DYZHoEKZNx5ylBFzOXzRxfC2iYgG_UU2aASTznFOgxXtxP1MqTr_8CdeGc-DKN2Az-PtfoT3C6MIqQ-UEeweQ0naZxvS-eeSWAvggo0XJ2mmFyyHgTowFLuXHaoaHd4uLE908JhC4SW74MMjYO6pqQ4PkG8az1ZdtCBZSRVF4R6Yetfrx-KXbOt874YHzXDa1YV-zHcucb16y6yWkfekuc0OkQ7bhNF1RmR-akvCMRhNMPG0k_zmM-Pd1TXD-wPAFLNHVUUzdN16oILbOFzt0RNQ1aOBAxse10S5yuSKi9pWVZFmIIlb-2gzgxj_8ghyLrKlhTJh94rctjQ0PZ5J1GjWP6tscuoNFufPF6EDf9uW1joFYx--fZh3A\"}";
        BaseMsg baseMsg = JSON.parseObject(msg, BaseMsg.class);
        System.out.println(baseMsg);
    }*/
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        LOGGER.info("msg={}", msg);
        SocketChannel socketChannel = (SocketChannel) channelHandlerContext.channel();
        String channelId = socketChannel.id().asShortText();

        BaseMsg baseMsg = JSON.parseObject(msg, BaseMsg.class);
        int type = baseMsg.getType();
        //正常登录
        if (type == 2) {
            LoginMsg loginMsg = JSON.parseObject(msg, LoginMsg.class);
            String userMark = loginMsg.getUserMark().toUpperCase();
            String token = loginMsg.getToken();
            String oldChannelId = ClientChannelMap.get(userMark);
            //账号有登录
            if (oldChannelId != null) {
                ReplyMsg replyMsg = new ReplyMsg(3);
                SocketChannel channel = NettyChannelMap.get(oldChannelId);
                channel.writeAndFlush(JSON.toJSONString(replyMsg));
                ClientChannelMap.remove(userMark);
                channelTokenMap.remove(oldChannelId);
                //LOGGER.info("{}被{}T返回reply={}", oldChannelId, channelId, 3);
            }
            ReplyMsg replyMsg = new ReplyMsg(2);
            socketChannel.writeAndFlush(JSON.toJSONString(replyMsg));
            ClientChannelMap.add(userMark, channelId);
            channelTokenMap.put(channelId, token);
            //LOGGER.info("{}正常返回reply={}", channelId, 2);
        } else if (type == 1) {
            ReplyMsg replyMsg = new ReplyMsg(1);
            socketChannel.writeAndFlush(JSON.toJSONString(replyMsg));
            //LOGGER.info("心跳返回reply={}", 1);
            //断线重连
        } else if (type == 3) {
            LoginMsg loginMsg = JSON.parseObject(msg, LoginMsg.class);
            String userMark = loginMsg.getUserMark().toUpperCase();
            String token = loginMsg.getToken();
            String oldChannelId = ClientChannelMap.get(userMark);
            //LOGGER.info("ClientChannelMap={}", ClientChannelMap.print());
            //LOGGER.info("NettyChannelMap={}", NettyChannelMap.print());
            if (oldChannelId != null) {
                //LOGGER.info("channelTokenMap:keySet={},values={}", channelTokenMap.keySet(), channelTokenMap.values());
                String oldToken = channelTokenMap.get(oldChannelId);
                //断线重连期间账号没有在其他客户端登录
                if (token.equals(oldToken)) {
                    ReplyMsg replyMsg = new ReplyMsg(2);
                    socketChannel.writeAndFlush(JSON.toJSONString(replyMsg));
                    ClientChannelMap.add(userMark, channelId);
                    channelTokenMap.put(channelId, token);
                    channelTokenMap.remove(oldChannelId);
                    //LOGGER.info("{}正常返回reply={}", channelId, 2);
                } else {
                    ReplyMsg replyMsg = new ReplyMsg(3);
                    SocketChannel channel = NettyChannelMap.get(channelId);
                    channel.writeAndFlush(JSON.toJSONString(replyMsg));
                    channelTokenMap.remove(oldChannelId);
                    //LOGGER.info("{}被{}T返回reply={}", channelId, oldChannelId, 3);
                }
            } else {
                ReplyMsg replyMsg = new ReplyMsg(2);
                socketChannel.writeAndFlush(JSON.toJSONString(replyMsg));
                ClientChannelMap.add(userMark, channelId);
                //LOGGER.info("{}正常返回reply={}", channelId, 2);
            }
            //前端生成二维码
        } else if (type == 5) {
            ReplyMsg replyMsg = new ReplyMsg(2);
            //String id = RandomNum.randomStrCode(5);
            //long curTime = System.currentTimeMillis() / 1000;
            //channelId加密
            replyMsg.setChannelId(channelId);
            replyMsg.setReply(5);
            socketChannel.writeAndFlush(JSON.toJSONString(replyMsg));
            //被扫码
        } else if (type == 6) {
            LoginMsg loginMsg = JSON.parseObject(msg, LoginMsg.class);
            String token = loginMsg.getToken();
            String receiveChannelId = loginMsg.getChannelId();
            String userMark = loginMsg.getUserMark().toUpperCase();
            SocketChannel newChannel = NettyChannelMap.get(receiveChannelId);

            ReplyMsg replyMsg = new ReplyMsg(2);
            replyMsg.setReply(6);
            replyMsg.setToken(token);
            replyMsg.setUserMark(userMark);

            newChannel.writeAndFlush(JSON.toJSONString(replyMsg));

            String oldChannelId = ClientChannelMap.get(userMark);
            if(StringUtils.isNotBlank(userMark)){
                ClientChannelMap.remove(userMark);
            }
            if(StringUtils.isNotBlank(oldChannelId)){
                channelTokenMap.remove(oldChannelId);
            }
            //LOGGER.info("{}正常返回reply={}", 6);
        }
    }
}
