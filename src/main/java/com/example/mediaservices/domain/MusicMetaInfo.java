package com.example.mediaservices.domain;

import lombok.Data;

/**
 * @author zcy
 * @create 2022/9/28 16:30
 * @description:
 */
@Data
public class MusicMetaInfo {

    private String format;

    private Long duration;

    private Integer bitRate;

    private Long sampleRate;

    private Long size;
}
