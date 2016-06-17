/*
 * Copyright 2015-2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package de.inventivegames.npc.channel;

import io.netty.channel.*;

import java.net.SocketAddress;

public class NPCChannel_1_10 extends AbstractChannel implements ControllableChannel {

	private final ChannelConfig config = new DefaultChannelConfig(this);

	public NPCChannel_1_10() {
		this(null);
	}

	public NPCChannel_1_10(Channel parent) {
		super(parent);
	}

	@Override
	public ChannelConfig config() {
		this.config.setAutoRead(true);
		return this.config;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	boolean open=false;

	@Override
	public void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public ChannelMetadata metadata() {
		return null;
	}

	@Override
	protected AbstractUnsafe newUnsafe() {
		return null;
	}

	@Override
	protected boolean isCompatible(EventLoop eventloop) {
		return false;
	}

	@Override
	protected SocketAddress localAddress0() {
		return null;
	}

	@Override
	protected SocketAddress remoteAddress0() {
		return null;
	}

	@Override
	protected void doBind(SocketAddress socketaddress) throws Exception {
	}

	@Override
	protected void doDisconnect() throws Exception {
	}

	@Override
	protected void doClose() throws Exception {
	}

	@Override
	protected void doBeginRead() throws Exception {
	}

	@Override
	protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
	}

}
