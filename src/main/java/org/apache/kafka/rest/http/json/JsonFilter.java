/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.rest.http.json;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.util.CharsetUtil;

import org.apache.kafka.rest.validation.Validator;

public class JsonFilter extends SimpleChannelUpstreamHandler {

    private final Validator validator;

    public JsonFilter(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object msg = e.getMessage();
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest)e.getMessage();
            ChannelBuffer content = request.getContent();
            if (content.readable()) {
                if (!validator.isValidJson(content.toString(CharsetUtil.UTF_8))) {
                    throw new InvalidJsonException("Invalid JSON");
                }
            }
            Channels.fireMessageReceived(ctx, request, e.getRemoteAddress());
        } else {
            ctx.sendUpstream(e);
        }
    }

}
