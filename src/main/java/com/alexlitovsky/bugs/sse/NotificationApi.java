package com.alexlitovsky.bugs.sse;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

@Path("/notifications")
@ApplicationScoped
public class NotificationApi {
	
	@Context Sse sse;
	
	private SseBroadcaster broadcaster;
	
	@PostConstruct
	public void init() {
		
		broadcaster = sse.newBroadcaster();

		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
				() -> sendNotification(),
				1, 1, TimeUnit.SECONDS);
	}

	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void subscribe(
			@Context SseEventSink sseEventSink) {
		broadcaster.register(sseEventSink);
	}
	
	private void sendNotification() {
		OutboundSseEvent event = sse.newEvent(String.valueOf(System.currentTimeMillis()));
		broadcaster.broadcast(event);
	}
}
