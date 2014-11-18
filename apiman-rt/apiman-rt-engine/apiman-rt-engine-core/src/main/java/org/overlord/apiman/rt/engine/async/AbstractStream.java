/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.apiman.rt.engine.async;

import org.overlord.apiman.rt.engine.beans.ServiceRequest;
import org.overlord.apiman.rt.engine.io.IBuffer;
import org.overlord.apiman.rt.engine.io.IReadWriteStream;

/**
 * Generic representation of a three part stream: head, body and end, with
 * handlers and operators for each.
 *
 * For example: The head might represent a {@link ServiceRequest}; the body is a
 * stream of {@link IBuffer} chunks; {@link #end()} is used indicate that
 * transmission of the body has completed.
 *
 * The head handler is also used to indicate the result of the operation.
 *
 * @author Marc Savy <msavy@redhat.com>
 *
 * @param <H> Type of head
 */
public abstract class AbstractStream<H> implements IReadWriteStream<H> {

    protected IAsyncHandler<H> headHandler;
    protected IAsyncHandler<IBuffer> bodyHandler;
    protected IAsyncHandler<Void> endHandler;
    protected boolean finished = false;

    public void headHandler(IAsyncHandler<H> headHandler) {
        this.headHandler = headHandler;
    }

    @Override
    public void bodyHandler(IAsyncHandler<IBuffer> bodyHandler) {
        this.bodyHandler = bodyHandler;
    }

    @Override
    public void endHandler(IAsyncHandler<Void> endHandler) {
        this.endHandler = endHandler;
    }

    @Override
    public void write(IBuffer chunk) {
        if(bodyHandler != null)
            bodyHandler.handle(chunk);
    }

    @Override
    public void end() { 
        if(endHandler != null)
            endHandler.handle((Void) null);
        finished = true;
    }
    
    @Override
    public boolean isFinished() {
        return finished;
    }

    protected abstract void handleHead(H head);

    protected void handleBody(IBuffer chunk) {
        if(bodyHandler != null)
            bodyHandler.handle(chunk);
    }

    protected void handleEnd() {
        if(endHandler != null)
            endHandler.handle((Void) null);

        finished = true;
    }
}
