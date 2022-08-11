package com.ayuan.mewtwo;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * http服务端处理handler实现
 *
 * @author Ayuan
 */
@Slf4j
public class MewtwoHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        log.info("MewtwoHttpServerHandler receive fullHttpRequest=" + fullHttpRequest);

        String result = doHandle(fullHttpRequest);
        byte[] bytes = result.getBytes(StandardCharsets.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK, Unpooled.wrappedBuffer(bytes));
        response.headers().set("Content-Type", "text/html; charset=utf-8");
        response.headers().setInt("Content-Length", response.content().readableBytes());

        boolean keepAlive = HttpUtil.isKeepAlive(response);
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set("Connection", "keep-alive");
            ctx.write(response);
        }
    }

    private String doHandle(FullHttpRequest fullHttpRequest) {
        if (HttpMethod.GET.equals(fullHttpRequest.method())) {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(fullHttpRequest.uri());
            Map<String, List<String>> parameters = queryStringDecoder.parameters();
            return JSON.toJSONString(parameters);
        } else if (HttpMethod.POST.equals(fullHttpRequest.method())) {
            return fullHttpRequest.content().toString();
        }
        return "";
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("MewtwoHttpServerHandler exception,", cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
