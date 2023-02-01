package com.avalon.packer.dto.channel;

import lombok.Data;

@Data
public class FindChannelDto {
    private String channelCode;
    private String channelName;
    private Boolean isMac;
}
