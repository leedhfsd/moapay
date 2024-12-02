package com.moa.moapay.global.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DutchRoomInfoEvent {
    private final UUID roomId;
}
