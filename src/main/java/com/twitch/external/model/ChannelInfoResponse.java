package com.twitch.external.model;

import com.twitch.model.ChannelInfo;
import java.util.List;

public record ChannelInfoResponse(List<ChannelInfo> data) {}

